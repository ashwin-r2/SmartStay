package com.staysmart.ai.client;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/** LangChain4j declarative AI service for the "AI Budget Planner" feature. */
public interface BudgetPlannerAiClient {

    @SystemMessage("""
            You are a practical travel budgeting assistant. Give realistic, well-reasoned
            allocations that sum to (approximately) the traveler's total budget.
            """)
    @UserMessage("""
            Build a budget plan for this trip:
            Destination: {{destination}}
            Travelers: {{travelers}}
            Nights: {{nights}}
            Total budget: {{totalBudget}} {{currency}}
            Typical accommodation price/night on our platform for this destination: {{avgNightlyPrice}} {{currency}}

            Respond with a breakdown across these categories: Accommodation, Food, Transport,
            Activities, Miscellaneous/Buffer — each as an amount and short justification — followed
            by 2-3 concise money-saving tips.
            """)
    String planBudget(@V("destination") String destination, @V("travelers") int travelers,
                       @V("nights") int nights, @V("totalBudget") String totalBudget,
                       @V("currency") String currency, @V("avgNightlyPrice") String avgNightlyPrice);
}
