# 0008. Host provider: pluggable, Render for now

Status: Accepted (2026-11-23)

## Context

The pipeline in `.github/workflows/ci.yml` and the `dev`/`production` environments were set up before a host was chosen. The service is a single Dockerized Spring Boot app with no host-specific code, so the choice of provider is an infrastructure detail that can change as traffic and budget needs change.

## Decision

- Treat the hosting provider as swappable. Keep provider-specific config in one file (`render.yaml`) rather than spreading assumptions about it through the app or CI.
- Deploy on Render for now, using the existing `Dockerfile` as-is: a Blueprint (`render.yaml`) defines a free-tier Docker web service with a health check at `/actuator/health` and `CHAT_ALLOWED_ORIGINS` set to the deployed widget's origin.
- Do not record a separate ADR per provider switch. Update this one (or replace it) when the provider changes, since the reasoning ("pick whatever is cheapest/simplest for a single small container") stays the same.

## Consequences

- Moving off Render later (for example to Cloudflare Workers Containers, Fly.io, or a VPS) means replacing `render.yaml` with that provider's equivalent, not changing application code.
- Render's free tier spins the service down after 15 minutes idle, so the first request after idle time is slow. Acceptable for now; revisit if that becomes a problem.
- `CHAT_ALLOWED_ORIGINS` is wired to the widget's current origin in `render.yaml`. It must be updated by hand (or via the Render dashboard) whenever the widget moves.
