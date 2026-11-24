# 0011. Merge Flyway migrations into chat-backend

Status: Accepted (2026-11-24). Supersedes [0010](0010-postgres-pgvector-flyway.md).

## Context

The separate `db-migrations` project (its own repo, Docker image, and CI pipeline) added real
coordination overhead without a payoff yet: nothing else reads the database, so there's no actual
need to deploy schema and app independently. It also opened a gap where the backend could deploy
before (or after) the matching migration had run, since the two pipelines aren't linked (see
`todos/deployments-not-linked.md`, now resolved by this change rather than acted on directly).

## Decision

- Move the migrations into chat-backend as `src/main/resources/db/migration/`, Flyway's default
  classpath location.
- Add `flyway-core` and `flyway-database-postgresql` to chat-backend's `pom.xml`. Spring Boot
  auto-runs pending migrations against `spring.datasource` on application startup — no separate
  Flyway service/image/step.
- Drop the `flyway` service from `docker-compose.yml`; chat-backend now depends only on `postgres`
  being healthy.
- `db-migrations` (repo, Docker image, CI) is left in place but unused — not deleted, in case it's
  needed again (e.g. if a separate ingest project needs to own schema independently later).

## Consequences

- One artifact, one deploy: chat-backend's image is both the schema and the app. No ordering gap
  between "migrate" and "deploy" to reason about.
- chat-backend now needs write/DDL privileges on the database (previously it only needed SELECT),
  and startup fails if migrations fail — migrations are now in the app's critical path.
- Local runs without Docker (`./mvnw spring-boot:run`) now also apply migrations automatically
  against whatever Postgres `PGHOST`/etc. point at — no separate `docker compose up ... flyway`
  step needed first.
- If/when a separate data-ingest project needs the same schema, revisit: either it also depends on
  chat-backend's migrations running first, or migrations move back out to a shared project.
