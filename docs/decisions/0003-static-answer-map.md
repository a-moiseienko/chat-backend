# 0003. Static map of questions and answers

Status: Accepted (2026-11-22). Temporary.

## Context

We want a working answer flow before we build storage, an agent console, or a knowledge base.

## Decision

Keep the questions and answers in a static `LinkedHashMap` in `AnswerService`. Normalize both the stored questions and the incoming message (lower case, strip punctuation, collapse whitespace) before matching. Return a fallback that lists the known questions when nothing matches.

## Consequences

- Changing an answer needs a code change and a deploy.
- Matching is exact after normalization. "opening hours" does not match "What are your opening hours?".
- The map lives in memory and works on one instance. Replace it with a database table when support staff need to edit content.
- Tests assert that every known question returns its own answer, so a normalization clash between two questions fails the build.
