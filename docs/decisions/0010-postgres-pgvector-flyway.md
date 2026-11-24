# 0010. Postgres with pgvector, schema owned by a separate Flyway project

Status: Accepted (2026-11-24).

## Context

We want a real database behind the chat backend, starting with a dummy `questions` table the
backend reads from, and the `vector` extension enabled so a later semantic-search schema (and a
separate data-ingest project) can add embedding columns without re-provisioning the database.

## Decision

- Run Postgres via the `pgvector/pgvector` image in `docker-compose.yml` at the repo root.
- Keep schema migrations in a separate `db-migrations` project (its own Flyway SQL files), not in
  chat-backend. The `flyway/flyway` image runs `migrate` against Postgres before chat-backend
  starts.
- chat-backend only reads (`spring-boot-starter-jdbc` + `JdbcClient`); it does not own or run
  migrations.

## Consequences

- chat-backend, db-migrations, and the future data-ingest project are independent projects sharing
  one database, so schema changes can be reviewed/deployed on their own cadence.
- Local `mvn`/`./mvnw` runs of chat-backend need a reachable Postgres with the migrations already
  applied (`docker compose up postgres flyway`), since there is no embedded/test database here yet.
- `vector` extension is enabled now (`V1__enable_pgvector.sql`) so adding embedding columns later
  is a plain `ALTER TABLE` migration.
- `render.yaml` provisions a matching managed Postgres (`chat-db`) and wires its host/port/
  database/user/password into chat-backend as `PGHOST`/`PGPORT`/`PGDATABASE`/`PGUSER`/`PGPASSWORD`
  (same variable names `docker-compose.yml` sets), so chat-backend's `application.yml` builds the
  same JDBC URL shape in both environments. Render's `connectionString` property isn't used because
  it isn't JDBC-formatted. Migrations still need to be run against `chat-db` separately (e.g. by
  pointing the `db-migrations` Flyway image at Render's external connection string) before
  chat-backend's first deploy — `render.yaml` does not run them automatically.
