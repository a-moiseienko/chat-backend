package com.example.chat.answers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnswerServiceTest {

    private final AnswerService service = new AnswerService();

    @Test
    void answersKnownQuestion() {
        assertThat(service.answerFor("What are your opening hours?")).contains("Monday to Friday");
    }

    @Test
    void ignoresCasePunctuationAndWhitespace() {
        assertThat(service.answerFor("  WHAT are your   opening hours!! "))
                .isEqualTo(service.answerFor("What are your opening hours?"));
    }

    @Test
    void everyKnownQuestionHasItsOwnAnswer() {
        AnswerService.KNOWLEDGE_BASE.forEach((question, answer) ->
                assertThat(service.answerFor(question)).isEqualTo(answer));
    }

    @Test
    void unknownQuestionGetsFallbackThatListsWhatToAsk() {
        assertThat(service.answerFor("xyzzy"))
                .startsWith("Sorry")
                .contains("\"Do you offer refunds?\"");
    }
}
