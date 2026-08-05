package com.staysmart.ai.service;

import com.staysmart.ai.client.BudgetPlannerAiClient;
import com.staysmart.ai.dto.BudgetPlanRequest;
import com.staysmart.ai.dto.BudgetPlanResponse;
import com.staysmart.ai.entity.AiFeature;
import com.staysmart.property.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * AI Budget Planner: allocates a traveler's total budget across categories, grounded in the
 * platform's actual average nightly price for the destination city where available.
 */
@Service
@RequiredArgsConstructor
public class BudgetPlannerAiService {

    private final BudgetPlannerAiClient client;
    private final PropertyService propertyService;
    private final AiCallExecutor aiCallExecutor;

    public BudgetPlanResponse plan(Long userId, BudgetPlanRequest request) {
        String currency = (request.currency() == null || request.currency().isBlank()) ? "USD" : request.currency();
        BigDecimal avgNightly = propertyService.averagePriceForCity(request.destination());
        String avgNightlyDisplay = (avgNightly != null && avgNightly.signum() > 0) ? avgNightly.toString() : "not available";

        String plan = aiCallExecutor.call(AiFeature.BUDGET_PLANNER, userId, () -> client.planBudget(
                request.destination(), request.travelers(), request.nights(),
                request.totalBudget().toString(), currency, avgNightlyDisplay));

        return new BudgetPlanResponse(request.destination(), avgNightly, plan);
    }
}
