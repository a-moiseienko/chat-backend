# 0007. Maven wrapper and LF line endings

Status: Accepted (2026-11-22)

## Context

Developers use Windows. CI and the Docker build use Linux. Machines should not need Maven installed.

## Decision

- Commit the Maven wrapper (Maven 3.9.9, script-only distribution). Store `mvnw` with the executable bit in the index.
- Commit `.gitattributes` with `* text=auto eol=lf` so files keep LF endings on every machine.

## Consequences

- `./mvnw` works in CI and Docker without a Maven install.
- Windows Git no longer prints CRLF warnings for tracked files.
- A new checkout needs only a JDK 21.
