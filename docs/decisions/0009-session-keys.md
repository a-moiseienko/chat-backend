# 0009. Session keys: random, opaque, unguessable

Status: Proposed (2026-11-23)

## Context

Session support (see the widget's own ADR) means the backend must recognize which conversation a request belongs to. The key travels from a browser tab, so it must be safe to hand to a stranger without letting them guess, enumerate, or reuse someone else's session.

## Decision

- Accept the session key the widget generates (a `crypto.randomUUID()`, 122 bits of randomness) instead of issuing sequential or timestamp-based IDs.
- Never derive the key from anything guessable: no incrementing counters, no visitor IP or user agent, no short keys.
- Treat the key as a bearer credential: whoever presents it can read and continue that conversation, so it travels over HTTPS only and is never logged in full.
- Expire sessions server-side after a period of inactivity, so a leaked key stops working eventually even if the client keeps it.

## Consequences

- No separate authentication is needed to continue a conversation, which keeps the widget anonymous and simple, but anyone who obtains the key can read that conversation.
- The backend needs a session store (in-memory to start, with an eviction policy) keyed by this ID; this does not exist yet.
- Rate-limit session lookups so a bad actor cannot brute-force valid keys by volume, even though the key space makes guessing one key impractical on its own.
