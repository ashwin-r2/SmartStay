package com.staysmart.admin.dto;

import java.math.BigDecimal;

public record DashboardStatsResponse(
        long totalUsers,
        long totalGuests,
        long totalHosts,
        long totalAdmins,
        long totalProperties,
        long activeProperties,
        long inactiveProperties,
        long totalBookings,
        long confirmedBookings,
        long cancelledBookings,
        long completedBookings,
        BigDecimal totalRevenue,
        long totalReviews
) {
}
