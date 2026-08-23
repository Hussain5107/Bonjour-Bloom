> Historical planning document preserved for context. Some branch names, baseline commits, task boundaries, or deployment instructions may be stale. Current repository state, AGENTS.md, and docs/PROJECT_STATUS.md take precedence.\n\n# Bonjour Bloom — Master Codex Handoff and Implementation Instructions

Copy this entire document into a new Codex conversation. Treat it as the authoritative project brief unless the user explicitly changes a requirement.

## 1. Role and working method

You are continuing development of an existing French-learning web application. Do not rebuild it from scratch, replace working systems, or make broad speculative changes.

Work as a cautious senior full-stack engineer:

- Inspect the repository, current architecture, database migrations, deployment configuration and existing tests before editing.
- Preserve existing authentication, lessons, progress tracking, UI, production deployment and Git history.
- Implement one bounded phase at a time.
- Use a feature branch and open a draft pull request. Do not commit directly to `main`.
- Before editing, report the current architecture, relevant files, database impact, risks and exact implementation plan.
- After editing, run the existing formatter, type-checker, tests and production build.
- Add focused tests for new functionality.
- Never expose credentials, tokens, private keys or Supabase service-role keys.
- Do not consume large token budgets regenerating content that can be imported, normalized or generated through a reusable pipeline.

## 2. Project identity and verified baseline

- Application: **Bonjour Bloom**
- Purpose: free French education from complete beginner toward advanced learning
- Learners: children aged 5–11 and adult complete beginners
- Existing production site: `https://bonjour-bloom.ihussain876633.chatgpt.site/`
- GitHub repository: `https://github.com/Hussain5107/Bonjour-Bloom`
- Default branch: `main`
- Verified baseline commit: `5da7b9b613ee895e3dcce98519653005c7dd67b0`
- Baseline commit message: `Initial import of Bonjour Bloom application`
- Supabase is already used for email authentication and progress tracking.
- The production build passed at the baseline.
- The original Git history was preserved.
- `.env.local` was excluded, and the previous secret scan found no embedded credentials.

Confirm that the repository HEAD and working tree are what you expect before making changes. Preserve the live application unless the user explicitly authorizes deployment of a new version.

## 3. Product vision

Bonjour Bloom must become an installable, mobile-first and tablet-first Progressive Web App that feels like a native application:

- application icon and installable PWA manifest;
- responsive layouts for phones and tablets;
- bottom navigation on mobile instead of a permanent sidebar;
- touch targets of at least 44×44 px;
- vocabulary → words/phrases → sentences → dialogues/stories → progressively advanced lessons;
- natural French audio for every important word and sentence;
- microphone recording and pronunciation feedback;
- daily goals, streaks and progress tracking;
- separate, age-appropriate experiences for children and adults;
- initial focus on a small number of high-quality lessons, with a scalable content pipeline for later expansion;
- free access to education, without copying paid-course content.

The design should be friendly for children without making the adult path childish. Child and adult content must use shared infrastructure but separate audience metadata, navigation and presentation where needed.

## 4. Non-negotiable safety and quality constraints

- The application serves children. Apply data minimization and privacy-by-design.
- Do not store a child’s microphone recording by default. Process transiently whenever technically feasible.
- If recordings are ever retained, require a separate explicit product decision, parental controls, retention rules, deletion controls and updated privacy documentation.
- Separate text-to-speech from pronunciation assessment; they are different technical systems.
- Never claim that a pronunciation score is phonetically accurate unless the implemented system has been validated for French pronunciation assessment.
- Never copy lessons, exercises, audio, videos or subscription content from Lawless French, Rocket Languages or other paid providers. They are design/feature references only.
- All imported datasets, models, voices, images and videos require provenance and licence review.
- Do not import an entire external dataset directly into the learner interface.
- All educational content must support draft → reviewed → published status and human review.
- Do not automatically publish AI-generated or externally imported lessons.

## 5. Current external repositories assessed

### A. `gbroques/awesome-french`

Repository: `https://github.com/gbroques/awesome-french`

This is primarily a curated directory of external French-learning resources, not a ready-made lesson database. Use it only as an input for a reviewed **Free Learning Resources** directory. Do not copy third-party videos, courses, transcripts or copyrighted lessons. Store links and source metadata after review.

### B. `hbenbel/French-Dictionary`

Repository: `https://github.com/hbenbel/French-Dictionary`

This contains CSV files for French nouns, verbs, conjugations and grammatical categories. It is the most technically useful source for:

- dictionary search;
- grammatical classification;
- gender information;
- verb-conjugation support;
- word validation;
- lesson-authoring assistance.

Do not ship the entire dataset to every tablet. Import it server-side or into Supabase, normalize it, index it, and expose paginated/search-based access.

