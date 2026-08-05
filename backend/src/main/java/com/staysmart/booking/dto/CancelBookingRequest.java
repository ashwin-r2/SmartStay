package com.staysmart.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelBookingRequest(
        @NotBlank(message = "A cancellation reason is required")
        @Size(max = 500)
        String reason
) {
}
