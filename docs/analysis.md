# Analysis: chat-backend

Date: 2026-11-22

## Purpose

The backend serves the support chat widget. The target product is a chat between a website visitor and a support team. The current build is a hello-world slice: a visitor asks a question and gets a canned answer.

## Current state

- One endpoint: `POST /api/chat`.
- Answers come from a static map of 7 questions in `AnswerService`.
- Messages are validated: not blank, at most 250 characters (Unicode code points).
- Browsers may call the API from origins listed in `chat.allowed-origins`.
- Spring Boot 3.3 on Java 21 with virtual threads.
- CI builds an image per commit on `master`. A version tag promotes that image to production after a reviewer approves.
- No data is stored. The service keeps no state between requests.

## Gap to the MVP

The first MVP plan described a real conversation with a support agent. These pieces do not exist yet:

| Planned MVP item | Status |
|------------------|--------|
| Visitor sends a message and gets a reply | Done, with canned answers |
| 250-character limit enforced server-side | Done |
| Real-time delivery over WebSocket | Not started |
| Anonymous visitor sessions | Not started |
| Message persistence | Not started |
| Agent console and agent login | Not started |
| Rate limiting | Not started |
| Read receipts and typing indicators | Moved to post-MVP backlog |

## Risks

- **Open endpoint.** Anyone who knows the URL can post to it. CORS only restricts browsers. Add rate limiting before a public deploy.
- **Placeholder deploy steps.** The `deploy-dev` and `promote-prod` jobs print a message and deploy nothing until a host is chosen.
- **Unverified in CI.** The workflows have not run on GitHub yet, and the first run may expose configuration errors.
- **Single instance assumption.** The answer map lives in memory. Replace it with a database before the service runs on more than one instance.

## Open questions

- Which host runs dev and production (Render, Railway, or a VPS)?
- Do agents use a console we build, or an existing helpdesk tool?
- Do visitors stay anonymous, or do they identify themselves (for example by email)?
- What database replaces the static map and stores conversations (SQLite for the MVP, PostgreSQL later)?
