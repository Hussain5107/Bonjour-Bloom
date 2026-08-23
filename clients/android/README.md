# Bonjour Bloom Android

This directory contains the native Android prototype exported from the Bonjour Bloom Google AI Studio project on 2026-08-23.

It is intentionally isolated from the production React web app at the repository root. The AI Studio export combined both projects inside one `app/` directory; this copy keeps only the Kotlin/Jetpack Compose Android module so future coding agents can understand and modify either client without confusing their build systems.

## Included features

- Milo voice conversation room with Gemini-backed exchanges and deterministic offline fallbacks
- Microphone recording, animated speech visualization, bilingual text, phonetic hints, quick replies, and replay controls
- Live audio transcription with four preset French feeds, timestamps, translations, vocabulary extraction, and pronunciation guidance
- Deterministic spaced repetition, CEFR Pre-A1 mastery calculation, adaptive daily planning, and Room persistence
- Compose navigation for daily sessions, review center, voice room, transcriber, profiles, lessons, rewards, and parent views

## Project layout

- `app/src/main/java/com/example/bonjourbloom/gemini/` — Gemini Retrofit API and response models
- `app/src/main/java/com/example/bonjourbloom/audio/` — recording and French text-to-speech services
- `app/src/main/java/com/example/bonjourbloom/ui/screens/` — Compose screens, including voice and transcription
- `app/src/main/java/com/example/bonjourbloom/data/` — curriculum, scheduler, mastery, planner, Room entities, DAOs, and repository
- `app/src/test/` — deterministic JVM unit tests

## Local setup

1. Open this directory in Android Studio.
2. Use JDK 21 and Android SDK 35.
3. Copy `.env.example` to `.env` and set `GEMINI_API_KEY` locally. Never commit `.env` or a real API key.
4. Sync Gradle and run the `app` configuration on an Android 8.0 (API 26) or newer emulator/device.

The exported project does not include a Gradle wrapper. Android Studio can use its bundled Gradle tooling, or a wrapper can be generated later with a compatible Gradle installation.

## Provenance

- AI Studio app: https://aistudio.google.com/apps/d10bfdc5-1a2c-492a-9459-97399429de4c
- Exported: 2026-08-23
- Package namespace: `com.example.bonjourbloom`
- Application ID: `com.aistudio.bonjourbloom.vkmz`

This is source preservation, not a claim that the Android client is deployed to the existing web URL. The web deployment and Android build are separate products.
