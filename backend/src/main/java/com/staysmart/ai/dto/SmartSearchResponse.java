package com.staysmart.ai.dto;

import com.staysmart.common.dto.PageResponse;
import com.staysmart.property.dto.PropertySearchCriteria;
import com.staysmart.property.dto.PropertySummaryResponse;

public record SmartSearchResponse(PropertySearchCriteria interpretedFilters, PageResponse<PropertySummaryResponse> results) {
}
