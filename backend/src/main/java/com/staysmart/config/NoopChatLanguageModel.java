package com.staysmart.config;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;

import java.util.List;

/**
 * Fallback {@link ChatLanguageModel} used when no OpenAI API key is configured. Every call fails
 * fast with a clear message; {@code com.staysmart.ai.service.*} classes catch this and translate
 * it into a {@link com.staysmart.exception.AiServiceException} so the REST layer returns a clean
 * HTTP 503 instead of the app crashing or hanging on a real network call.
 */
public class NoopChatLanguageModel implements ChatLanguageModel {

    private final String message;

    public NoopChatLanguageModel() {
        this("AI API key is not configured on the server. Set OPENAI_API_KEY or GEMINI_API_KEY in .env to enable AI features.");
    }

    public NoopChatLanguageModel(String message) {
        this.message = message;
    }

    @Override
    public dev.langchain4j.model.output.Response<dev.langchain4j.data.message.AiMessage> generate(List<ChatMessage> messages) {
        throw new IllegalStateException(message);
    }
}
