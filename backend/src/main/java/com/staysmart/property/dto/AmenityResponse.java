package com.staysmart.property.dto;

import com.staysmart.property.entity.Amenity;

public record AmenityResponse(Long id, String name, String icon) {
    public static AmenityResponse from(Amenity amenity) {
        return new AmenityResponse(amenity.getId(), amenity.getName(), amenity.getIcon());
    }
}
