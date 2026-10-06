package com.staysmart.property.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Search filters shared by the regular search endpoint and the AI Smart Search feature (which
 * parses a natural-language query into this same structure before delegating to
 * {@code PropertyService.search}).
 */
public record PropertySearchCriteria(
        String city,
        String state,
        String country,
        LocalDate checkIn,
        LocalDate checkOut,
        Integer guests,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String propertyType,
        String roomType,
        List<Long> amenityIds,
        String keyword
) {
    public static PropertySearchCriteria empty() {
        return new PropertySearchCriteria(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public PropertySearchCriteria withoutKeyword() {
        return new PropertySearchCriteria(city, state, country, checkIn, checkOut, guests, minPrice, maxPrice,
                propertyType, roomType, amenityIds, null);
    }

    /** True if any filter other than {@code keyword} is set. */
    public boolean hasNonKeywordFilters() {
        return !withoutKeyword().equals(empty());
    }
}
