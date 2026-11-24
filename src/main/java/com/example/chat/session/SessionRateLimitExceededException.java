package com.example.chat.session;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class SessionRateLimitExceededException extends RuntimeException {

    public SessionRateLimitExceededException() {
        super("Too many requests for this session");
    }
}
