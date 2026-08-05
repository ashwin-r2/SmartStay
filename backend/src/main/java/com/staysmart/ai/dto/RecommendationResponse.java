package com.staysmart.ai.dto;

import com.staysmart.property.dto.PropertySummaryResponse;

import java.util.List;

public record RecommendationResponse(String summary, List<PropertySummaryResponse> properties) {
}
