package com.staysmart.property.dto;

import com.staysmart.property.entity.PropertyImage;

public record PropertyImageResponse(Long id, String url, int displayOrder) {
    public static PropertyImageResponse from(PropertyImage image) {
        return new PropertyImageResponse(image.getId(), image.getUrl(), image.getDisplayOrder());
    }
}
