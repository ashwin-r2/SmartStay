package com.staysmart.property.dto;

import com.staysmart.property.entity.Property;
import com.staysmart.property.entity.PropertyType;
import com.staysmart.property.entity.RoomType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public record PropertyResponse(
        Long id,
        HostSummary host,
        String title,
        String description,
        PropertyType propertyType,
        RoomType roomType,
        String addressLine,
        String city,
        String state,
        String country,
        String zipCode,
        Double latitude,
        Double longitude,
        BigDecimal pricePerNight,
        BigDecimal cleaningFee,
        int maxGuests,
        int bedrooms,
        int beds,
        BigDecimal bathrooms,
        BigDecimal avgRating,
        int reviewCount,
        String status,
        boolean aiGeneratedDescription,
        List<PropertyImageResponse> images,
        List<AmenityResponse> amenities,
        Instant createdAt
) {
    public record HostSummary(Long id, String fullName, String avatarUrl) {
    }

    public static PropertyResponse from(Property p) {
        return new PropertyResponse(
                p.getId(),
                new HostSummary(p.getHost().getId(), p.getHost().getFullName(), p.getHost().getAvatarUrl()),
                p.getTitle(), p.getDescription(), p.getPropertyType(), p.getRoomType(),
                p.getAddressLine(), p.getCity(), p.getState(), p.getCountry(), p.getZipCode(),
                p.getLatitude(), p.getLongitude(), p.getPricePerNight(), p.getCleaningFee(),
                p.getMaxGuests(), p.getBedrooms(), p.getBeds(), p.getBathrooms(),
                p.getAvgRating(), p.getReviewCount(), p.getStatus().name(), p.isAiGeneratedDescription(),
                p.getImages().stream()
                        .sorted(Comparator.comparingInt(com.staysmart.property.entity.PropertyImage::getDisplayOrder))
                        .map(PropertyImageResponse::from).toList(),
                p.getAmenities().stream().map(AmenityResponse::from).toList(),
                p.getCreatedAt()
        );
    }
}
