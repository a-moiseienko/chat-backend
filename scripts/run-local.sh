#!/usr/bin/env sh
# Start the backend on http://localhost:8080 (needs a JDK 21 on PATH).
set -e
cd "$(dirname "$0")/.."
exec ./mvnw spring-boot:run
