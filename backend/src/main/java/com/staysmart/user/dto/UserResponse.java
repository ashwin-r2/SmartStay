package com.staysmart.user.dto;

import com.staysmart.user.entity.Role;
import com.staysmart.user.entity.User;

import java.time.Instant;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        Role role,
        String avatarUrl,
        String bio,
        boolean enabled,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(), user.getFullName(), user.getEmail(), user.getPhone(),
                user.getRole(), user.getAvatarUrl(), user.getBio(), user.isEnabled(), user.getCreatedAt());
    }
}
