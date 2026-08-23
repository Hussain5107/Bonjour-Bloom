# Implementation status

Last updated: 2026-08-23. Keep this file below 400 words.

## Baseline and completed work

- Baseline: `main` at `5da7b9b` (original application import).
- `phase-1/mobile-audio-controls` at `6c8329e`: five-destination responsive navigation, French browser-speech controls, recording/privacy behavior and offline improvements.
- `feature/family-learner-profiles` at `f256c74`, draft PR #2: resumable individual/family onboarding, persistent profile switching, age/CEFR/goal/time preferences, child-safe permissions and mobile centring.
- `feature/curriculum-foundation` at `c94e380`, draft PR #3 stacked on PR #2: language-neutral curriculum, five audience bands, structured exercises, governed review/publishing, profile-aware discovery, editor workflow, French pilot and shared mobile-shell overflow fix.

Current validation on PR #3: 68 tests passed, typecheck passed, lint has zero errors and seven pre-existing warnings, and the production build passed. Production was not deployed.

## Migrations

- `002_profiles_progress.sql`: account profiles and progress isolation.
- `003_content_pipeline.sql`: versioned lesson-content governance.
- `004_family_learner_profiles.sql`: accounts, learner profiles, permissions and profile progress.
- `005_legacy_profile_backfill.sql`: idempotent legacy-profile/default backfill.
- `006_curriculum_foundation.sql`: normalized curriculum, objectives/prerequisites, audience variants, editor roles, lifecycle/RLS, immutable publication, indexes and legacy mappings.

Migrations 004–005 were previously applied to Supabase project `ksjwuadjvbswhuhmznwg`. Migration 006 and the curriculum seed have **not** been applied to production.

## Current blockers/manual gates

- PR #2 must be reviewed/merged before stacked PR #3 is merged or rebased.
- All six new curriculum pilot lessons remain `in_review` pending qualified French editorial sign-off.
- Migration 006, pgTAP/RLS tests and the idempotent seed need an approved Supabase operator/test environment; the local Supabase CLI was unavailable.
- No production merge, migration, seed, editor-role assignment or deployment is authorized.

## Next phase

Section 3 is specified in `docs/phases/SECTION_3.md`. It has not started and must use Sections 1–2 as dependencies without duplicating their schemas.
