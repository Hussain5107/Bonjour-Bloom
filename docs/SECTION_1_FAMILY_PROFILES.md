# Section 1 — Family profiles and onboarding

The responsive eight-step onboarding supports individual or family mode, nickname, age band, French target language, normalized CEFR level, controlled learning goal, daily minutes and a final privacy summary. Draft state is stored with the existing offline application state, so refresh and sign-in hydration can resume it. Completion creates one profile once and clears the draft.

The persistent profile manager switches active learners, adds family profiles, stores owner-managed child permissions and archives profiles after confirmation. The final usable profile cannot be archived. Archived progress is preserved. Permanent deletion and account-level data export require a later reviewed workflow.

The dashboard remains the existing learning experience and reads its active profile’s isolated progress. When level is unconfirmed the saved `level_confirmed` flag provides the future placement-assessment extension point; no assessment is implemented.

Apply `004_family_learner_profiles.sql` and `005_legacy_profile_backfill.sql` after migrations 002–003. Both are designed to be rerun safely. Configure only the existing public Supabase URL and anon key; no new environment variables or service-role browser credentials are required.

Automated TypeScript tests cover legacy migration, safe defaults, age/level independence, switching/archiving and UI contracts. The SQL pgTAP scaffold documents negative cross-account checks; it requires a local Supabase/pgTAP harness that this repository does not yet provide.
