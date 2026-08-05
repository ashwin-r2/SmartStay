package com.staysmart.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record BookingRequest(
        @NotNull(message = "Property id is required")
        Long propertyId,

        @NotNull(message = "Check-in date is required")
        @FutureOrPresent(message = "Check-in date cannot be in the past")
        LocalDate checkIn,

        @NotNull(message = "Check-out date is required")
        @Future(message = "Check-out date must be in the future")
        LocalDate checkOut,

        @NotNull(message = "Number of guests is required")
        @Positive(message = "Number of guests must be greater than zero")
        Integer guestsCount
) {
}
