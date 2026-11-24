package com.example.chat.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which session ids have been seen recently, so an idle session is eventually forgotten.
 * The backend never rejects a well-formed, expired id outright: it is simply treated as new again.
 */
@Component
public class SessionStore {

    private final Map<String, Instant> lastSeenAt = new ConcurrentHashMap<>();
    private final ChatSessionProperties properties;
    private final Clock clock;

    @Autowired
    public SessionStore(ChatSessionProperties properties) {
        this(properties, Clock.systemUTC());
    }

    SessionStore(ChatSessionProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    /** Records activity for a well-formed session id. Returns false if the id is not a UUID. */
    public boolean touch(String sessionId) {
        if (!isWellFormed(sessionId)) {
            return false;
        }
        Instant now = clock.instant();
        evictExpired(now);
        lastSeenAt.put(sessionId, now);
        return true;
    }

    int trackedSessionCount() {
        return lastSeenAt.size();
    }

    private void evictExpired(Instant now) {
        Instant cutoff = now.minus(properties.inactivityTimeout());
        lastSeenAt.values().removeIf(seenAt -> seenAt.isBefore(cutoff));
    }

    private static boolean isWellFormed(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        try {
            UUID.fromString(sessionId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
