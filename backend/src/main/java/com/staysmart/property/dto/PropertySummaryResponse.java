package com.staysmart.property.dto;

import com.staysmart.property.entity.Property;
import com.staysmart.property.entity.PropertyType;

import java.math.BigDecimal;

/** Lightweight projection used in search results, listings, host dashboard and AI recommendations. */
public record PropertySummaryResponse(
        Long id,
        String title,
        PropertyType propertyType,
        String city,
        String country,
        BigDecimal pricePerNight,
        int maxGuests,
        BigDecimal avgRating,
        int reviewCount,
        String status,
        String coverImageUrl
) {
    public static PropertySummaryResponse from(Property p) {
        String cover = p.getImages().stream().findFirst().map(img -> img.getUrl()).orElse(null);
        return new PropertySummaryResponse(
                p.getId(), p.getTitle(), p.getPropertyType(), p.getCity(), p.getCountry(),
                p.getPricePerNight(), p.getMaxGuests(), p.getAvgRating(), p.getReviewCount(),
                p.getStatus().name(), cover);
    }
}
