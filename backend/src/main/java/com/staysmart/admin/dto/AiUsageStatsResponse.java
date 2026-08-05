package com.staysmart.admin.dto;

import java.util.List;

public record AiUsageStatsResponse(
        long totalCalls,
        long successCount,
        long failureCount,
        List<FeatureStat> perFeature
) {
    public record FeatureStat(String feature, long totalCalls, long successCount, long failureCount, double avgLatencyMs) {
    }
}