Important licensing issue: the repository code declares MIT, but its dictionary data is derived from Kaikki/Wiktionary. Underlying Wiktionary-derived content has attribution/share-alike obligations. Treat software licensing and source-data licensing separately. Add appropriate notices and provenance. Do not assume the repository’s MIT licence overrides the source dataset licence.

### C. `melling/LanguageLearning`

Repository: `https://github.com/melling/LanguageLearning`

This is mainly a list of links. It provides little reusable application code or structured lesson content. It may supply candidates for the reviewed resource directory only.

## 6. Content and dictionary architecture

Build a reusable content pipeline instead of hard-coding hundreds of lessons.

Suggested entities, adapted to the existing schema rather than blindly duplicated:

- `content_sources`
- `dictionary_entries`
- `dictionary_forms` or conjugation records
- `vocabulary_collections`
- `vocabulary_collection_items`
- `lessons`
- `lesson_sections`
- `lesson_items`
- `content_reviews`
- `audio_assets`
- `video_resources`
- `learner_progress`

Every imported/published content record should be able to retain:

- source name and source URL;
- external identifier;
- source licence and attribution text;
- import date and source version/commit when available;
- age group;
- CEFR level;
- topic;
- difficulty;
- draft/reviewed/published status;
- reviewer and review timestamp;
- active/deprecated status.

Do not treat a general dictionary as a curriculum. Create reviewed vocabulary collections such as:

- children 5–7;
- children 8–11;
- adult beginner;
- adult business;
- CEFR Pre-A1, A1, A2, B1, B2 and later levels.

For the first import, use approximately **500–1,000 high-frequency, manually reviewed beginner entries**, not every word in the source dictionary. Add import validation, duplicate handling, idempotency, dry-run reporting and rollback/recovery instructions.

## 7. Voice and text-to-speech architecture

The application must eventually read any French word, sentence, dialogue or user-authored story aloud.

Implement a provider interface so the application is not coupled to one voice engine:

- `BrowserSpeechProvider`
- `CachedAudioProvider`
- `OpenSourceServerTTSProvider`

### Immediate provider

Implement or preserve browser `window.speechSynthesis` as the no-cost fallback:

- select a voice whose language begins with `fr`, preferring `fr-FR`;
- handle asynchronous voice loading;
- play, pause, resume, stop and replay;
- normal and slow speed;
- split long text into sentence-sized chunks;
- highlight the sentence currently being read;
- show a useful fallback message when a French system voice is unavailable.

### Open-source providers for later testing

- Piper with a tested French voice: lightweight and suitable for self-hosting, but engine and individual voice-model licences must be checked separately.
- MeloTTS French: potentially more natural but requires a separate Python/server runtime.
- Kokoro/browser models: experimental until French support, model licence, tablet memory use, download size and startup performance are verified.

Do not put a Python neural model directly into a static frontend. A higher-quality model should run through a separate HTTPS service or through a proven browser runtime that passes real tablet performance tests.

### Generated-audio caching

For administrator-authored stories and lessons:

1. Normalize the text.
2. Hash normalized text + language + voice + speed.
3. Reuse existing generated audio when the hash matches.
4. Generate audio once through the configured provider.
5. Store the completed file in Supabase Storage or the project’s approved storage system.
6. Store its URL, hash, duration, provider, voice, speed, licence/provenance and generation status.
7. Learners normally replay cached audio instead of generating it repeatedly.
8. Cache published lesson audio for offline use through the PWA where appropriate.

Never put server secrets in frontend code.

## 8. Pronunciation recording and feedback

Pronunciation assessment is a later independent phase. For the initial version:

- allow the learner to record and replay their own voice locally;
- request microphone permission only after a clear user action;
- provide clear recording, processing, retry and failure states;
- do not upload or retain recordings by default;
- compare recognized text with the expected phrase only as a basic first signal;
- describe results honestly, e.g. “words recognized” rather than falsely claiming precise accent correction;
- design a future provider interface for speech recognition and phoneme-level scoring;
- include child privacy review before enabling any cloud speech provider.

## 9. YouTube integration rules

YouTube Shorts and videos may be used only as optional, curated learning supplements.

Do not:

- download YouTube videos;
- extract or redistribute their audio;
- reproduce transcripts/captions without a suitable licence or permission;
- remove ads, branding or standard player controls;
- suppress required YouTube functionality;
- allow background playback outside YouTube’s permitted experience;
- automatically publish search results;
- award coins, prizes or compensation solely for watching YouTube videos;
- allow unrestricted YouTube browsing inside the children’s interface.

Create an administrator-reviewed video library. Each record should include:

- YouTube video ID;
- title and channel;
- canonical URL;
- associated lesson/topic and vocabulary;
- CEFR level;
- age group;
- children/adult suitability;
- speaking speed;
- captions availability;
- Made-for-Kids status when available/required;
- reviewer, review status and review date;
- active/unavailable status.

Player behaviour:

