package com.staysmart.config;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Wires up the LangChain4j {@link ChatLanguageModel} backed by whichever provider {@code
 * app.ai.provider} selects ("openai", "gemini", or "claude"; default "openai"). If the selected
 * provider's API key isn't configured (e.g. running the project for local grading/demo without billing), a
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
        String provider = appProperties.getAi().getProvider();
        if (provider == null || provider.isBlank()) {
            provider = "openai";
        }
        return switch (provider.toLowerCase()) {
            case "gemini" -> geminiModel();
            case "claude", "anthropic" -> claudeModel();
            case "openai" -> openAiModel();
            default -> {
                log.warn("Unknown app.ai.provider '{}' — falling back to a no-op AI client. "
                        + "Set AI_PROVIDER to 'openai', 'gemini', or 'claude'.", provider);
                yield new NoopChatLanguageModel();
            }
        };
    }

    private ChatLanguageModel openAiModel() {
        String apiKey = appProperties.getAi().getOpenai().getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI provider is 'openai' but OPENAI_API_KEY is not set — AI features will respond with a "
                    + "'not configured' message. Set the OPENAI_API_KEY environment variable to enable real AI responses.");
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

    private ChatLanguageModel geminiModel() {
        String apiKey = appProperties.getAi().getGemini().getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI provider is 'gemini' but GEMINI_API_KEY is not set — AI features will respond with a "
                    + "'not configured' message. Set the GEMINI_API_KEY environment variable to enable real AI responses.");
            return new NoopChatLanguageModel();
        }
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(appProperties.getAi().getGemini().getChatModel())
                .temperature(appProperties.getAi().getGemini().getTemperature())
                .timeout(Duration.ofSeconds(appProperties.getAi().getGemini().getTimeoutSeconds()))
                .build();
    }

    private ChatLanguageModel claudeModel() {
        String apiKey = appProperties.getAi().getClaude().getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI provider is 'claude' but ANTHROPIC_API_KEY is not set — AI features will respond with a "
                    + "'not configured' message. Set the ANTHROPIC_API_KEY environment variable to enable real AI responses.");
            return new NoopChatLanguageModel();
        }
        return AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName(appProperties.getAi().getClaude().getChatModel())
                .temperature(appProperties.getAi().getClaude().getTemperature())
                .timeout(Duration.ofSeconds(appProperties.getAi().getClaude().getTimeoutSeconds()))
                .logRequests(false)
                .logResponses(false)
                .build();
    }
}
