package com.example.chat.api;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank
        @MaxCodePoints(ChatRequest.MAX_MESSAGE_LENGTH)
        String message) {

    public static final int MAX_MESSAGE_LENGTH = 250;
}
