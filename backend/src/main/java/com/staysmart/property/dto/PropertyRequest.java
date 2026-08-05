package com.staysmart.property.dto;

import com.staysmart.property.entity.PropertyType;
import com.staysmart.property.entity.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record PropertyRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must be at most 150 characters")
        String title,

        @Size(max = 10000, message = "Description is too long")
        String description,

        @NotNull(message = "Property type is required")
        PropertyType propertyType,

        @NotNull(message = "Room type is required")
        RoomType roomType,

        String addressLine,

        @NotBlank(message = "City is required")
        String city,

        String state,

        @NotBlank(message = "Country is required")
        String country,

        String zipCode,

        Double latitude,

        Double longitude,

        @NotNull(message = "Price per night is required")
        @DecimalMin(value = "0.0", message = "Price per night must not be negative")
        BigDecimal pricePerNight,

        @DecimalMin(value = "0.0", message = "Cleaning fee must not be negative")
        BigDecimal cleaningFee,

        @NotNull(message = "Max guests is required")
        @Positive(message = "Max guests must be greater than zero")
        Integer maxGuests,

        @Min(0) Integer bedrooms,

        @Min(0) Integer beds,

        @DecimalMin(value = "0.0", message = "Bathrooms must not be negative")
        BigDecimal bathrooms,

        List<Long> amenityIds
) {
}
