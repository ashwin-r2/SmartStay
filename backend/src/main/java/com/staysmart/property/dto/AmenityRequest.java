package com.staysmart.property.dto;

import jakarta.validation.constraints.NotBlank;

public record AmenityRequest(
        @NotBlank(message = "Amenity name is required") String name,
        String icon
) {
}