- render a thumbnail/consent placeholder first;
- load the official YouTube iframe only after the user taps Play;
- keep autoplay off;
- use a responsive official player and preserve required controls;
- implement a parent setting to disable external videos;
- periodically check availability;
- keep children and adult video collections separate;
- add Bonjour Bloom’s own vocabulary, comprehension or speaking activity around a video so the app provides independent educational value.

Third-party videos are optional enrichment and may disappear. Core lessons must remain usable without YouTube. Prefer original Bonjour Bloom micro-lessons for long-term reliability.

## 10. Mobile, tablet and PWA requirements

- Mobile layout must use bottom navigation rather than a desktop sidebar.
- Tablet layout may use adaptive navigation when sufficient width exists.
- Test at representative widths including approximately 360, 390, 768 and 1024 px.
- Respect safe-area insets on installed iOS/Android PWAs.
- Prevent bottom navigation from covering content or controls.
- Use large readable typography and accessible contrast.
- Ensure microphone and audio controls remain usable in portrait and landscape.
- Validate the web-app manifest, icons, service worker, installability and offline fallback.
- Do not promise complete offline functionality for external YouTube content or uncached server-generated speech.

## 11. Supabase and security requirements

- Inspect current migrations and RLS before creating tables.
- Maintain least-privilege RLS for every new table and storage bucket.
- Learners may read only published/allowed content and their own progress.
- Administrator/editor operations require explicit roles and server-side enforcement.
- Never rely only on hidden UI controls for authorization.
- Use migrations, not manual undocumented dashboard changes.
- Add indexes for dictionary lookup, filters and imported-source identifiers.
- Use idempotent import jobs and avoid duplicate records.
- Preserve account deletion and data deletion capability.
- Document required environment-variable names in `.env.example`; never commit values.

## 12. Recommended phased roadmap

### Phase 0 — Repository and architecture audit

- Verify baseline commit and current branch.
- Map frontend framework, routes, state management, Supabase schema, RLS, storage, lesson model, progress model, audio implementation, PWA configuration and deployment files.
- Run the existing tests and production build.
- Identify existing functionality that already satisfies these requirements.
- Produce a short gap report. Do not edit yet.

### Phase 1 — Voice foundation and local recording

- Introduce the provider-based TTS abstraction without breaking existing audio.
- Implement/preserve browser French speech as fallback.
- Add reusable listen controls for words, sentences and stories.
- Add local record/replay with no default upload.
- Add mobile/tablet tests.

### Phase 2 — Curated dictionary foundation

- Add licence/provenance documentation.
- Add schema/migrations and RLS for dictionary/content sources.
- Create an idempotent importer for a reviewed 500–1,000-word starter dataset.
- Add dictionary search, gender, grammatical category and conjugation display.
- Connect dictionary entries to lesson-authoring tools.

### Phase 3 — Lesson authoring and cached open-source audio

- Add admin-only story/lesson editor.
- Add draft/review/publish workflow.
- Add preview with device voice.
- Add server TTS contract and generated-audio caching.
- Test Piper French first; compare against MeloTTS before selecting a production engine.

### Phase 4 — Curated resources and YouTube

- Add reviewed external-resource directory.
- Add admin-approved YouTube video records and consent-based embeds.
- Add age separation, Made-for-Kids handling and parent-disable setting.
- Add original activities around each video.

### Phase 5 — Pronunciation assessment

- Evaluate open-source/local and cloud providers separately.
- Establish a transparent scoring model and test it with French speakers.
- Add privacy/retention controls before any server upload.
- Never market basic speech-to-text matching as full pronunciation correction.

### Phase 6 — Curriculum expansion

- Expand reviewed lessons from Pre-A1/A1 upward.
- Add adult business-life modules.
- Add child-specific stories, games and repetition patterns.
- Use external AI such as Perplexity/AWS models only for drafting structured lesson content; imported/generated content must remain unverified until human review.

## 13. Immediate task for this Codex session

Start with **Phase 0 only**.

Do not implement the dictionary import, YouTube integration, neural TTS service or pronunciation scoring yet.

Perform the following:

1. Inspect the repository at the verified baseline.
2. Run the existing validation commands and production build.
3. Map the current architecture and database schema.
4. Identify existing audio, recording, lesson, progress, mobile navigation and PWA functionality.
5. Identify security, licensing and child-privacy gaps relevant to the proposed roadmap.
6. Recommend the smallest safe Phase 1 change set.
7. List the exact files and migrations Phase 1 would modify.
8. Report any assumptions or blockers.
9. Stop and wait for user approval before changing source.

## 14. Definition of success

The project succeeds when it becomes a reliable, maintainable and genuinely educational French PWA—not merely a large collection of copied words and links. Every added feature should improve structured learning while preserving child safety, licensing compliance, mobile usability, performance and the ability to expand content without repeatedly rewriting the application.

