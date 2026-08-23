# Bonjour Bloom product roadmap

## Architecture direction

Bonjour Bloom is a mobile-first, offline-tolerant language-learning application. Supabase owns authenticated account/profile data, curriculum governance, and private learner records; local storage/IndexedDB preserve safe resumability. The domain boundaries are:

```text
Account → learner profiles → permissions/preferences
Language track → modules → objectives → lessons → audience variants/exercises
Learner events → review state/mastery → daily plan → focused session
Provider boundaries → speech/audio now; replaceable services later
```

Universal curriculum and learning records must remain language-neutral. Language-specific features live in extensible metadata. All private learner data is profile-scoped and protected by account-derived RLS. Published curriculum versions and historical learner evidence remain immutable or append-only.

## Delivery sequence

### Foundation — complete

- Verified and preserved the original application baseline.
- Added mobile navigation, responsive lesson/audio controls, offline-safe local persistence, and provider boundaries.
- Added account/profile persistence and Supabase progress isolation.

### Section 1: family learner profiles — implemented, awaiting merge

- Individual/family onboarding, age bands, language/CEFR/goal/time preferences.
- Parent-managed child-safe defaults and profile switching/archiving.
- Additive profile/backfill migrations with account ownership and RLS.

### Section 2: curriculum foundation — implemented, awaiting review/merge

- Language-neutral tracks, modules, objectives, prerequisites, lessons and five audience variants.
- Structured exercise validation, deterministic scoring, provenance and governed publishing.
- French Pre-A1 pilot, protected editor review, hosted discovery and legacy mappings.
- Pilot remains `in_review` pending qualified French editorial sign-off.

### Section 3: daily learning engine — current

- Append-only attempts/events, reviewable units and derived mastery.
- Deterministic spaced review and profile-specific daily plans.
- Mobile daily-session and manual-review flows with retry/idempotency safety.
- See [SECTION_3.md](./phases/SECTION_3.md).

### Section 4: skill-practice centres — next, bounded

- Reading, writing, listening and speaking practice using validated curriculum.
- Reuse Section 3 evidence/scheduling; no placement exam, AI scoring, or cloud speech.

### Later capability phases

1. Curated dictionary/search foundation; do not bulk-import external dictionaries.
2. Reviewed lesson authoring and licensed/cached audio providers.
3. Curated external resources and privacy-enhanced YouTube embedding.
4. Evidence-based pronunciation assessment after a separate safety/accuracy review.
5. Additional language curricula and broader CEFR coverage after editorial capacity exists.

Each later phase requires its own specification, migrations, threat/data review, tests, and explicit production authorization.

