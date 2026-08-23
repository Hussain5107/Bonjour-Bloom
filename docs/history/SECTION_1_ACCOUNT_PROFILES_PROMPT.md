> Historical planning document preserved for context. Some branch names, baseline commits, task boundaries, or deployment instructions may be stale. Current repository state, AGENTS.md, and docs/PROJECT_STATUS.md take precedence.\n\n# Bonjour Bloom — Codex Development Prompt

## Section 1: Family Accounts, Learner Profiles and Personalized Onboarding

Copy this complete prompt into a new Codex conversation. Attach or provide the existing `Bonjour_Bloom_Codex_Handoff.md` as the authoritative project brief. If this prompt conflicts with the handoff, this prompt controls only the scope of Section 1; all safety, security, licensing and engineering requirements in the handoff remain applicable.

---

You are continuing development of the existing **Bonjour Bloom** application.

Repository: `https://github.com/Hussain5107/Bonjour-Bloom`  
Default branch: `main`  
Verified baseline commit from the original handoff: `5da7b9b613ee895e3dcce98519653005c7dd67b0`

Do not rebuild the application, replace working systems, erase existing data, commit directly to `main`, or deploy to production without explicit user authorization.

## 1. Objective

Implement the foundation for an all-age, family-friendly and eventually multilingual learning platform by adding:

1. individual and family account onboarding;
2. multiple learner profiles under a family account;
3. age-appropriate learner categories;
4. language, proficiency, goal and daily-target preferences;
5. parent-managed permissions for child profiles;
6. profile-specific progress isolation;
7. a personalized onboarding flow;
8. a profile switcher and profile-aware dashboard;
9. a data model that can support additional languages later, while making only French available in this release.

Age and proficiency must remain separate. Age changes presentation, safeguarding and content suitability. Proficiency changes lesson difficulty and recommendations.

## 2. Required working method

Before editing:

1. Read the supplied master handoff completely.
2. Inspect the repository, current branch, Git status and recent history.
3. Compare the current HEAD with the baseline commit and report any differences.
4. Inspect the frontend framework, routes, authentication, Supabase client, migrations, RLS policies, progress model, lesson model, tests, PWA configuration and deployment files.
5. Run the existing formatter/linter, type-checker, test suite and production build using the repository's documented commands.
6. Identify how current users and progress records will migrate to learner profiles without data loss.
7. Report:
   - current architecture relevant to this section;
   - exact files and migrations likely to change;
   - proposed database model;
   - migration/backfill strategy;
   - RLS strategy;
   - risks, assumptions and blockers;
   - implementation sequence.

If there is a material ambiguity that could cause data loss, authorization weakness or an incompatible user experience, stop and ask the user. Otherwise, continue with the implementation after reporting the plan.

Create a feature branch named appropriately for this work, such as `feature/family-learner-profiles`. Do not commit directly to `main`. Do not deploy or merge.

## 3. Product rules

### Account modes

Support these two onboarding choices:

- **Individual account:** intended for one learner managing their own profile.
- **Family account:** one account owner can create and manage multiple learner profiles.

Do not require a separate email address for every child. Authentication must remain at account level; learning progress must be stored at learner-profile level.

An existing authenticated user must be migrated safely to an individual account with one default learner profile. Existing progress must remain associated with that user through the new default profile.

### Learner profile fields

Adapt field names to the existing conventions, but the model must support:

- stable profile identifier;
- owning account or family identifier;
- display name or nickname;
- profile type: `child`, `teen`, or `adult`;
- age band: `5-7`, `8-11`, `12-15`, `16-17`, or `18+`;
- target language code;
- interface/source language code, prepared for later use;
- current proficiency level;
- learning goal;
- daily study target in minutes;
- avatar or safe predefined avatar identifier, if compatible with the current UI;
- active/archive status;
- created and updated timestamps.

Do not store a child's exact date of birth in this phase. Do not collect unnecessary personal information.

### Proficiency levels

Store normalized CEFR-compatible levels:

- `pre_a1`
- `a1`
- `a2`
- `b1`
- `b2`
- `c1`
- `c2`

The UI may present friendly labels such as New Learner, Beginner, Elementary, Intermediate, Upper Intermediate, Advanced and Mastery, but the stored values must remain normalized.

During onboarding, allow:

- `I am completely new` → `pre_a1`;
- selection of a known level;
- `I am not sure` → save an unconfirmed/default starting level and show a clearly labelled future placement-assessment option.

