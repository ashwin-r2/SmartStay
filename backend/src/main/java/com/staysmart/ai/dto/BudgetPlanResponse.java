package com.staysmart.ai.dto;

import java.math.BigDecimal;

public record BudgetPlanResponse(String destination, BigDecimal avgNightlyPriceReference, String plan) {
}
