package com.staysmart.config;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Decorates a {@link ChatLanguageModel} with retries for transient "provider overloaded" errors
 * (HTTP 503 / UNAVAILABLE, e.g. Gemini's "model is currently experiencing high demand"). The
 * built-in {@code maxRetries} of the LangChain4j Gemini client only covers building the request,
 * not the HTTP response, so a 503 would otherwise fail the call immediately. Retries use
 * exponential backoff; any other error is rethrown at once.
 */
@Slf4j
public class RetryingChatLanguageModel implements ChatLanguageModel {

    private final ChatLanguageModel delegate;
    private final int maxRetries;
    private final long initialBackoffMs;

    public RetryingChatLanguageModel(ChatLanguageModel delegate, int maxRetries, long initialBackoffMs) {
        this.delegate = delegate;
        this.maxRetries = Math.max(0, maxRetries);
        this.initialBackoffMs = Math.max(0, initialBackoffMs);
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages) {
        return withRetry(() -> delegate.generate(messages));
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages, List<ToolSpecification> toolSpecifications) {
        return withRetry(() -> delegate.generate(messages, toolSpecifications));
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages, ToolSpecification toolSpecification) {
        return withRetry(() -> delegate.generate(messages, toolSpecification));
    }

    @Override
    public ChatResponse chat(ChatRequest chatRequest) {
        return withRetry(() -> delegate.chat(chatRequest));
    }

    @Override
    public Set<Capability> supportedCapabilities() {
        return delegate.supportedCapabilities();
    }

    private <T> T withRetry(Supplier<T> call) {
        long backoffMs = initialBackoffMs;
        for (int attempt = 0; ; attempt++) {
            try {
                return call.get();
            } catch (RuntimeException e) {
                if (attempt >= maxRetries || !isRetryable(e)) {
                    throw e;
                }
                log.warn("AI provider unavailable (attempt {}/{}), retrying in {} ms: {}",
                        attempt + 1, maxRetries + 1, backoffMs, rootMessage(e));
                sleep(backoffMs);
                backoffMs *= 2;
            }
        }
    }

    static boolean isRetryable(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            String msg = t.getMessage();
            if (msg != null && (msg.contains("(code 503)") || msg.contains("UNAVAILABLE")
                    || msg.contains("high demand") || msg.contains("overloaded"))) {
                return true;
            }
            if (t.getCause() == t) {
                break;
            }
        }
        return false;
    }

    private static String rootMessage(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t.getMessage();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting to retry AI call", ie);
        }
    }
}
