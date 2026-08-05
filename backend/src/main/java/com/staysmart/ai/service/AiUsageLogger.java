package com.staysmart.ai.service;

import com.staysmart.ai.entity.AiFeature;
import com.staysmart.ai.entity.AiUsageLog;
import com.staysmart.ai.repository.AiUsageLogRepository;
import com.staysmart.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Records every AI feature invocation for the admin "AI usage statistics" dashboard. Uses
 * {@code REQUIRES_NEW} so a logging write always commits even if the surrounding request later
 * rolls back for an unrelated reason.
 */
@Service
@RequiredArgsConstructor
public class AiUsageLogger {

    private final AiUsageLogRepository aiUsageLogRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSuccess(AiFeature feature, Long userId, Integer promptTokens, Integer completionTokens, long latencyMs) {
        AiUsageLog log = AiUsageLog.builder()
                .user(userId != null ? userRepository.getReferenceById(userId) : null)
                .feature(feature)
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .latencyMs(latencyMs)
                .success(true)
                .build();
        aiUsageLogRepository.save(log);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(AiFeature feature, Long userId, long latencyMs, String errorMessage) {
        AiUsageLog log = AiUsageLog.builder()
                .user(userId != null ? userRepository.getReferenceById(userId) : null)
                .feature(feature)
                .latencyMs(latencyMs)
                .success(false)
                .errorMessage(errorMessage != null && errorMessage.length() > 500 ? errorMessage.substring(0, 500) : errorMessage)
                .build();
        aiUsageLogRepository.save(log);
    }
}
