# 0005. CORS allowlist from configuration

Status: Accepted (2026-11-22)

## Context

The widget runs on other websites and calls this API from the visitor's browser.

## Decision

Allow `POST` with the `Content-Type` header on `/api/**` for the origins in `chat.allowed-origins`. Read the list from `application.yml` and allow an environment override per deploy target.

## Consequences

- Each customer site must be added to the list before the widget works there.
- CORS protects visitors' browsers, not the API. Anyone can call it with `curl`. Rate limiting is a separate task.
- Local development works without extra setup because the Vite dev origin is in the default list.
