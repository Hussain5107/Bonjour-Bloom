# Section 3 — personalized daily learning, mastery and spaced review

Status: current phase; requirements only. Do not implement from this document until the user explicitly starts Section 3.

## Objective

Turn published, profile-appropriate curriculum into a deterministic daily learning experience. Keep exposure, attempts, lesson completion, review scheduling and objective mastery distinct. No AI may select, explain, or score the path.

## Preconditions

- Base work on Sections 1 and 2; do not recreate account, profile, curriculum, objective, lesson or exercise tables.
- Confirm the active profile provides target language, CEFR, age band, goal, timezone and 5/10/15/20/30-minute target.
- Confirm published curriculum, prerequisites, stable identifiers/versions, structured exercises and safe audience variants exist.
- Confirm the shared mobile shell has no desktop sidebar offset or horizontal overflow.
- If dependency branches remain unmerged, use a documented stacked branch such as `feature/daily-learning-engine`; never commit to `main` or deploy.

## Required domain model

### Append-only learning events and attempts

Retain stable event/idempotency IDs, owning profile/account context, language, curriculum/lesson/section/exercise/objective IDs, content version, event/activity type, privacy-minimized submitted answer, deterministic result, hint/retry data, duration when reliable, session ID, client timestamp, authoritative server timestamp and app version. Do not store recordings or unnecessary free text. Historical outcomes must not change when curriculum changes.

### Reviewable units

Schedule stable pedagogical units—vocabulary, phrases, patterns, objectives or reusable exercise prompts—not arbitrary UI elements. Store language, primary skill, objective links, source/version, active/deprecated and review-eligibility state, render modes and safe audience suitability. Preserve history across replacement/archive mappings.

### Review state

One profile/unit state records `new | learning | review | relearning | suspended`, first seen, last attempt/success, next review UTC, interval, consecutive success, lapses, review count, recent normalized grade, scheduler version and content version. Counters and intervals are non-negative and bounded.

### Mastery

Use explainable categories: `not_started | introduced | practising | familiar | mastered | needs_review`. Exposure or one immediate correct answer cannot produce mastery. Require independent, delayed evidence and multiple exercise modes where available. Lapses reduce or flag mastery. Objective aggregation must prevent one repeated item hiding weak coverage elsewhere.

## Deterministic scheduler v1

- Grades: `again`, `hard`, `good`, `easy`; incorrect answers cannot become `easy`, and hints cap the grade.
- New/incorrect items return through a bounded learning sequence.
- `hard` grows slower than `good`; `easy` grows faster only after introductory evidence.
- Lapses shorten intervals and enter relearning.
- Immediate retries do not count as delayed mastery evidence.
- Centralize minimum/maximum intervals and scheduler version in one module.
- Identical inputs produce identical UTC outputs. Timezone changes affect display/day grouping, never duplicate or erase reviews.
- Place the algorithm behind a provider interface so future versions migrate state explicitly.

## Daily-plan algorithm

Generate an idempotent profile/day/session plan from published active content and current progress:

1. due/overdue reviews, capped;
2. resume an incomplete eligible lesson;
3. next prerequisite-satisfied lesson;
4. a bounded, age-configurable number of new reviewable units;
5. recap/summary.

Fit approximately—not as a promise—to 5, 10, 15, 20 or 30 minutes. Preserve overdue work predictably without allowing it to consume every session. Never select unpublished, archived, wrong-language, wrong-level or unsafe-variant content. Show an honest empty state when none is eligible. Persist the plan or versioned inputs so refresh and profile switching cannot generate contradictory/cross-profile plans.

## Learner experience

- Home: learner, target, due count, resume state, `Start today's learning`, optional `Review now`, and factual recommendation reason.
- Session: visible progress, pause/resume, refresh recovery, retry-safe submission, deterministic feedback/hints, exit and factual summary.
- Review: due items, supported vocabulary/phrase/objective grouping, safe review-ahead, suspend/unsuspend, and confirmed reset/relearn.
- Adapt density, controls and wording by age without changing normalized grades or authorization.
- Avoid shame, manipulative countdowns, streak loss, unsupported performance claims and false scientific precision.

## Data integrity, offline and RLS

- Add additive migrations and preserve all legacy completion. Ambiguous legacy evidence maps to exposure/introduction—not invented mastery.
- Writes use client idempotency keys and server timestamps. Duplicate/retried submissions cannot double count.
- Queue only safe events using the existing offline foundation; show pending/failure honestly.
- Profiles may access only their own plans, attempts, mastery and review states through owning-account RLS.
- Clients cannot directly assign mastery, arbitrary next-review dates, or attempts for unpublished content.
- Curriculum editor rights do not grant private learner-history access.
- Index profile/due time, profile/language/mastery, plan/session, idempotency key, content version, objective/item and event time.

## Required verification

- Scheduler grades, hint cap, boundaries, lapses, same-session retry, determinism, versioning, UTC/timezone.
- Planner priority/caps, prerequisites, language/CEFR/variant filtering, all five time targets, stable regeneration and empty state.
- Mastery evidence, aggregation, duplicate submission and lossless legacy migration.
- Cross-profile/family denial, profile-ID substitution and trusted derived writes.
- Pause/resume, refresh/offline retry, keyboard/focus and accessible feedback.
- Home, session and review at 360, 390, 430, 768 and 1024px: no horizontal overflow or obscured controls.
- Run lint, typecheck, unit/integration tests, available database/RLS and migration dry runs, production build and supported browser flows. Report skipped checks precisely.

## Explicit exclusions

No full skill-practice centres, placement/level exams, certification, AI/cloud APIs, AI planning/scoring/explanations, pronunciation scoring, new speech recognition/TTS, notifications, reward economy, YouTube, tutors/video/payments, additional curricula, merge or production deployment.

## Definition of done

Existing learners and progress remain intact; daily plans are deterministic and profile-specific; due/resume/new work fits the saved target; events are immutable and idempotent; mastery requires meaningful delayed evidence; RLS isolates every learner state; mobile pause/resume works without overflow; migrations/tests/build pass or blockers are accurately reported.

