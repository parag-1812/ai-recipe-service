package com.cloudkitchen.ai_recipe_service.exception;

/** Thrown when a user exceeds their AI-parse quota for the current window. */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
