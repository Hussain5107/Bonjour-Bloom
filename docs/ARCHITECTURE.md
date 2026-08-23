# Architecture

## System context

Bonjour Bloom is a mobile- and tablet-first French-learning PWA for children aged 5–11 and adult complete beginners. It runs as a Vinext/React application on OpenAI Sites, supports a local-only experience, and can optionally use Supabase for email authentication and synchronized progress.

```text
Learner / parent
      |
React + Vinext UI
      |
      +-- curriculum and learning engine (bundled, validated content)
      +-- audio provider chain (recorded/neural/browser fallback)
      +-- pronunciation practice (transient local recording by default)
      +-- IndexedDB + localStorage migration + offline queue
      +-- optional Supabase Auth / Postgres with RLS
      |
OpenAI Sites / Cloudflare-compatible runtime
```

## Main modules

| Area | Location | Responsibility |
|---|---|---|
| Application shell and learner flows | `app/bloom-app.tsx` | Navigation, profiles, lessons, parent gate, offline and progress UI |
| Account boundary | `app/account-gate.tsx` | Email authentication and account/session UI |
| Responsive/PWA UI | `app/*.css`, `public/manifest.webmanifest`, `public/sw.js` | Phone/tablet layouts, install metadata and caching |
| Course model | `lib/course-model.ts` | Zod schemas and typed curriculum contracts |
| Starter curriculum | `lib/starter-course.ts`, `lib/curriculum.ts` | Pre-A1 content and compatibility projection |
| Learning/progress | `lib/learning-engine.ts`, `lib/progress.ts` | Completion, mastery/review helpers and learner state |
| Audio | `lib/audio.ts`, `app/listen-controls.tsx` | Provider fallback, French playback and controls |
| Pronunciation | `lib/pronunciation.ts`, `app/pronunciation-practice.tsx` | Local recording and honest assessment-provider boundary |
| Offline state | `lib/offline-store.ts` | IndexedDB persistence, legacy migration and sync queue |
| Cloud integration | `lib/supabase.ts`, `supabase/` | Supabase client, schema, migrations and RLS |
| Content pipeline | `content/`, `scripts/`, `CONTENT_AUTHORING.md` | Authored JSON validation, schema generation and import |
| Hosting | `.openai/hosting.json`, `vite.config.ts` | Existing Sites project binding and build configuration |

## Data flow

1. The account layer establishes an optional Supabase session.
2. Local app state is hydrated from IndexedDB/legacy storage.
3. The active learner profile selects its independent progress, preferences and lesson state.
4. Curriculum content is validated and projected into the lesson player.
5. Progress writes locally first; safe queued synchronization runs when configured and online.
6. Supabase RLS must enforce account and learner ownership for cloud records.
7. Microphone recordings remain local/transient unless a future, explicitly reviewed provider changes that policy.

## Architectural decisions

- Local-first operation keeps the starter experience usable without credentials.
- Profiles share one family/account but retain independent progress.
- Curriculum is modeled and validated rather than embedded as arbitrary UI copy.
- Audio and pronunciation are separate systems.
- Browser French speech is the no-cost fallback; secrets remain server-side.
- Scheduling and learning decisions should remain deterministic and testable until a validated alternative is intentionally adopted.
- `.openai/hosting.json` identifies the existing production Site and must not be replaced with a newly created project.

## Security and privacy boundaries

- The local four-digit parent gate is casual separation, not strong authentication.
- Never expose a Supabase service-role key to the client.
- Use migrations and RLS for cloud data changes.
- Do not retain child recordings by default.
- Avoid behavioral advertising, public chat, unrestricted child-facing external links, and unsupported assessment claims.
