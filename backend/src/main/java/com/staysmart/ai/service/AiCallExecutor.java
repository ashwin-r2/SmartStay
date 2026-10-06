package com.staysmart.ai.service;

import com.staysmart.ai.entity.AiFeature;
import com.staysmart.exception.AiServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Shared "call the model, time it, log it, translate failures" wrapper used by every AI feature
 * service so each one only has to describe its own prompt inputs/outputs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiCallExecutor {

    private final AiUsageLogger usageLogger;

    public String call(AiFeature feature, Long userId, Supplier<String> aiCall) {
        long start = System.currentTimeMillis();
        try {
            String result = aiCall.get();
            usageLogger.logSuccess(feature, userId, null, null, System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - start;
            Throwable root = e;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }
            log.warn("AI call failed for feature {} (user={}): {} [root cause: {}: {}]", feature, userId,
                    e.getMessage(), root.getClass().getSimpleName(), root.getMessage());
            usageLogger.logFailure(feature, userId, latency, e.getMessage());
            throw new AiServiceException(friendlyMessage(e), e);
        }
    }

    private String friendlyMessage(Exception e) {
        String msg = e.getMessage();
        if (msg == null) {
            return "The AI service is temporarily unavailable. Please try again in a moment.";
        }
        if (msg.contains("OPENAI_API_KEY") || msg.contains("GEMINI_API_KEY") || msg.contains("ANTHROPIC_API_KEY")) {
            return msg;
        }
        if (msg.contains("401") || msg.contains("UNAUTHENTICATED") || msg.contains("Unauthorized")
                || msg.contains("invalid authentication credentials") || msg.contains("API key not valid")) {
            return "AI API key authentication failed (401 Unauthorized). Please check your API key in .env.";
        }
        if (msg.contains("429") || msg.contains("RESOURCE_EXHAUSTED") || msg.contains("quota") || msg.contains("Rate limit")) {
            return "AI provider rate limit or quota exceeded. Please check your provider account quota.";
        }
        return "The AI service is temporarily unavailable. Please try again in a moment.";
    }
}
