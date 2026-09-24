package com.staysmart.ai.controller;

import com.staysmart.ai.dto.BudgetPlanRequest;
import com.staysmart.ai.dto.BudgetPlanResponse;
import com.staysmart.ai.dto.ChatMessageDto;
import com.staysmart.ai.dto.ChatRequest;
import com.staysmart.ai.dto.ChatResponse;
import com.staysmart.ai.dto.DescriptionGenerateRequest;
import com.staysmart.ai.dto.DescriptionGenerateResponse;
import com.staysmart.ai.dto.RecommendationResponse;
import com.staysmart.ai.dto.ReviewSummaryResponse;
import com.staysmart.ai.dto.SmartSearchRequest;
import com.staysmart.ai.dto.SmartSearchResponse;
import com.staysmart.ai.dto.TripPlanRequest;
import com.staysmart.ai.dto.TripPlanResponse;
import com.staysmart.ai.service.BudgetPlannerAiService;
import com.staysmart.ai.service.ChatAssistantAiService;
import com.staysmart.ai.service.DescriptionGeneratorAiService;
import com.staysmart.ai.service.RecommendationAiService;
import com.staysmart.ai.service.ReviewSummaryAiService;
import com.staysmart.ai.service.SmartSearchAiService;
import com.staysmart.ai.service.TripPlannerAiService;
import com.staysmart.common.dto.ApiResponse;
import com.staysmart.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Every AI feature in one place: recommendations, review summarization, chat assistant, trip
 * planner, description generator, budget planner and smart search. Each call is authenticated
 * (usage is tracked per user) and logged to {@code ai_usage_logs} for the admin dashboard.
 */
@Tag(name = "AI Features", description = "Recommendations, review summarization, chat assistant, trip/budget planning, description generation, smart search")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final RecommendationAiService recommendationAiService;
    private final ReviewSummaryAiService reviewSummaryAiService;
    private final ChatAssistantAiService chatAssistantAiService;
    private final TripPlannerAiService tripPlannerAiService;
    private final DescriptionGeneratorAiService descriptionGeneratorAiService;
    private final BudgetPlannerAiService budgetPlannerAiService;
    private final SmartSearchAiService smartSearchAiService;

    @Operation(summary = "Get AI-curated property recommendations for the current user")
    @GetMapping("/recommendations")
    public ApiResponse<RecommendationResponse> recommendations(@AuthenticationPrincipal User currentUser,
                                                                 @RequestParam(required = false) String city) {
        return ApiResponse.ok(recommendationAiService.recommend(currentUser.getId(), city));
    }

    @Operation(summary = "Get an AI-generated summary of a property's reviews (works for guests too)")
    @GetMapping("/reviews/{propertyId}/summary")
    public ApiResponse<ReviewSummaryResponse> reviewSummary(@PathVariable Long propertyId,
                                                              @AuthenticationPrincipal User currentUser) {
        Long userId = currentUser != null ? currentUser.getId() : null;
        return ApiResponse.ok(reviewSummaryAiService.summarize(propertyId, userId));
    }

    @Operation(summary = "Send a message to the AI chat assistant (creates a new session if sessionId is omitted)")
    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@AuthenticationPrincipal User currentUser,
                                           @Valid @RequestBody ChatRequest request) {
        return ApiResponse.ok(chatAssistantAiService.chat(currentUser.getId(), request));
    }

    @Operation(summary = "Get the message history for a chat session")
    @GetMapping("/chat/{sessionId}/history")
    public ApiResponse<List<ChatMessageDto>> chatHistory(@PathVariable String sessionId,
                                                           @AuthenticationPrincipal User currentUser) {
        return ApiResponse.ok(chatAssistantAiService.history(sessionId, currentUser.getId()));
    }

    @Operation(summary = "Generate a day-by-day AI trip itinerary")
    @PostMapping("/trip-planner")
    public ApiResponse<TripPlanResponse> tripPlan(@AuthenticationPrincipal User currentUser,
                                                   @Valid @RequestBody TripPlanRequest request) {
        return ApiResponse.ok(tripPlannerAiService.plan(currentUser.getId(), request));
    }

    @Operation(summary = "Generate an AI property listing description (optionally apply it directly to a property you own)")
    @PostMapping("/description-generator")
    public ApiResponse<DescriptionGenerateResponse> generateDescription(@AuthenticationPrincipal User currentUser,
                                                                          @Valid @RequestBody DescriptionGenerateRequest request) {
        return ApiResponse.ok(descriptionGeneratorAiService.generate(currentUser.getId(), request));
    }

    @Operation(summary = "Generate an AI trip budget breakdown")
    @PostMapping("/budget-planner")
    public ApiResponse<BudgetPlanResponse> budgetPlan(@AuthenticationPrincipal User currentUser,
                                                       @Valid @RequestBody BudgetPlanRequest request) {
        return ApiResponse.ok(budgetPlannerAiService.plan(currentUser.getId(), request));
    }

    @Operation(summary = "Search properties using a natural-language query (works for guests too)")
    @PostMapping("/smart-search")
    public ApiResponse<SmartSearchResponse> smartSearch(@AuthenticationPrincipal User currentUser,
                                                          @Valid @RequestBody SmartSearchRequest request,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        Long userId = currentUser != null ? currentUser.getId() : null;
        return ApiResponse.ok(smartSearchAiService.search(userId, request.query(), pageable));
    }
}
