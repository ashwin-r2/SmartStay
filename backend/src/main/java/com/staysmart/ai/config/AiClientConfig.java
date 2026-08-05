package com.staysmart.ai.config;

import com.staysmart.ai.client.BudgetPlannerAiClient;
import com.staysmart.ai.client.DescriptionGeneratorAiClient;
import com.staysmart.ai.client.RecommendationAiClient;
import com.staysmart.ai.client.ReviewSummaryAiClient;
import com.staysmart.ai.client.SmartSearchAiClient;
import com.staysmart.ai.client.TripPlannerAiClient;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires each LangChain4j declarative AI client interface to the shared {@link ChatLanguageModel}
 * bean (see {@code com.staysmart.config.AiConfig}). The chat assistant is handled separately with
 * manual multi-turn message construction and is not part of this declarative-client set.
 */
@Configuration
@RequiredArgsConstructor
public class AiClientConfig {

    private final ChatLanguageModel chatLanguageModel;

    @Bean
    public RecommendationAiClient recommendationAiClient() {
        return AiServices.create(RecommendationAiClient.class, chatLanguageModel);
    }

    @Bean
    public ReviewSummaryAiClient reviewSummaryAiClient() {
        return AiServices.create(ReviewSummaryAiClient.class, chatLanguageModel);
    }

    @Bean
    public TripPlannerAiClient tripPlannerAiClient() {
        return AiServices.create(TripPlannerAiClient.class, chatLanguageModel);
    }

    @Bean
    public DescriptionGeneratorAiClient descriptionGeneratorAiClient() {
        return AiServices.create(DescriptionGeneratorAiClient.class, chatLanguageModel);
    }

    @Bean
    public BudgetPlannerAiClient budgetPlannerAiClient() {
        return AiServices.create(BudgetPlannerAiClient.class, chatLanguageModel);
    }

    @Bean
    public SmartSearchAiClient smartSearchAiClient() {
        return AiServices.create(SmartSearchAiClient.class, chatLanguageModel);
    }
}
