package com.staysmart.ai.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staysmart.ai.client.SmartSearchAiClient;
import com.staysmart.ai.dto.SmartSearchResponse;
import com.staysmart.ai.entity.AiFeature;
import com.staysmart.common.dto.PageResponse;
import com.staysmart.property.dto.PropertySearchCriteria;
import com.staysmart.property.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * AI Smart Search: turns a free-text query ("cozy beach house in Goa under 5000 for 2 guests
 * next weekend") into structured {@link PropertySearchCriteria}, then reuses the exact same
 * {@code PropertyService.search} path as the regular filter-based search endpoint.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmartSearchAiService {

    private final SmartSearchAiClient client;
    private final PropertyService propertyService;
    private final AiCallExecutor aiCallExecutor;
    private final ObjectMapper objectMapper;

    public SmartSearchResponse search(Long userId, String query, Pageable pageable) {
        String today = LocalDate.now().toString();
        String rawJson = aiCallExecutor.call(AiFeature.SMART_SEARCH, userId, () -> client.parseQuery(query, today));

        PropertySearchCriteria criteria = parseOrFallback(rawJson, query);
        PageResponse<com.staysmart.property.dto.PropertySummaryResponse> results =
                PageResponse.from(propertyService.search(criteria, pageable));

        return new SmartSearchResponse(criteria, results);
    }

    private PropertySearchCriteria parseOrFallback(String rawJson, String originalQuery) {
        try {
            String cleaned = rawJson.trim()
                    .replaceAll("^```(json)?", "")
                    .replaceAll("```$", "")
                    .trim();
            ParsedFilters parsed = objectMapper.readValue(cleaned, ParsedFilters.class);
            return new PropertySearchCriteria(
                    blankToNull(parsed.city()),
                    blankToNull(parsed.country()),
                    parseDate(parsed.checkIn()),
                    parseDate(parsed.checkOut()),
                    parsed.guests(),
                    parsed.minPrice(),
                    parsed.maxPrice(),
                    blankToNull(parsed.propertyType()),
                    blankToNull(parsed.roomType()),
                    null,
                    blankToNull(parsed.keyword()));
        } catch (Exception e) {
            log.warn("Could not parse Smart Search AI output as JSON, falling back to keyword search: {}", e.getMessage());
            return new PropertySearchCriteria(null, null, null, null, null, null, null, null, null, null, originalQuery);
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank() || "null".equalsIgnoreCase(value)) ? null : value.trim();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ParsedFilters(
            String city,
            String country,
            String checkIn,
            String checkOut,
            Integer guests,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String propertyType,
            String roomType,
            String keyword
    ) {
    }
}
