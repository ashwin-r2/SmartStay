package com.staysmart.review.dto;

import com.staysmart.review.entity.Review;

import java.time.Instant;
import java.util.List;

public record ReviewResponse(
        Long id,
        Long bookingId,
        Long propertyId,
        Long userId,
        String userName,
        String userAvatarUrl,
        int rating,
        String comment,
        List<String> imageUrls,
        Instant createdAt
) {
    public static ReviewResponse from(Review r) {
        return new ReviewResponse(
                r.getId(), r.getBooking().getId(), r.getProperty().getId(),
                r.getUser().getId(), r.getUser().getFullName(), r.getUser().getAvatarUrl(),
                r.getRating(), r.getComment(),
                r.getImages().stream().map(img -> img.getUrl()).toList(),
                r.getCreatedAt());
    }
}
