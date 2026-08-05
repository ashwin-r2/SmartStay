package com.staysmart.exception;

/** Thrown when an authenticated user acts outside their role/ownership permissions — maps to HTTP 403. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
