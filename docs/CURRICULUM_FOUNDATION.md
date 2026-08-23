# Curriculum foundation (Section 2)

## Architecture

Section 2 is additive to the family learner model in migrations 004–005. A language-neutral track owns ordered modules. Modules own lessons; lessons reference reusable CEFR-tagged objectives and store five presentation variants for ages 5–7, 8–11, 12–15, 16–17, and 18+. Objective prerequisites form a directed acyclic graph. Exercises have an explicit type, expected answer, and point value.

The existing hard-coded French course remains available while content is migrated. `legacy_lesson_mappings` provides stable old-to-new IDs; no progress rows are rewritten or deleted. The `/curriculum` discovery route selects only `published` content matching the active profile's CEFR level and age audience. It does not infer level from age.

## Governance

Lifecycle is `draft → in_review → reviewed → published → archived`. Authors can draft and request review, reviewers record review, and publishers release or archive. RLS hides every non-published lesson and variant from learner queries. The transition RPC and database checks require recorded review before publication.

The pilot contains 2 modules, 6 lessons, 6 shared objectives, 3 variants per lesson, and 48 French words/phrases. All six new-model lessons remain `in_review` because qualified French editorial review has not been verified. The already shipped legacy lessons remain usable in the existing experience through stable mappings; this does not falsely mark the new curriculum records reviewed. Its provenance explicitly records that limitation.

## Authoring and validation

1. Create objectives and prerequisites before lessons; cycles are rejected in TypeScript and PostgreSQL.
2. Create all three audience variants. Variants adjust tone and framing, never CEFR level or objective.
3. Include project/source notes and license information. Do not claim a reviewer until a qualified reviewer signs off.
4. Run `npm test`, `npm run typecheck`, `npm run lint`, and `npm run build`.
5. Use `/admin/curriculum` to filter, preview, and inspect allowed transitions. Applying transitions to hosted content requires an assigned database editor role.

## Rollout and rollback

Apply migration 006 after migrations 001–005. Seed content first as draft or in-review, validate as an editor, then review and publish through the controlled transition. Rollback is non-destructive: stop querying the new routes/tables and keep the legacy mapping; do not drop tables after learner progress begins referencing new IDs. Production deployment and migration execution are intentionally outside this branch.

## Language and exercise contracts

Tracks separate target language, interface language, locale, script and writing direction, with extensible metadata for language-specific features. Universal tables contain no French-only grammar fields. Objectives distinguish primary and secondary skills and retain topic tags, mastery evidence, author notes and an internal CEFR-alignment rationale (not certification).

Exercises are versioned structured records covering instruction, prompt cards, listening selection, matching, multiple choice, true/false, reorder, fill-gap, short answer, listen/repeat placeholders, dialogue and reflection. Scorable records retain deterministic scoring metadata. In this client-visible pilot, answers are suitable only for formative practice—not secure examinations.

## Hosted querying and seed

`listPublishedCurriculum(language, cefr)` requests only active, published lessons and their variants for the selected slice. The bundled pilot is a temporary review/compatibility fixture, not the long-term complete curriculum store. Run `npm run curriculum:seed` with the project URL and service-role key from a trusted operator environment. The script uses stable slug conflicts and upserts, is safe to dry-run against a disposable/local project, and intentionally downgrades source records to `in_review`; reviewers and publishers must use their explicit roles and the transition RPC.

The production project was not migrated or seeded in Section 2. The local Supabase CLI was unavailable, so pgTAP database tests were authored but not executed.