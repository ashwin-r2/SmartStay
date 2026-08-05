package com.staysmart.ai.dto;

import com.staysmart.property.entity.PropertyType;
import com.staysmart.property.entity.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record DescriptionGenerateRequest(
        @NotBlank String title,
        @NotNull PropertyType propertyType,
        @NotNull RoomType roomType,
        @NotBlank String city,
        @NotBlank String country,
        @Positive int bedrooms,
        @Positive int beds,
        BigDecimal bathrooms,
        @Positive int maxGuests,
        List<String> amenities,
        BigDecimal pricePerNight,

        /** Optional: when set and the requester owns the property, the generated text is saved directly. */
        Long applyToPropertyId
) {
}
