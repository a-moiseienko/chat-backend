package com.example.chat.api;

import com.example.chat.answers.AnswerService;
import com.example.chat.session.ChatSessionProperties;
import com.example.chat.session.SessionIdHeader;
import com.example.chat.session.SessionRateLimiter;
import com.example.chat.session.SessionStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@Import({AnswerService.class, CorsConfig.class, SessionStore.class, SessionRateLimiter.class,
        ChatControllerTest.SessionPropertiesConfig.class})
@TestPropertySource(properties = {
        "chat.session.inactivity-timeout=30m",
        "chat.session.rate-limit.max-requests=30",
        "chat.session.rate-limit.window=1m"
})
class ChatControllerTest {

    private static final String EMOJI = "😀"; // one code point, two UTF-16 units
    private static final String SESSION_ID = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mvc;

    @TestConfiguration
    @EnableConfigurationProperties(ChatSessionProperties.class)
    static class SessionPropertiesConfig {
    }

    private ResultActions ask(String message) throws Exception {
        return ask(message, SESSION_ID);
    }

    private ResultActions ask(String message, String sessionId) throws Exception {
        var request = post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"" + message + "\"}");
        if (sessionId != null) {
            request = request.header(SessionIdHeader.NAME, sessionId);
        }
        return mvc.perform(request);
    }

    @Test
    void knownQuestionReturnsItsAnswer() throws Exception {
        ask("What are your opening hours?")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer", containsString("Monday to Friday")));
    }

    @Test
    void unknownQuestionReturnsFallback() throws Exception {
        ask("xyzzy")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer", startsWith("Sorry")));
    }

    @Test
    void blankMessageIsRejected() throws Exception {
        ask("   ").andExpect(status().isBadRequest());
    }

    @Test
    void missingMessageIsRejected() throws Exception {
        mvc.perform(post("/api/chat").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void messageOf250CharactersIsAccepted() throws Exception {
        ask("a".repeat(250)).andExpect(status().isOk());
    }

    @Test
    void messageOf251CharactersIsRejected() throws Exception {
        ask("a".repeat(251)).andExpect(status().isBadRequest());
    }

    @Test
    void limitCountsCharactersNotUtf16Units() throws Exception {
        ask(EMOJI.repeat(250)).andExpect(status().isOk());
        ask(EMOJI.repeat(251)).andExpect(status().isBadRequest());
    }

    @Test
    void missingSessionIdIsRejected() throws Exception {
        ask("Hello", null).andExpect(status().isBadRequest());
    }

    @Test
    void malformedSessionIdIsRejected() throws Exception {
        ask("Hello", "not-a-uuid").andExpect(status().isBadRequest());
    }

    @Test
    void requestsBeyondTheRateLimitAreRejected() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        for (int i = 0; i < 30; i++) {
            ask("Hello", sessionId).andExpect(status().isOk());
        }
        ask("Hello", sessionId).andExpect(status().isTooManyRequests());
    }

    @Test
    void differentSessionsHaveIndependentRateLimits() throws Exception {
        String busySession = UUID.randomUUID().toString();
        for (int i = 0; i < 30; i++) {
            ask("Hello", busySession).andExpect(status().isOk());
        }

        ask("Hello", UUID.randomUUID().toString()).andExpect(status().isOk());
    }

    @Test
    void allowsConfiguredOrigin() throws Exception {
        mvc.perform(options("/api/chat")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type," + SessionIdHeader.NAME))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void rejectsUnknownOrigin() throws Exception {
        mvc.perform(options("/api/chat")
                        .header("Origin", "http://evil.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}
