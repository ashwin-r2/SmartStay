package com.staysmart.exception;

/** Thrown for authentication failures (bad credentials, expired/invalid token) — maps to HTTP 401. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
