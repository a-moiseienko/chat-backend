# 0006. Build once, promote the same image by tag

Status: Accepted (2026-11-22)

## Context

The team wants every push to `master` to reach dev automatically and production to follow a strict, reviewed release.

## Decision

- Pull requests run `./mvnw verify` only.
- A push to `master` runs the tests, builds a Docker image tagged with the commit SHA, pushes it to GHCR, and deploys it to the `dev` environment.
- A tag matching `v*` retags the existing SHA image with the version and deploys it to the `production` environment. It does not rebuild.
- The `production` environment requires a reviewer to approve each run.
- The release job fails when the tagged commit is not on `master`.
- `master` requires a pull request and a passing `verify` check.

## Consequences

- Production runs the exact bytes that passed in dev.
- A release needs a tag on a commit that already built on `master`.
- The deploy steps were placeholders until a host was chosen; see [0008](0008-host-provider.md).
- GitHub settings (environments, branch protection) must be configured by hand. The workflow file cannot set them.
