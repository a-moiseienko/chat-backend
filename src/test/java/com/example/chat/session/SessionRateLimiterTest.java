package com.example.chat.session;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionRateLimiterTest {

    private final MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
    private final ChatSessionProperties properties =
            new ChatSessionProperties(Duration.ofMinutes(30), new ChatSessionProperties.RateLimit(3, Duration.ofMinutes(1)));
    private final SessionRateLimiter limiter = new SessionRateLimiter(properties, clock);

    @Test
    void allowsRequestsUpToTheLimit() {
        String sessionId = UUID.randomUUID().toString();
        assertThat(limiter.tryAcquire(sessionId)).isTrue();
        assertThat(limiter.tryAcquire(sessionId)).isTrue();
        assertThat(limiter.tryAcquire(sessionId)).isTrue();
    }

    @Test
    void rejectsRequestsBeyondTheLimitWithinTheWindow() {
        String sessionId = UUID.randomUUID().toString();
        limiter.tryAcquire(sessionId);
        limiter.tryAcquire(sessionId);
        limiter.tryAcquire(sessionId);

        assertThat(limiter.tryAcquire(sessionId)).isFalse();
    }

    @Test
    void resetsTheCountAfterTheWindowPasses() {
        String sessionId = UUID.randomUUID().toString();
        limiter.tryAcquire(sessionId);
        limiter.tryAcquire(sessionId);
        limiter.tryAcquire(sessionId);
        assertThat(limiter.tryAcquire(sessionId)).isFalse();

        clock.advance(Duration.ofMinutes(1).plusSeconds(1));

        assertThat(limiter.tryAcquire(sessionId)).isTrue();
    }

    @Test
    void tracksSessionsIndependently() {
        String first = UUID.randomUUID().toString();
        String second = UUID.randomUUID().toString();
        limiter.tryAcquire(first);
        limiter.tryAcquire(first);
        limiter.tryAcquire(first);
        assertThat(limiter.tryAcquire(first)).isFalse();

        assertThat(limiter.tryAcquire(second)).isTrue();
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        MutableClock(Instant instant) {
            this.instant = instant;
        }

        void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