Do not implement the placement test in this section.

### Learning goals

Support a controlled initial set such as:

- general learning;
- school;
- travel;
- conversation;
- work/business;
- examination preparation.

Use stable internal identifiers rather than storing display text as the only value.

### Daily targets

Allow 5, 10, 15, 20 or 30 minutes per day. Preserve this as a learner preference; do not implement notification delivery in this section.

### Language selection

The schema and application services must support multiple target languages. For this release:

- show French as the only enabled language;
- other languages may appear only as disabled `Coming later` options if this matches the existing design;
- do not create empty curricula or claim that other languages are available;
- avoid hard-coding French assumptions into universal account/profile tables;
- use standard language/locale identifiers such as `fr` or `fr-FR` consistently with the existing codebase.

## 4. Parent-managed child settings

For family accounts, the account owner must be able to manage child and teen profiles.

Add a profile-level settings structure, adapted to the existing schema, that can later control:

- microphone use;
- AI features;
- external/YouTube content;
- live tutor or coaching features;
- reminder permissions.

In this section, implement storage and a parent-facing settings UI, but do not implement the future services themselves.

Safe defaults for profiles under 18:

- external videos disabled;
- live tutor/coaching disabled;
- AI features disabled until explicitly enabled by the account owner;
- microphone permission disabled at product-setting level until explicitly enabled by the account owner;
- reminders disabled until explicitly configured.

Browser/device permission prompts remain separate from these application settings. Never trigger microphone or notification permission during onboarding.

Adult individual profiles may manage their own settings.

Do not claim formal legal compliance. Implement privacy-by-design and document where a jurisdiction-specific privacy/legal review remains necessary.

## 5. Onboarding experience

Create a responsive, accessible, resumable onboarding flow. Reuse the existing visual system and components rather than introducing a conflicting design language.

Suggested steps:

1. Choose Individual or Family.
2. Create the first learner profile.
3. Select age band/profile type.
4. Select the target language, with French enabled.
5. Select current level or `I am not sure`.
6. Select one primary learning goal.
7. Select a daily study target.
8. Review privacy/parent settings when the profile is under 18.
9. Show a concise summary and complete onboarding.

Requirements:

- allow back/next navigation without losing entered values;
- validate each step;
- prevent impossible combinations, such as an `18+` age band stored as a child profile;
- persist completion safely and idempotently;
- resume incomplete onboarding after refresh or sign-in;
- avoid duplicate families/profiles if completion is retried;
- work at approximately 360, 390, 768 and 1024 px widths;
- maintain touch targets of at least 44×44 px;
- support keyboard navigation and visible focus;
- use clear language suitable for the selected age/account context;
- do not request microphone, notification or third-party permissions.

## 6. Profile switching and management

After onboarding, provide:

- a visible current-profile indicator;
- a profile switcher for accounts with multiple active profiles;
- add-profile capability for family owners;
- edit profile preferences;
- archive profile with confirmation;
- prevention of archiving/deleting the last usable profile without a safe recovery path;
- a child-safe profile selection view that does not expose account administration unnecessarily.

Do not implement permanent profile deletion unless the current application already has a safe deletion workflow. Archiving is sufficient for this section. Document a future deletion/export requirement.

The active profile must persist safely across refresh and sign-in. Do not rely exclusively on insecure client-side state for authorization.

## 7. Profile-specific progress

All learner progress reads and writes must become profile-aware.

Requirements:

- one profile must never see or overwrite another profile's progress;
- existing user progress must be backfilled to the existing user's default profile;
- switching profiles must immediately show the selected profile's correct progress;
- progress APIs/hooks/services must require or derive an authorized profile identifier;
- prevent arbitrary profile-ID substitution;
- preserve the current lesson and progress experience for migrated users;
- add appropriate indexes and uniqueness constraints to prevent duplicate progress records.

If the current schema makes a full progress migration unsafe within this bounded section, implement a compatible transitional layer and document the exact follow-up migration. Do not silently discard or reset progress.

## 8. Database and RLS expectations

Use migrations; do not rely on undocumented manual Supabase dashboard changes.

Adapt to the existing schema after inspection. A reasonable design may include:

- account/family metadata;
- learner profiles;
- learner preferences;
- parent-managed feature permissions;
- profile-aware learner progress;
- optional onboarding state.

Do not duplicate an existing table if the current schema already provides equivalent functionality.

RLS must enforce at minimum:

