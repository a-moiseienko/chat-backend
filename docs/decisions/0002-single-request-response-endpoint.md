# 0002. One request-response endpoint for the first slice

Status: Accepted (2026-11-22). Expect a later record to extend it with WebSocket.

## Context

The MVP plan calls for real-time messaging over WebSocket. The first slice needs to prove the widget, API, validation, and pipeline work together.

## Decision

Expose `POST /api/chat` with a JSON body and a JSON response. Defer WebSocket until conversations with human agents exist.

## Consequences

- The widget needs no connection management.
- The backend cannot push an agent's reply to a visitor yet.
- The widget API client is the only code that changes when we add WebSocket.
