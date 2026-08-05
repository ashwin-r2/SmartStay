package com.staysmart.admin.dto;

import java.math.BigDecimal;
import java.util.List;

public record BookingAnalyticsResponse(
        long totalBookings,
        long pending,
        long confirmed,
        long cancelled,
        long completed,
        BigDecimal totalRevenue,
        List<MonthlyStat> monthlyStats
) {
    public record MonthlyStat(String month, long bookingCount, BigDecimal revenue) {
    }
}
