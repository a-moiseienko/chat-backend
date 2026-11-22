# 0001. Java 21, Spring Boot, virtual threads

Status: Accepted (2026-11-22)

## Context

The team wants a JVM backend that handles many concurrent chat connections without reactive code.

## Decision

Use Java 21 with Spring Boot 3.3. Enable virtual threads with `spring.threads.virtual.enabled: true`.

## Consequences

- Request handling blocks on I/O without exhausting a platform thread pool.
- Code that holds a `synchronized` monitor while it blocks can pin a carrier thread. Prefer `ReentrantLock` in our own code. Run with `-Djdk.tracePinnedThreads=full` to find pinning.
- SQLite allows one writer at a time. Keep the connection pool small if we use it, and plan the move to PostgreSQL before real traffic.
- The runtime needs more memory than a Node service. Plan for 512 MB to 1 GB.
