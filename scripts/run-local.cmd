@echo off
rem Start the backend on http://localhost:8080 (needs a JDK 21 on PATH).
cd /d "%~dp0.."
call mvnw.cmd spring-boot:run
