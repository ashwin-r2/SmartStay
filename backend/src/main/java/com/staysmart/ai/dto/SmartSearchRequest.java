package com.staysmart.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record SmartSearchRequest(
        @NotBlank(message = "Search query is required")
        String query
) {
}
