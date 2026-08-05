package com.staysmart.admin.service;

import com.staysmart.admin.dto.AiUsageStatsResponse;
import com.staysmart.admin.dto.BookingAnalyticsResponse;
import com.staysmart.admin.dto.DashboardStatsResponse;
import com.staysmart.ai.entity.AiFeature;
import com.staysmart.ai.repository.AiUsageLogRepository;
import com.staysmart.booking.entity.BookingStatus;
import com.staysmart.booking.service.BookingService;
import com.staysmart.property.entity.PropertyStatus;
import com.staysmart.property.service.PropertyService;
import com.staysmart.review.service.ReviewService;
import com.staysmart.user.entity.Role;
import com.staysmart.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Aggregates data already owned by each module's service into the read-models the admin
 * dashboard needs: platform totals, booking analytics and AI feature usage statistics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final PropertyService propertyService;
    private final BookingService bookingService;
    private final ReviewService reviewService;
    private final AiUsageLogRepository aiUsageLogRepository;

    public DashboardStatsResponse dashboardStats() {
        return new DashboardStatsResponse(
                userService.countAll(),
                userService.countByRole(Role.USER),
                userService.countByRole(Role.HOST),
                userService.countByRole(Role.ADMIN),
                propertyService.countAll(),
                propertyService.countByStatus(PropertyStatus.ACTIVE),
                propertyService.countByStatus(PropertyStatus.INACTIVE),
                bookingService.countAll(),
                bookingService.countByStatus(BookingStatus.CONFIRMED),
                bookingService.countByStatus(BookingStatus.CANCELLED),
                bookingService.countByStatus(BookingStatus.COMPLETED),
                bookingService.totalRevenue(),
                reviewService.countAll()
        );
    }

    public BookingAnalyticsResponse bookingAnalytics() {
        List<BookingAnalyticsResponse.MonthlyStat> monthly = safeMonthlyStats();
        return new BookingAnalyticsResponse(
                bookingService.countAll(),
                bookingService.countByStatus(BookingStatus.PENDING),
                bookingService.countByStatus(BookingStatus.CONFIRMED),
                bookingService.countByStatus(BookingStatus.CANCELLED),
                bookingService.countByStatus(BookingStatus.COMPLETED),
                bookingService.totalRevenue(),
                monthly
        );
    }

    public AiUsageStatsResponse aiUsageStats() {
        List<AiUsageStatsResponse.FeatureStat> perFeature = Arrays.stream(AiFeature.values())
                .map(feature -> new AiUsageStatsResponse.FeatureStat(
                        feature.name(),
                        aiUsageLogRepository.countByFeature(feature),
                        aiUsageLogRepository.countByFeatureAndSuccess(feature, true),
                        aiUsageLogRepository.countByFeatureAndSuccess(feature, false),
                        round(aiUsageLogRepository.averageLatencyMs(feature))
                ))
                .toList();

        long totalCalls = perFeature.stream().mapToLong(AiUsageStatsResponse.FeatureStat::totalCalls).sum();
        long successCount = aiUsageLogRepository.countBySuccess(true);
        long failureCount = aiUsageLogRepository.countBySuccess(false);

        return new AiUsageStatsResponse(totalCalls, successCount, failureCount, perFeature);
    }

    private double round(Double value) {
        if (value == null) return 0.0;
        return Math.round(value * 100.0) / 100.0;
    }

    private List<BookingAnalyticsResponse.MonthlyStat> safeMonthlyStats() {
        try {
            return bookingService.monthlyStatsRaw().stream()
                    .map(row -> new BookingAnalyticsResponse.MonthlyStat(
                            String.valueOf(row[0]),
                            ((Number) row[1]).longValue(),
                            (BigDecimal) row[2]))
                    .toList();
        } catch (Exception e) {
            log.debug("Monthly booking stats unavailable on this database (likely running on H2): {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
