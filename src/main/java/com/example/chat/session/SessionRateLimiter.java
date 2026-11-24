package com.example.chat.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Fixed-window request limiter per session id, so a leaked or brute-forced key cannot be hammered. */
@Component
public class SessionRateLimiter {

    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final ChatSessionProperties properties;
    private final Clock clock;

    @Autowired
    public SessionRateLimiter(ChatSessionProperties properties) {
        this(properties, Clock.systemUTC());
    }

    SessionRateLimiter(ChatSessionProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    /** Returns true if this request is allowed under the session's current window, consuming one slot. */
    public boolean tryAcquire(String sessionId) {
        int maxRequests = properties.rateLimit().maxRequests();
        Instant now = clock.instant();

        Window result = windows.compute(sessionId, (id, current) -> {
            if (current == null || current.isExpired(now, properties.rateLimit().window())) {
                return new Window(now, 1);
            }
            return current.incremented();
        });

        return result.count() <= maxRequests;
    }

    private record Window(Instant startedAt, int count) {

        boolean isExpired(Instant now, java.time.Duration window) {
            return startedAt.plus(window).isBefore(now);
        }

        Window incremented() {
            return new Window(startedAt, count + 1);
        }
    }
}
