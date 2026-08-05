package com.staysmart.exception;

/** Thrown for domain/business-rule violations that map to HTTP 400 (invalid dates, overlapping bookings, etc.). */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
