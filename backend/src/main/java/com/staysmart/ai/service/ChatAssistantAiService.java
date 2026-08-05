package com.staysmart.ai.service;

import com.staysmart.ai.dto.ChatMessageDto;
import com.staysmart.ai.dto.ChatRequest;
import com.staysmart.ai.dto.ChatResponse;
import com.staysmart.ai.entity.AiChatMessage;
import com.staysmart.ai.entity.AiFeature;
import com.staysmart.ai.entity.ChatRole;
import com.staysmart.ai.repository.AiChatMessageRepository;
import com.staysmart.exception.AiServiceException;
import com.staysmart.exception.ForbiddenException;
import com.staysmart.user.entity.User;
import com.staysmart.user.service.UserService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AI Chat Assistant: a multi-turn concierge that helps guests with platform questions
 * (booking policy, amenities, general travel advice). Unlike the other six AI features, this one
 * talks to {@link ChatLanguageModel} directly (rather than through a declarative LangChain4j
 * client) so it can replay persisted conversation history as proper message turns and capture
 * real token usage from the {@link Response} envelope.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAssistantAiService {

    private static final String SYSTEM_PROMPT = """
            You are the StaySmart AI concierge for a vacation rental platform. Help guests find
            properties, understand booking/cancellation policies, amenities, and give general,
            practical travel advice. Be friendly, concise (under 120 words unless asked for more
            detail), and say so plainly if something is outside what you can help with (e.g. you
            cannot make a booking on the guest's behalf — direct them to search/book in the app).
            """;

    private final ChatLanguageModel chatLanguageModel;
    private final AiChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final AiUsageLogger usageLogger;

    @Transactional
    public ChatResponse chat(Long userId, ChatRequest request) {
        String sessionId = (request.sessionId() != null && !request.sessionId().isBlank())
                ? request.sessionId() : UUID.randomUUID().toString();

        User user = userService.getEntityById(userId);
        List<AiChatMessage> history = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(dev.langchain4j.data.message.SystemMessage.from(SYSTEM_PROMPT));
        for (AiChatMessage m : history) {
            if (m.getRole() == ChatRole.USER) {
                messages.add(dev.langchain4j.data.message.UserMessage.from(m.getContent()));
            } else {
                messages.add(AiMessage.from(m.getContent()));
            }
        }
        messages.add(dev.langchain4j.data.message.UserMessage.from(request.message()));

        long start = System.currentTimeMillis();
        String replyText;
        try {
            Response<AiMessage> response = chatLanguageModel.generate(messages);
            replyText = response.content().text();
            Integer promptTokens = response.tokenUsage() != null ? response.tokenUsage().inputTokenCount() : null;
            Integer completionTokens = response.tokenUsage() != null ? response.tokenUsage().outputTokenCount() : null;
            usageLogger.logSuccess(AiFeature.CHAT_ASSISTANT, userId, promptTokens, completionTokens,
                    System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.warn("Chat assistant call failed for user {}: {}", userId, e.getMessage());
            usageLogger.logFailure(AiFeature.CHAT_ASSISTANT, userId, System.currentTimeMillis() - start, e.getMessage());
            String msg = e.getMessage() != null && e.getMessage().contains("OPENAI_API_KEY")
                    ? e.getMessage() : "The AI assistant is temporarily unavailable. Please try again shortly.";
            throw new AiServiceException(msg, e);
        }

        chatMessageRepository.save(AiChatMessage.builder().user(user).sessionId(sessionId)
                .role(ChatRole.USER).content(request.message()).build());
        chatMessageRepository.save(AiChatMessage.builder().user(user).sessionId(sessionId)
                .role(ChatRole.ASSISTANT).content(replyText).build());

        return new ChatResponse(sessionId, replyText);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> history(String sessionId, Long requesterId) {
        List<AiChatMessage> messages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        if (!messages.isEmpty() && !messages.get(0).getUser().getId().equals(requesterId)) {
            throw new ForbiddenException("You cannot view another user's chat session");
        }
        return messages.stream().map(ChatMessageDto::from).toList();
    }
}
