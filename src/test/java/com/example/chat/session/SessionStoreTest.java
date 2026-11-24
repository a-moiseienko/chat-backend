package com.example.chat.session;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionStoreTest {

    private final MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
    private final ChatSessionProperties properties =
            new ChatSessionProperties(Duration.ofMinutes(30), new ChatSessionProperties.RateLimit(30, Duration.ofMinutes(1)));
    private final SessionStore store = new SessionStore(properties, clock);

    @Test
    void acceptsAWellFormedSessionId() {
        assertThat(store.touch(UUID.randomUUID().toString())).isTrue();
    }

    @Test
    void rejectsAMalformedSessionId() {
        assertThat(store.touch("not-a-uuid")).isFalse();
    }

    @Test
    void rejectsAMissingSessionId() {
        assertThat(store.touch(null)).isFalse();
    }

    @Test
    void evictsASessionThatHasBeenIdleLongerThanTheTimeout() {
        store.touch(UUID.randomUUID().toString());
        assertThat(store.trackedSessionCount()).isEqualTo(1);

        clock.advance(Duration.ofMinutes(31));
        store.touch(UUID.randomUUID().toString());

        assertThat(store.trackedSessionCount()).isEqualTo(1);
    }

    @Test
    void keepsASessionThatIsStillWithinTheTimeout() {
        store.touch(UUID.randomUUID().toString());

        clock.advance(Duration.ofMinutes(29));
        store.touch(UUID.randomUUID().toString());

        assertThat(store.trackedSessionCount()).isEqualTo(2);
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
