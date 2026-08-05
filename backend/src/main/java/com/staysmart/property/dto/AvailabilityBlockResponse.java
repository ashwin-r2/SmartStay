package com.staysmart.property.dto;

import com.staysmart.property.entity.AvailabilityBlock;

import java.time.LocalDate;

public record AvailabilityBlockResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        Long bookingId
) {
    public static AvailabilityBlockResponse from(AvailabilityBlock b) {
        return new AvailabilityBlockResponse(b.getId(), b.getStartDate(), b.getEndDate(), b.getReason().name(), b.getBookingId());
    }
}
