package com.staysmart.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Wires up the LangChain4j {@link ChatLanguageModel} backed by OpenAI. If no API key is
 * configured (e.g. running the project for local grading/demo without billing), a
 * {@link NoopChatLanguageModel} is registered instead so the application still starts cleanly;
 * the AI service layer detects this and returns a friendly "AI not configured" error rather than
 * crashing the whole request pipeline.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiConfig {

    private final AppProperties appProperties;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        String apiKey = appProperties.getAi().getOpenai().getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("OPENAI_API_KEY is not set — AI features will respond with a 'not configured' message. "
                    + "Set the OPENAI_API_KEY environment variable to enable real AI responses.");
            return new NoopChatLanguageModel();
        }
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(appProperties.getAi().getOpenai().getChatModel())
                .temperature(appProperties.getAi().getOpenai().getTemperature())
                .timeout(Duration.ofSeconds(appProperties.getAi().getOpenai().getTimeoutSeconds()))
                .logRequests(false)
                .logResponses(false)
                .build();
    }
}
