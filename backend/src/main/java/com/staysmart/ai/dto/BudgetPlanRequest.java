package com.staysmart.ai.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BudgetPlanRequest(
        @NotBlank(message = "Destination is required")
        String destination,

        @NotNull @Positive
        Integer travelers,

        @NotNull @Positive
        Integer nights,

        @NotNull(message = "Total budget is required")
        @DecimalMin(value = "1.0", message = "Total budget must be greater than zero")
        BigDecimal totalBudget,

        String currency
) {
}
