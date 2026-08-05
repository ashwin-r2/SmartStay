package com.staysmart.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staysmart.ai.client.RecommendationAiClient;
import com.staysmart.ai.dto.RecommendationResponse;
import com.staysmart.ai.entity.AiFeature;
import com.staysmart.booking.dto.BookingResponse;
import com.staysmart.common.dto.PageResponse;
import com.staysmart.property.dto.PropertySummaryResponse;
import com.staysmart.property.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI Property Recommendation: builds a candidate pool from the platform's own top-rated
 * properties (optionally scoped to a guest's inferred preferred city from booking history),
 * then asks the LLM to explain why those candidates suit the guest, in a short paragraph.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationAiService {

    private final RecommendationAiClient client;
    private final PropertyService propertyService;
    private final com.staysmart.booking.service.BookingService bookingService;
    private final AiCallExecutor aiCallExecutor;
    private final ObjectMapper objectMapper;

    public RecommendationResponse recommend(Long userId, String preferredCity) {
        String city = preferredCity != null && !preferredCity.isBlank() ? preferredCity : inferPreferredCity(userId);

        List<PropertySummaryResponse> candidates = city != null
                ? propertyService.topRatedInCity(city, 8)
                : propertyService.listActiveSample(8);

        if (candidates.isEmpty()) {
            candidates = propertyService.listActiveSample(8);
        }

        String profile = city != null
                ? "Guest interested in staying in " + city + "."
                : "Guest has not indicated a destination preference yet; suggest a broad, appealing mix.";

        String candidatesJson = toJson(candidates);
        String summary = aiCallExecutor.call(AiFeature.RECOMMENDATION, userId,
                () -> client.recommend(profile, candidatesJson));

        return new RecommendationResponse(summary, candidates);
    }

    private String inferPreferredCity(Long userId) {
        try {
            PageResponse<BookingResponse> recent = PageResponse.from(
                    bookingService.listByGuest(userId, PageRequest.of(0, 5)));
            return recent.content().stream()
                    .findFirst()
                    .map(BookingResponse::propertyCity)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private String toJson(List<PropertySummaryResponse> candidates) {
        try {
            return objectMapper.writeValueAsString(candidates);
        } catch (Exception e) {
            return "[]";
        }
    }
}
