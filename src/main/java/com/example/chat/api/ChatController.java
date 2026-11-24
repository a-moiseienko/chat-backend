package com.example.chat.api;

import com.example.chat.answers.AnswerService;
import com.example.chat.session.InvalidSessionIdException;
import com.example.chat.session.SessionIdHeader;
import com.example.chat.session.SessionRateLimitExceededException;
import com.example.chat.session.SessionRateLimiter;
import com.example.chat.session.SessionStore;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AnswerService answerService;
    private final SessionStore sessionStore;
    private final SessionRateLimiter rateLimiter;

    public ChatController(AnswerService answerService, SessionStore sessionStore, SessionRateLimiter rateLimiter) {
        this.answerService = answerService;
        this.sessionStore = sessionStore;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping
    public ChatResponse ask(@Valid @RequestBody ChatRequest request,
                             @RequestHeader(SessionIdHeader.NAME) String sessionId) {
        if (!sessionStore.touch(sessionId)) {
            throw new InvalidSessionIdException();
        }
        if (!rateLimiter.tryAcquire(sessionId)) {
            throw new SessionRateLimitExceededException();
        }
        return new ChatResponse(answerService.answerFor(request.message()));
    }
}
