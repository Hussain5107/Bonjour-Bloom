# Bonjour Bloom

This GitHub repository is the canonical source for the complete deployed project. Start with the documentation map below before making changes.\n\nA tablet-first French learning PWA for ages 5–11 and adult complete beginners. The initial release contains a validated, data-driven five-lesson Pre-A1 Starter Path, independent learner profiles, resumable progress, French audio, local recording/replay, review scheduling, parent controls, IndexedDB offline state, Supabase sync, install guidance, and app-shell caching.


## Documentation map

- [Agent instructions](AGENTS.md) — primary rules for Codex, Claude Code, Gemini/AI Studio, and other coding agents.
- [Current project status](docs/PROJECT_STATUS.md) — deployed capabilities, known limitations, and next priorities.
- [Architecture](docs/ARCHITECTURE.md) — system boundaries, modules, data flow, and decisions.
- [Development guide](docs/DEVELOPMENT.md) — setup, verification, common tasks, and Git workflow.
- [Deployment runbook](docs/DEPLOYMENT.md) — Sites publication, smoke test, and rollback.
- [Content authoring](CONTENT_AUTHORING.md) — validated lesson-content workflow.
- [Privacy design](PRIVACY_DESIGN.md) and [reuse audit](docs/AUDIT_AND_REUSE.md).
- [Historical planning material](docs/history/) — preserved product rationale and phase prompts; current docs take precedence.

## Run and test

```bash
npm ci
npm run dev
```

Open `http://localhost:3000`. Before committing or deploying, run:

```bash
npm run lint
npm run typecheck
npm test
npm run build
```

No credentials are required for the local-only experience. Copy `.env.example` to `.env.local` when configuring Supabase or production providers. Never commit `.env.local` or server credentials.

### Email accounts and cloud progress

Create a Supabase project, run `supabase/schema.sql`, then apply the numbered files in `supabase/migrations/` in ascending order. Enable Email authentication and set the Site URL plus redirect URLs. Configure `NEXT_PUBLIC_SUPABASE_URL` and `NEXT_PUBLIC_SUPABASE_ANON_KEY` in `.env.local`. Email/password sessions persist on-device. The legacy JSON row remains compatible while normalized profile, progress, idempotent completion, and authored-content tables enforce ownership with RLS. Never place a service-role key in client configuration.

## Architecture and local data

- `app/bloom-app.tsx`: accessible UI and interaction flows.
- `lib/course-model.ts`: complete Zod course, lesson, vocabulary, dialogue, audio, section and exercise models.
- `lib/starter-course.ts`: five-lesson Pre-A1 content pack.
- `lib/curriculum.ts`: compatibility projection consumed by the reusable player.
- `lib/progress.ts`: profiles, goals, review and pronunciation-result logic.
- `lib/audio.ts`: recorded, neural, browser-fallback and mock audio providers behind one teacher-voice service.
- `lib/pronunciation.ts`: technically separate pronunciation evidence contract with local, production and test adapters.
- `lib/offline-store.ts`: IndexedDB state and offline sync queue; legacy localStorage state is migrated and preserved.
- `public/sw.js`: versioned app-shell/runtime cache. Updating the cache version never removes local learner data.

## Content authoring

See `CONTENT_AUTHORING.md`. The schemas reject incomplete packs and broken vocabulary references. Run `npm run validate:content` after every content change. French content is original educational text; Milo artwork is AI-generated for this project.

## Audio and pronunciation

Every visible learning item uses a tiered audio service: licensed recorded assets when mapped, a server-only neural TTS endpoint when configured, then French `speechSynthesis` as the development/offline fallback. Female and male teacher preferences, normal/slow requests and provider failure fallback are represented without putting credentials in the browser. The demo currently ships no third-party recordings and therefore normally reaches the browser fallback; voices and offline availability vary by device. Microphone permission is requested only inside pronunciation practice. Audio remains in memory, is not uploaded by the local adapter, and is discarded on reload.

The local adapter deliberately returns “We couldn’t hear clearly” rather than presenting transcript similarity as pronunciation precision. For production, implement a server-only adapter for Azure Speech Pronunciation Assessment (or equivalent) that returns word/phoneme evidence, rate-limits requests, validates payloads, and keeps credentials server-side. Always allow retry, slow audio and “practice later.”

The continuation audit and license/reuse matrix are in `docs/AUDIT_AND_REUSE.md`; attribution decisions are recorded in `THIRD_PARTY_NOTICES.md`.

## Offline and installation

The versioned service worker caches the shell and visited resources and shows an update-ready reload control. Lesson content ships in the app bundle; progress uses IndexedDB and queues safe sync after reconnection. Android browsers may offer Install App. On iPadOS use Share → Add to Home Screen. Device French voices may be unavailable offline; the app never substitutes an English voice.

## Privacy and safety

See `PRIVACY_DESIGN.md`. There are no ads, public chat, purchases, social feed, child-facing external links, or behavioral analytics. Parent settings use a simple local gate suitable only for casual separation, not authentication. No recordings are retained by default.

## Backlog

- **MVP hardening:** IndexedDB repositories, pre-cached licensed native audio, full exercise analytics, Playwright onboarding/offline/microphone flows, axe audit, content editor, secure parent authentication.
- **Next release:** Azure pronunciation adapter, encrypted optional sync, placement, full A1/A2 curriculum, illustrated dialogue/story packs, storage quota controls.
- **Long term:** B1–C1 curriculum, teacher tools, human content review workflow, regional French voices, advanced writing/conversation assessment.

## Deployment

The project is Cloudflare Worker-compatible through the included Vinext/Sites configuration. Keep all service credentials in hosted runtime variables, never client code.