- authenticated account owners can read and manage only profiles belonging to their own account/family;
- learner progress is accessible only through profiles owned by the authenticated account;
- child settings can be changed only by the owning authenticated account, not merely because the UI hides controls;
- no user can access another family's profiles by guessing identifiers;
- service-role credentials are never exposed to the frontend;
- every new table has explicit RLS policies and appropriate indexes.

Prefer server-derived ownership checks. Add automated negative tests proving cross-account access is denied.

## 9. Dashboard behavior

Update the existing dashboard minimally so that it becomes profile-aware.

It should show:

- learner display name/avatar;
- selected language;
- current proficiency label;
- primary goal;
- daily target;
- existing progress for the active profile;
- a placeholder or disabled action for `Take a placement assessment` when the level is uncertain.

Do not build the later recommendation engine in this section. A deterministic welcome/recommendation message based on the saved level and goal is acceptable, but label it accurately and keep it simple.

## 10. Migration and backward compatibility

The implementation must include a documented, testable backfill strategy.

For every existing authenticated user:

1. create or resolve one account/family ownership record;
2. create exactly one default adult/individual learner profile unless reliable existing age information says otherwise;
3. attach existing progress to that default profile;
4. preserve authentication and user identifiers;
5. ensure the migration is idempotent;
6. provide a rollback or recovery strategy appropriate to the current migration system.

Do not invent child status or age for existing users. Use a neutral adult/unspecified-safe migration default and ask the user to complete missing preferences on next sign-in where necessary.

## 11. Testing requirements

Add focused tests covering:

- new individual onboarding;
- new family onboarding;
- add and switch learner profiles;
- child safe-default settings;
- age-band/profile-type validation;
- incomplete onboarding resume;
- idempotent onboarding completion;
- migrated existing-user behavior;
- profile-specific progress separation;
- cross-family RLS denial;
- unauthorized profile-ID substitution;
- responsive navigation and essential onboarding controls;
- accessibility basics for labels, focus and keyboard operation.

Run and report:

- formatter/linter;
- type-checker;
- unit and integration tests;
- database/RLS tests available in the repository;
- production build;
- focused browser or end-to-end flows if the repository supports them.

Do not state that testing passed unless the commands actually ran successfully. Report skipped or blocked tests explicitly.

## 12. Explicit exclusions

Do not implement any of the following in Section 1:

- actual placement examinations;
- vocabulary, grammar or CEFR exams;
- spaced-repetition scheduling;
- the full daily learning path;
- AI analysis or AI reports;
- any AI API connection;
- neural text-to-speech;
- pronunciation scoring;
- cloud storage of microphone recordings;
- notification delivery;
- YouTube integration;
- real-person matching, video calling or tutor payments;
- additional language curricula;
- production deployment.

Do not add speculative tables for every future feature. Add only the extension points required by this section.

## 13. Documentation

Update relevant project documentation with:

- the account/family/profile model;
- onboarding flow;
- age and proficiency separation;
- safe child defaults;
- migration/backfill behavior;
- RLS ownership model;
- new environment-variable names, if any, in `.env.example` without values;
- known limitations and next-phase dependencies.

Add a concise architecture decision record if the repository uses ADRs, or create one in the existing documentation style explaining why authentication remains account-level while learning data is profile-level.

## 14. Completion report

When implementation and verification are complete, provide:

1. concise outcome summary;
2. branch name and commit hash(es), if commits were created;
3. files and migrations changed;
4. final data model and RLS summary;
5. existing-user migration behavior;
6. test/build commands and their actual results;
7. screenshots or a precise description of tested onboarding/profile flows;
8. remaining risks, assumptions and blockers;
9. any manual Supabase/deployment steps required;
10. confirmation that production was not deployed or modified;
11. recommended scope for Section 2, without implementing it.

Open a draft pull request only if repository authentication and the user's existing authorization permit it. Otherwise, leave the feature branch ready and provide the exact command or steps the user can use. Never merge the pull request.

## 15. Definition of done

Section 1 is complete only when:

- existing users can sign in without losing progress;
- a new user can complete individual or family onboarding;
- a family owner can create and switch between multiple learner profiles;
- age band and proficiency are stored independently;
- French is selectable through a multilingual-ready model;
- child profiles receive safe default permissions;
- profile-specific progress is isolated by enforced authorization;
- onboarding works on phone and tablet widths;
- migrations, RLS, tests and production build pass, or any external blocker is reported accurately;
- no excluded later-phase functionality was introduced;
- production remains unchanged.


