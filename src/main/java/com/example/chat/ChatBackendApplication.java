package com.example.chat;

import com.example.chat.session.ChatSessionProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(ChatSessionProperties.class)
@SpringBootApplication
public class ChatBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatBackendApplication.class, args);
    }
}
