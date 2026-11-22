# Features: chat-backend

Status date: 2026-11-22. Each feature lists its business rule and where the code lives.

## F1. Answer a visitor's question

A visitor sends one text message and receives one text answer.

- Request: `POST /api/chat` with `{"message": "..."}`.
- Response: `200` with `{"answer": "..."}`.
- Code: `api/ChatController`, `answers/AnswerService`.

## F2. Answer from a fixed question list

The backend holds a static map of known questions and their answers.

- Matching ignores letter case, punctuation, and extra whitespace. "WHAT are your opening hours!!" matches "What are your opening hours?".
- Known questions today: Hello; What are your opening hours?; How can I contact support?; How do I reset my password?; Do you offer refunds?; Where is my order?; Thanks.
- The answer text is sample content. Support staff should replace it with real wording.
- Code: `AnswerService.KNOWLEDGE_BASE`.

## F3. Suggest what to ask

When the backend has no answer, it replies with an apology and lists every question it knows. A visitor who asks something unexpected learns what the bot can handle.

- Code: `AnswerService` (fallback text).

## F4. Limit message length to 250 characters

The backend rejects an empty message or a message over 250 characters with `400`.

- A character is one Unicode code point. An emoji counts as one.
- A message of exactly 250 characters passes. 251 fails.
- The widget enforces the same limit, but the backend does not rely on the widget.
- Code: `api/ChatRequest`, `api/MaxCodePoints`, `api/MaxCodePointsValidator`.

## F5. Accept requests from approved websites

Browsers may call the API only from origins listed in `chat.allowed-origins` (`application.yml`). Other origins receive `403` on the preflight request.

- Default: `http://localhost:5173` and `http://127.0.0.1:5173`.
- Production sites must be added here before the widget works on them.
- Code: `api/CorsConfig`.

## Not available yet

Live conversation with a support agent, conversation history, visitor identity, agent login, read receipts, and typing indicators. See [analysis.md](analysis.md).
