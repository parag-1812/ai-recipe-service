package com.cloudkitchen.ai_recipe_service.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple in-memory fixed-window rate limiter: at most {@code maxPerMinute}
 * AI parses per user per minute. Keeps a public, paid AI endpoint from being
 * hammered (abuse or a runaway client) and caps Groq cost. Good enough for a
 * single instance; swap for Redis/bucket4j when horizontally scaled.
 */
@Component
public class RateLimiter {

    private static final int MAX_PER_MINUTE = 20;

    private record Window(long minute, AtomicInteger count) {
    }

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    /** @return true if the caller is within their quota (and counts this hit). */
    public boolean tryAcquire(String key) {
        long currentMinute = System.currentTimeMillis() / 60_000L;
        Window window = windows.compute(key, (k, existing) -> {
            if (existing == null || existing.minute() != currentMinute) {
                return new Window(currentMinute, new AtomicInteger(0));
            }
            return existing;
        });
        return window.count().incrementAndGet() <= MAX_PER_MINUTE;
    }
}
