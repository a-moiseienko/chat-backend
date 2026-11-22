package com.example.chat.answers;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** Answers questions from a fixed question-to-answer map. Replace the map with real support content later. */
@Service
public class AnswerService {

    private static final Pattern NON_WORD = Pattern.compile("[^\\p{L}\\p{N}]+");

    /** Human-readable question -> answer. Insertion order is kept so suggestions read naturally. */
    static final Map<String, String> KNOWLEDGE_BASE = knowledgeBase();

    private final Map<String, String> answersByNormalizedQuestion;
    private final String fallbackAnswer;

    public AnswerService() {
        this.answersByNormalizedQuestion = new LinkedHashMap<>();
        KNOWLEDGE_BASE.forEach((question, answer) ->
                answersByNormalizedQuestion.put(normalize(question), answer));

        String suggestions = KNOWLEDGE_BASE.keySet().stream()
                .map(question -> "\"" + question + "\"")
                .collect(Collectors.joining(", "));
        this.fallbackAnswer = "Sorry, I don't have an answer for that yet. Try asking: " + suggestions;
    }

    public String answerFor(String message) {
        return answersByNormalizedQuestion.getOrDefault(normalize(message), fallbackAnswer);
    }

    /** Case, punctuation and extra whitespace do not affect matching. */
    static String normalize(String text) {
        return NON_WORD.matcher(text.toLowerCase(Locale.ROOT)).replaceAll(" ").trim();
    }

    private static Map<String, String> knowledgeBase() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Hello",
                "Hi there! How can I help you today?");
        map.put("What are your opening hours?",
                "Our support team is available Monday to Friday, 9:00 to 18:00 (CET).");
        map.put("How can I contact support?",
                "You can write to support@example.com or keep chatting here during opening hours.");
        map.put("How do I reset my password?",
                "Choose \"Forgot password\" on the sign-in page and follow the link we email you.");
        map.put("Do you offer refunds?",
                "Yes. You can request a refund within 30 days of purchase.");
        map.put("Where is my order?",
                "Open the tracking link in your confirmation email to see where your order is.");
        map.put("Thanks",
                "You're welcome! Anything else I can help with?");
        return map;
    }
}
