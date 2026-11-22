# chat-backend

Backend for the support chat widget. A visitor sends a message and gets an answer. Today the answers come from a fixed list of questions. Live conversations with a support agent are planned (see [docs/analysis.md](docs/analysis.md)).

Java 21, Spring Boot 3.3, virtual threads. The browser side lives in the [widget](https://github.com/a-moiseienko/widget) repository.

## Quick start

You need a JDK 21 on your PATH. Maven is not required, the repository ships the Maven wrapper.

    .\mvnw.cmd spring-boot:run      # Windows
    ./mvnw spring-boot:run          # Linux and macOS

The service listens on http://localhost:8080. Check it:

    curl -X POST http://localhost:8080/api/chat -H "Content-Type: application/json" -d "{\"message\":\"hello\"}"

To see it with the widget, start the widget dev server as well. See the widget README.

## API

    POST /api/chat
    Content-Type: application/json

    {"message": "What are your opening hours?"}

| Status | Meaning |
|--------|---------|
| 200 | `{"answer": "..."}` |
| 400 | The message is missing, blank, or longer than 250 characters |
| 403 | A browser request came from an origin that is not allowed |

Rules:

- A character is one Unicode code point, so an emoji counts as one.
- Matching ignores case, punctuation, and extra spaces.
- An unknown question gets an apology that lists the questions the service knows.

The known questions and answers live in `AnswerService` (`src/main/java/com/example/chat/answers`). The answer text is sample content.

## Configuration

| Property | Environment variable | Default | Purpose |
|----------|----------------------|---------|---------|
| `server.port` | `SERVER_PORT` | `8080` | HTTP port |
| `chat.allowed-origins` | `CHAT_ALLOWED_ORIGINS` | `http://localhost:5173,http://127.0.0.1:5173` | Websites whose browsers may call the API, comma separated |

Add every website that embeds the widget to `chat.allowed-origins` for that environment.

## Test

    .\mvnw.cmd verify

The tests cover the answer matching, the 250-character limit (including the 250 and 251 boundary and emoji), validation errors, and CORS.

## Project layout

    src/main/java/com/example/chat/
      api/        controller, request and response types, validation, CORS
      answers/    the question and answer map
    docs/         analysis, features, and architecture decisions

## Build and release

GitHub Actions runs the pipeline in `.github/workflows/ci.yml`.

| Event | What happens |
|-------|--------------|
| Pull request to `master` | `./mvnw verify` |
| Push to `master` | Tests, then a Docker image tagged with the commit SHA goes to GHCR, then an automatic deploy to `dev` |
| Tag `v*` | The existing SHA image gets the version tag, then a deploy to `production` after a reviewer approves |

To release, tag a commit that already built on `master`:

    git tag v0.1.0
    git push origin v0.1.0

The deploy steps are placeholders until a host is chosen. Set up the `dev` and `production` environments (with required reviewers on `production`) and protect `master` in the GitHub repository settings.

## Docs

- [Analysis](docs/analysis.md): current state, gaps, risks
- [Features](docs/features.md): what the backend does and its rules
- [Decisions](docs/decisions/): architecture decision records

## Contributing

Keep changes small and focused, and update the docs when behavior changes.
