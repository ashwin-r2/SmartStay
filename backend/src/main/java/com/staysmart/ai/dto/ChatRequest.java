package com.staysmart.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        /** Optional; a new session id is generated when omitted (start of a new conversation). */
        String sessionId,

        @NotBlank(message = "Message cannot be empty")
        String message
) {
}
