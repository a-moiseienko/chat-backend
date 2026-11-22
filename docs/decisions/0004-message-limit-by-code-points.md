# 0004. Enforce the 250-character limit by Unicode code points on the server

Status: Accepted (2026-11-22)

## Context

The product limits messages to 250 characters. Bean Validation's `@Size` counts UTF-16 code units, so it counts one emoji as two characters. The widget counts code points.

## Decision

Add a `@MaxCodePoints` constraint that uses `Character.codePointCount`. Apply it to `ChatRequest.message` together with `@NotBlank`. Validate on the server even though the widget also limits input.

## Consequences

- The server and widget agree on what a character is: 250 emoji pass, 251 fail.
- A grapheme cluster built from several code points, such as a family emoji, counts as several characters in both places.
- Tests cover the 250 and 251 boundary and the emoji case.
