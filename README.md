# chat-backend

Spring Boot (Java 21, virtual threads) backend for the support chat widget.

## API

    POST /api/chat
    {"message": "What are your opening hours?"}

    200 {"answer": "Our support team is available Monday to Friday, ..."}
    400 message is blank or longer than 250 characters

Answers come from a static map in `AnswerService`. Matching ignores case and punctuation.
Unknown questions get a fallback that lists the questions it does know.

## Run locally

    .\mvnw.cmd spring-boot:run      # Windows
    ./mvnw spring-boot:run          # Linux / macOS

Try it:

    curl -X POST http://localhost:8080/api/chat -H "Content-Type: application/json" -d "{\"message\":\"hello\"}"

Browsers may call the API from the origins in `chat.allowed-origins` (`application.yml`).

## Test

    .\mvnw.cmd verify
