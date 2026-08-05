package com.staysmart.booking.dto;

import com.staysmart.booking.entity.Booking;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record BookingResponse(
        Long id,
        Long propertyId,
        String propertyTitle,
        String propertyCity,
        String propertyCoverImageUrl,
        Long guestId,
        String guestName,
        LocalDate checkIn,
        LocalDate checkOut,
        int guestsCount,
        int nights,
        BigDecimal pricePerNight,
        BigDecimal cleaningFee,
        BigDecimal totalPrice,
        String status,
        String cancellationReason,
        Instant cancelledAt,
        Instant createdAt
) {
    public static BookingResponse from(Booking b) {
        String cover = b.getProperty().getImages().stream().findFirst()
                .map(img -> img.getUrl()).orElse(null);
        return new BookingResponse(
                b.getId(), b.getProperty().getId(), b.getProperty().getTitle(), b.getProperty().getCity(), cover,
                b.getGuest().getId(), b.getGuest().getFullName(),
                b.getCheckIn(), b.getCheckOut(), b.getGuestsCount(), b.getNights(),
                b.getPricePerNight(), b.getCleaningFee(), b.getTotalPrice(),
                b.getStatus().name(), b.getCancellationReason(), b.getCancelledAt(), b.getCreatedAt());
    }
}
