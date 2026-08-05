package com.staysmart.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
        String fullName,

        String phone,

        String avatarUrl,

        @Size(max = 1000, message = "Bio must be at most 1000 characters")
        String bio
) {
}
