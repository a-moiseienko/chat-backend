package com.example.chat.api;

import com.example.chat.answers.AnswerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@Import({AnswerService.class, CorsConfig.class})
class ChatControllerTest {

    private static final String EMOJI = "😀"; // one code point, two UTF-16 units

    @Autowired
    private MockMvc mvc;

    private ResultActions ask(String message) throws Exception {
        return mvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"" + message + "\"}"));
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
    void allowsConfiguredOrigin() throws Exception {
        mvc.perform(options("/api/chat")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
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
