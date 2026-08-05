package com.staysmart.ai.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record TripPlanRequest(
        @NotBlank(message = "Destination is required")
        String destination,

        @NotNull(message = "Start date is required")
        @FutureOrPresent(message = "Start date cannot be in the past")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @NotNull(message = "Number of travelers is required")
        @Positive
        Integer travelers,

        String interests,

        /** e.g. "budget", "mid-range", "luxury" */
        String budgetLevel
) {
}
