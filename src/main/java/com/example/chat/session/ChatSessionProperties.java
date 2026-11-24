package com.example.chat.session;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "chat.session")
public record ChatSessionProperties(Duration inactivityTimeout, RateLimit rateLimit) {

    public record RateLimit(int maxRequests, Duration window) {
    }
}
