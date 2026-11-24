package com.example.chat.questions;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Reads questions seeded by the db-migrations (Flyway) project. */
@Repository
public class QuestionRepository {

    private final JdbcClient jdbcClient;

    public QuestionRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<String> findAllQuestions() {
        return jdbcClient.sql("SELECT question FROM questions ORDER BY id")
                .query(String.class)
                .list();
    }
}
