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
            log.warn("AI call failed for feature {} (user={}): {}", feature, userId, e.getMessage());
            usageLogger.logFailure(feature, userId, latency, e.getMessage());
            throw new AiServiceException(friendlyMessage(e), e);
        }
    }

    private String friendlyMessage(Exception e) {
        String msg = e.getMessage();
        if (msg != null && msg.contains("OPENAI_API_KEY")) {
            return msg;
        }
        return "The AI service is temporarily unavailable. Please try again in a moment.";
    }
}
