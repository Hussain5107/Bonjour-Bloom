# Continuation audit and open-source reuse decision

Audit date: 2026-08-23. No candidate code or assets were imported during this audit.

## Existing app status

| Area | Status | Evidence / weakness |
|---|---|---|
| Child onboarding and profiles | Complete for demo | Early, Junior and Explorer profiles persist independently. Adult mode missing. |
| Curriculum and lesson player | Partial | Zod-validated 44-word Pre-A1 seed, four lesson/review flows and varied exercises. No complete A1 unit, sound-activity set, story/dialogue model or adaptive presentation service. |
| Audio | Technically weak | Every displayed word/lesson model can use browser French speech synthesis with slow playback, but no provider abstraction, voice choice, neural/recorded source or persistent audio cache. |
| Pronunciation | Partial | Just-in-time microphone permission, recording, replay, denial/unsupported fallback and honest non-scoring copy work. Provider/result separation, recognized words, VAD, difficult-sound feedback and production adapter are missing. |
| Progress and review | Technically weak | Profiles, lesson completion, XP, minutes, goals and a minimal due helper exist. No per-word mastery, attempts, review queue, due forecast or adaptive repetition. |
| Parent and safety | Partial | Local parent gate, goal settings, export and privacy copy work. Gate is casual separation only; reset/import, weekly strengths/needs and adult-as-manager are missing. |
| PWA/offline | Partial | Manifest, install icon, Apple metadata, service worker shell/runtime caching and install guidance work. Lesson-pack state is cosmetic; no IndexedDB pack/audio repository, quota handling, safe sync queue or update UI. |
| Accessibility | Partial | Semantic controls, labels, visible focus, 44px targets and reduced motion are present. Automated axe and Playwright coverage are missing; some native media controls remain browser-dependent. |
| Verification | Complete for current scope | Strict TypeScript, four Vitest tests, ESLint and production build pass. Test depth and end-to-end coverage are insufficient for the expanded acceptance criteria. |

Storage migration must read and preserve the existing `bonjour-bloom` localStorage record before writing any IndexedDB schema.

## Reuse/license matrix

| Feature | Candidate | Reuse directly | Adapt concept only | Reject | Reason |
|---|---|---:|---:|---:|---|
| IndexedDB repository boundaries and tested local history | EchoTalk | No | Yes | No | MIT, TypeScript/Vite, strong test evidence and relevant offline services. Its non-React service/UI structure should not be copied into the current React/Vinext app. Reimplement the repository contract and migration locally. |
| Practice/shadowing state machine | EchoTalk | No | Yes | No | Useful, well-tested sequencing concept; current curriculum stages differ and need age/adult adaptation. |
| Recording/playback and TTS controls | EchoTalk | No | Yes | No | Browser APIs match, but current recording flow already works and direct porting would add duplication. EchoTalk does not provide genuine pronunciation assessment. |
| Service worker | EchoTalk | No | No | Yes | Current worker exists; replacing it with another handcrafted worker would not reduce complexity. |
| French dictionary/content/audio bundle | PopMots | No | No | Yes | Repository code is MIT, but Lexique/Kaikki/Wiktionary-derived data and bundled audio require separate record-level license/provenance review. Do not import the dataset. |
| FSRS review UX, daily limits and seven-day forecast | PopMots | No | Yes | No | Strong product fit; Vue components are incompatible. Use an independently maintained scheduler package or an original tested scheduler behind an interface. |
| Import/export and local-first review model | PopMots | No | Yes | No | Useful concept, but current family export and profile model require different validation and migrations. |
| VAD/noise calibration and poor-recording detection | SpeakFlow | No | Yes | No | MIT and React/TypeScript compatible in principle. Its Web Audio pipeline is relevant, but must be independently implemented and tested for iOS/tablets. |
| Transcript fuzzy comparison as pronunciation score | SpeakFlow | No | No | Yes | Recognition similarity is not phoneme-level pronunciation evidence and must remain a separately labelled transcription aid. |
| Visualizer/permission/failure-state concepts | SpeakFlow | No | Yes | No | Valuable interaction patterns, but Chakra/Framer dependencies and branding do not fit this app. Use a minimal Canvas/Web Audio meter later. |
| Exercise catalogue and vocabulary detail information architecture | WordPecker | No | Yes | No | MIT and React/TypeScript, but backend, paid AI/audio/image services and MongoDB architecture are disproportionate. Adapt only the learning patterns. |
| ElevenLabs/audio caching implementation | WordPecker | No | No | Yes | Provider-specific backend and cost model do not fit the credential-free local demo; create neutral provider interfaces instead. |
| Provider boundaries and IndexedDB/FSRS concepts | EchoType | No | Yes | No | MIT and unusually close Next/React/TypeScript versions. Scope is much larger and includes desktop, auth, AI and sync; use only architectural concepts. |
| Tauri/desktop, AI tutor, authentication and full sync stack | EchoType | No | No | Yes | Outside the tablet PWA scope and increases privacy, dependency and operational risk. |
| Phoneme scoring implementation | EchoType | No | No | Yes | Depends on external providers and needs independent accuracy/privacy validation; define our own honest result contract and server adapter. |

## Candidate summary

- EchoTalk: MIT, TypeScript/Vite, browser APIs, PWA/IndexedDB, and 200+ reported tests. Best conceptual reference for local history and practice sequencing.
- PopMots: MIT Vue 3 PWA using `ts-fsrs`, local audio, offline use, export/import and Playwright/Vitest. Best conceptual reference for review UX; dictionary/audio assets are excluded pending independent licensing review.
- SpeakFlow: MIT React/TypeScript PWA using Web Audio/Web Speech. Best conceptual reference for VAD and failure states; transcript fuzziness is explicitly not pronunciation scoring.
- WordPecker: MIT React/TypeScript with MongoDB and multiple paid AI providers. Useful exercise/detail concepts, but direct integration would increase complexity and external data exposure.
- EchoType: MIT Next.js 16/React 19/TypeScript with IndexedDB, FSRS, provider options and optional Supabase/Tauri. Closest framework match, but its broad desktop/AI/auth surface should not be adopted.

## Decision

No source code, branding, datasets, recordings or images will be copied. The continuation will implement small original interfaces and tests informed by the concepts above. This keeps the architecture coherent and avoids third-party content-license ambiguity. If direct source adaptation becomes justified later, the exact commit, copyright, license text, files and modifications must be recorded before merging.

## Sources inspected

- https://github.com/alisolphp/EchoTalk
- https://github.com/claudiabdm/popmots
- https://github.com/Kimosabey/speak-flow
- https://github.com/baturyilmaz/wordpecker-app
- https://github.com/Talljack/echo-type
