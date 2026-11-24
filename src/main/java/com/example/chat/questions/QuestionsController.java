package com.example.chat.questions;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class QuestionsController {

    private final QuestionRepository questionRepository;

    public QuestionsController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @GetMapping
    public QuestionsResponse list() {
        return new QuestionsResponse(questionRepository.findAllQuestions());
    }
}
