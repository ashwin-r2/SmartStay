package com.staysmart.exception;

/** Thrown for state conflicts such as double-booking a property or duplicate registration — maps to HTTP 409. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
