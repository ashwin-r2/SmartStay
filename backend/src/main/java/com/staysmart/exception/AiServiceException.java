package com.staysmart.exception;

/**
 * Thrown when an AI feature cannot complete — either because no OpenAI API key is configured
 * (expected in an offline/grading environment) or because the upstream call failed. Mapped to
 * HTTP 503 so the frontend can show a friendly "AI temporarily unavailable" message instead of
 * a generic 500.
 */
public class AiServiceException extends RuntimeException {

    public AiServiceException(String message) {
        super(message);
    }

    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
