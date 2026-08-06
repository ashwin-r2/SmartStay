package com.staysmart.ai.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staysmart.ai.client.SmartSearchAiClient;
import com.staysmart.ai.dto.SmartSearchResponse;
import com.staysmart.ai.entity.AiFeature;
import com.staysmart.common.dto.PageResponse;
import com.staysmart.property.dto.PropertySearchCriteria;
import com.staysmart.property.entity.Amenity;
import com.staysmart.property.repository.AmenityRepository;
import com.staysmart.property.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    private final AmenityRepository amenityRepository;
    private final AiCallExecutor aiCallExecutor;
    private final ObjectMapper objectMapper;

    public SmartSearchResponse search(Long userId, String query, Pageable pageable) {
        String today = LocalDate.now().toString();
        List<Amenity> allAmenities = amenityRepository.findAll();
        String amenityCatalog = allAmenities.stream().map(Amenity::getName).collect(Collectors.joining(", "));
        String rawJson = aiCallExecutor.call(AiFeature.SMART_SEARCH, userId,
                () -> client.parseQuery(query, today, amenityCatalog));

        PropertySearchCriteria criteria = parseOrFallback(rawJson, query, allAmenities);
        PageResponse<com.staysmart.property.dto.PropertySummaryResponse> results =
                PageResponse.from(propertyService.search(criteria, pageable));

        return new SmartSearchResponse(criteria, results);
    }

    private PropertySearchCriteria parseOrFallback(String rawJson, String originalQuery, List<Amenity> allAmenities) {
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
                    resolveAmenityIds(parsed.amenities(), allAmenities),
                    blankToNull(parsed.keyword()));
        } catch (Exception e) {
            log.warn("Could not parse Smart Search AI output as JSON, falling back to keyword search: {}", e.getMessage());
            return new PropertySearchCriteria(null, null, null, null, null, null, null, null, null, null, originalQuery);
        }
    }

    /** Matches the AI-extracted amenity names back to their DB ids, case-insensitively; unknown names are dropped. */
    private List<Long> resolveAmenityIds(List<String> amenityNames, List<Amenity> allAmenities) {
        if (amenityNames == null || amenityNames.isEmpty()) {
            return null;
        }
        List<Long> ids = amenityNames.stream()
                .filter(name -> name != null && !name.isBlank())
                .flatMap(name -> allAmenities.stream().filter(a -> a.getName().equalsIgnoreCase(name.trim())))
                .map(Amenity::getId)
                .distinct()
                .toList();
        return ids.isEmpty() ? null : ids;
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
            List<String> amenities,
            String keyword
    ) {
    }
}
