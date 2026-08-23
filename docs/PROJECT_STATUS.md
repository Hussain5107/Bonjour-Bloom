# Project status

Updated: 2026-08-23

## Current production baseline

Bonjour Bloom is deployed at https://bonjour-bloom.ihussain876633.chatgpt.site. GitHub is intended to be the canonical source for the complete deployed project.

Implemented capabilities include:

- email account flow with optional Supabase-backed synchronization;
- separate learner profiles and active-profile switching;
- working “Add another learner” profile setup;
- child and adult beginner modes;
- five-lesson validated Pre-A1 starter curriculum;
- responsive phone/tablet application shell and bottom navigation;
- French audio controls with provider fallback;
- local microphone recording/replay with no default retention;
- goals, progress, review helpers and parent settings;
- IndexedDB/localStorage migration, offline queue and PWA caching;
- content schemas, validation/import tooling and Supabase migrations;
- OpenAI Sites production deployment metadata.

## Verified checks for the current deployed learner fix

- TypeScript: passed
- Tests: 27 passed
- Production build: passed

Run the full suite again for every future change; this status is historical evidence, not a substitute for verification.

## Known limitations

- The four-digit parent gate is casual local separation, not secure parent authentication.
- Browser speech quality and offline voice availability vary by device.
- No third-party recorded French audio ships by default.
- Pronunciation practice does not claim phoneme-accurate scoring.
- Curriculum coverage remains an initial Pre-A1 path rather than a complete CEFR program.
- Cloud sync and RLS require a correctly configured Supabase project.
- Automated browser end-to-end coverage and accessibility audits should be expanded.
- Content review/editor workflows and production pronunciation providers remain future work.

## Recommended next priorities

1. Keep GitHub `main` synchronized with every deployed Sites version.
2. Add focused UI regression tests for onboarding, profile creation, offline behavior, and authentication callbacks.
3. Harden parent authentication and account/profile ownership.
4. Expand reviewed curriculum through the existing content pipeline.
5. Improve accessibility and real-device PWA testing.
6. Introduce production audio/pronunciation providers only after privacy, licensing, cost, and validation review.

## Historical planning material

Earlier Codex phase prompts and the original master handoff are preserved under `docs/history/`. They provide product rationale and roadmap context but may contain stale branch names, baseline commits, or “do not deploy” instructions from their original bounded sessions. This status file and the current repository state take precedence.
