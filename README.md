# Bonjour Bloom

A tablet-first, local-first French learning PWA for children aged 5–11. The demo includes independent learner profiles, age modes, goals, four complete lesson/review flows, 44 validated vocabulary records, audio, recording, rewards, parent controls, export, install guidance, and offline shell caching.

## Run and test

```bash
npm install
npm run dev
npm run lint
npm run typecheck
npm test
npm run build
```

No credentials are required. Copy `.env.example` only when configuring production providers.

### Email accounts and cloud progress

Create a Supabase project, run `supabase/schema.sql` in its SQL editor, enable Email authentication, and set the Site URL plus redirect URLs for local and production origins. Configure `NEXT_PUBLIC_SUPABASE_URL` and `NEXT_PUBLIC_SUPABASE_ANON_KEY`. The public anon key is protected by row-level security; never place a Supabase service-role key in client configuration. Users receive passwordless email links, and the `user_learning_state` policy restricts each account to its own JSON progress record. Existing device-local progress is uploaded on first sign-in when no cloud record exists.

## Architecture and local data

- `app/bloom-app.tsx`: accessible UI and interaction flows.
- `lib/curriculum.ts`: Zod-validated content, kept separate from UI.
- `lib/progress.ts`: profiles, goals, review and pronunciation-result logic.
- `lib/audio.ts`: recorded, neural, browser-fallback and mock audio providers behind one teacher-voice service.
- `lib/pronunciation.ts`: technically separate pronunciation evidence contract with local, production and test adapters.
- `localStorage`: local-first profile/progress repository for this offline demo. A production release should replace this implementation behind a repository interface with IndexedDB and an encrypted, consent-aware sync adapter.
- `public/sw.js`: versioned app-shell/runtime cache. Updating the cache version never removes local learner data.

## Content authoring

Vocabulary records require French, English, article, gender, part of speech, learner hint, level, topic, emoji/image reference, example and translation. Lessons contain sequenced exercises with prompt, answer and choices. `curriculumSchema` rejects incomplete packs; run `npm run validate:content` after edits. French content is original educational text. Milo artwork is AI-generated for this project; browser speech synthesis voices are device-provided.

## Audio and pronunciation

Every visible learning item uses a tiered audio service: licensed recorded assets when mapped, a server-only neural TTS endpoint when configured, then French `speechSynthesis` as the development/offline fallback. Female and male teacher preferences, normal/slow requests and provider failure fallback are represented without putting credentials in the browser. The demo currently ships no third-party recordings and therefore normally reaches the browser fallback; voices and offline availability vary by device. Microphone permission is requested only inside pronunciation practice. Audio remains in memory, is not uploaded by the local adapter, and is discarded on reload.

The local adapter deliberately returns “We couldn’t hear clearly” rather than presenting transcript similarity as pronunciation precision. For production, implement a server-only adapter for Azure Speech Pronunciation Assessment (or equivalent) that returns word/phoneme evidence, rate-limits requests, validates payloads, and keeps credentials server-side. Always allow retry, slow audio and “practice later.”

The continuation audit and license/reuse matrix are in `docs/AUDIT_AND_REUSE.md`; attribution decisions are recorded in `THIRD_PARTY_NOTICES.md`.

## Offline and installation

The service worker caches the shell and visited resources. The in-app pack control represents a local pack state; production should pre-cache licensed audio with quota/error reporting in IndexedDB/Cache Storage. Android browsers may offer Install App. On iPadOS use Share → Add to Home Screen. Microphone and speech APIs require secure contexts and vary on embedded browsers.

## Privacy and safety

There are no ads, public chat, purchases, social feed, child-facing external links, or behavioral analytics. Parent settings use a simple local gate suitable only for casual separation, not authentication. No recordings are retained by default. Before launch, obtain legal review for COPPA, UK Age Appropriate Design Code, GDPR/GDPR-K and applicable local laws; complete consent copy, retention/deletion controls, threat modeling, secure headers and endpoint rate limiting.

## Backlog

- **MVP hardening:** IndexedDB repositories, pre-cached licensed native audio, full exercise analytics, Playwright onboarding/offline/microphone flows, axe audit, content editor, secure parent authentication.
- **Next release:** Azure pronunciation adapter, encrypted optional sync, placement, full A1/A2 curriculum, illustrated dialogue/story packs, storage quota controls.
- **Long term:** B1–C1 curriculum, teacher tools, human content review workflow, regional French voices, advanced writing/conversation assessment.

## Deployment

The project is Cloudflare Worker-compatible through the included Vinext/Sites configuration. Keep all service credentials in hosted runtime variables, never client code.
