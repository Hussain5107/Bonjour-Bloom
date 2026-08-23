> Historical planning document preserved for context. Some branch names, baseline commits, task boundaries, or deployment instructions may be stale. Current repository state, AGENTS.md, and docs/PROJECT_STATUS.md take precedence.\n\n# Bonjour Bloom — Codex Development Prompt

## Section 2: Language-Neutral Curriculum and CEFR Skill Foundation

Copy this complete prompt into a new Codex conversation. Provide these references with it:

1. `Bonjour_Bloom_Codex_Handoff.md` — authoritative master project brief.
2. `Bonjour_Bloom_Section_1_Codex_Prompt.md` — the preceding family-profile phase.
3. The Section 1 implementation report or pull request, if Section 1 has been completed.

This prompt controls the bounded scope of Section 2. All security, privacy, licensing and engineering constraints from the master handoff remain applicable.

---

You are continuing development of the existing **Bonjour Bloom** application.

Repository: `https://github.com/Hussain5107/Bonjour-Bloom`  
Default branch: `main`  
Original verified baseline commit: `5da7b9b613ee895e3dcce98519653005c7dd67b0`

Do not rebuild the application, erase or reset existing data, commit directly to `main`, merge a pull request, or deploy to production without explicit user authorization.

## 1. Objective

Build the curriculum foundation needed for an all-age and eventually multilingual language-learning platform.

Section 2 must add:

1. a language-neutral curriculum hierarchy;
2. normalized CEFR levels and skill taxonomy;
3. age/audience adaptations that reuse shared learning objectives;
4. structured lesson content and exercise definitions;
5. prerequisite and progression relationships;
6. draft → reviewed → published governance;
7. source, licence and attribution provenance;
8. a small, high-quality French Pre-A1/A1 curriculum slice demonstrating the architecture;
9. profile-aware curriculum discovery using the learner settings from Section 1;
10. administrator/editor tooling sufficient to review and publish the pilot content safely.

The result must establish a scalable curriculum model, not merely add more hard-coded lessons.

## 2. Preconditions and stop conditions

Before implementation, verify whether Section 1 is present in the target branch or has been merged.

Required Section 1 capabilities:

- authenticated account ownership;
- learner profiles;
- age band/profile type;
- target language;
- stored CEFR-compatible proficiency;
- profile-specific progress or a documented transition path;
- parent-managed child settings.

If Section 1 is missing, incomplete, or incompatible, do not recreate it independently. Report the dependency gap and either:

- rebase this work on the existing Section 1 feature branch/PR, if safely available; or
- stop and ask the user which branch should be the base.

Do not silently develop competing account/profile tables.

## 3. Required working method

Before editing:

1. Read the master handoff and Section 1 prompt/report completely.
2. Inspect repository status, branches, current HEAD and recent history.
3. Inspect existing lesson data, routes, components, Supabase migrations, RLS, progress logic, audio behavior, admin roles and tests.
4. Identify every existing lesson/content structure that this phase will preserve, adapt or migrate.
5. Run the existing formatter/linter, type-checker, tests and production build.
6. Produce a concise pre-implementation report containing:
   - current content architecture;
   - Section 1 dependency status;
   - proposed curriculum model;
   - migration/backfill strategy for existing lessons and progress;
   - RLS and publishing model;
   - proposed pilot curriculum scope;
   - exact files and migrations expected to change;
   - risks, assumptions and blockers.

If an unresolved issue could cause content loss, broken progress, authorization weakness or duplicate schemas, stop and ask the user. Otherwise, proceed after reporting the plan.

Create a feature branch such as `feature/curriculum-foundation`. Never commit directly to `main`. Do not deploy.

## 4. Curriculum hierarchy

Adapt naming to the existing codebase, but support this logical hierarchy:

```text
Language
  → Curriculum Track
    → CEFR Level
      → Module/Unit
        → Learning Objective
          → Lesson
            → Lesson Section
              → Lesson Item / Exercise
```

Do not force every relationship into separate tables if a simpler normalized model fits the existing architecture. However, the implementation must support:

- stable identifiers/slugs;
- explicit ordering;
- language and locale;
- CEFR level;
- primary and secondary skills;
- topic/theme;
- age/audience suitability;
- prerequisites;
- learning objectives;
- estimated study time;
- version/status information;
- active/deprecated state;
- source and licence provenance;
- created/updated/reviewed/published metadata.

Avoid embedding the complete curriculum as large frontend constants. Published content should be queryable efficiently and cacheable where appropriate.

## 5. Language-neutral architecture

French is the only published target language in this phase, but the universal data model must not assume all languages behave like French.

At minimum, support:

- BCP 47 or consistently normalized language/locale codes;
- source/interface language distinct from target language;
- writing direction (`ltr`/`rtl`);
- script where relevant;
- language-specific feature metadata through an extensible structure;
- localized titles, explanations and instructions;
- target-language text separated from translations/explanations.

Do not put French-only fields such as grammatical gender, liaison or French conjugation directly onto universal curriculum tables. Use typed metadata, language-feature records or existing extensible mechanisms.

Design now for eventual Arabic/right-to-left presentation, but do not implement an Arabic curriculum in this section.

## 6. CEFR and learning activity taxonomy

Use normalized levels:

- `pre_a1`
- `a1`
- `a2`
- `b1`
- `b2`
- `c1`
- `c2`

Use a controlled skill taxonomy that can support both the learner-facing four-skill practice centres and CEFR-style communicative activity:

- reading;
- writing;
- listening;
- speaking;
- spoken interaction;
- written interaction;
- vocabulary;
- grammar;
- pronunciation;
- mediation, reserved for later curriculum use.

Each learning objective and lesson must declare at least one primary skill. It may declare secondary skills.

Do not claim that assigning a CEFR label makes content officially CEFR-certified. Store a mapping rationale or descriptor reference where possible and present the levels as curriculum alignment, not external accreditation.

## 7. Age and audience adaptation

The platform serves all ages. Do not create one identical lesson presentation for everyone, and do not duplicate an entire curriculum for every age band.

Support shared learning objectives with audience-specific variants for:

- children 5–7;
- children 8–11;
- learners 12–15;
- learners 16–17;
- adults 18+.

Variants may differ in:

- vocabulary examples;
- instructions;
- imagery references;
- scenario/context;
- reading length;
- interaction complexity;
- visual density;
- tone;
- expected writing load;
- activity duration.

They must share the same underlying objective when pedagogically equivalent. Avoid inappropriate adult topics for children and childish presentation for adults.

Implement deterministic variant selection based on the active learner profile, with a documented fallback order when an exact age-band variant is unavailable. Never expose an unpublished or unsuitable variant merely because a fallback is missing.

## 8. Learning objectives and progression

Learning objectives must be explicit and measurable. Examples:

- recognize and use basic French greetings;
- introduce oneself using a name and simple greeting;
- identify numbers 0–10 when heard;
- match common classroom objects to spoken words;
- understand a short exchange asking how someone is.

Each objective should support:

- stable identifier;
- learner-facing outcome;
- internal authoring notes;
- language;
- CEFR level;
- skill tags;
- topic tags;
- prerequisite objectives;
- mastery evidence type;
- review/publish status.

Validate prerequisite graphs so they cannot contain cycles. Do not build an adaptive recommendation algorithm yet; expose the ordered/prerequisite data that Section 3 will consume.

## 9. Lesson and exercise schema

Support structured lesson sections/items rather than unvalidated arbitrary HTML.

Create an extensible, versioned exercise contract. The pilot may implement/render only the types needed now, but the schema should safely identify types such as:

- instructional text;
- image/prompt card;
- listen-and-select;
- word-to-meaning match;
- multiple choice;
- true/false;
- sequence/reorder;
- fill-the-gap;
- typed short answer;
- listen-and-repeat placeholder;
- dialogue/story block;
- completion/reflection.

For implemented scorable types, store:

- prompt/instructions;
- answer options where applicable;
- normalized correct answer or accepted answers;
- feedback/explanation;
- skill tag;
- difficulty;
- optional hint;
- content/audio/image references;
- deterministic scoring rule.

Correct answers and author-only notes must not be exposed unnecessarily in a way that makes learner assessment trivial. Use the safest architecture compatible with the current static/client/server setup, document limitations, and do not claim secure examinations in this phase.

Validate structured payloads at runtime and in import/authoring paths. Reject unsupported types or malformed records rather than failing inside the learner UI.

## 10. Content governance and provenance

All curriculum records must support a controlled lifecycle:

- `draft`
- `in_review`
- `reviewed`
- `published`
- `archived`

Adapt to existing status conventions if equivalent.

Rules:

- learners can read only published, active content appropriate to their profile;
- drafts and review content require explicit editor/reviewer/admin permissions;
- publishing is enforced server-side/RLS, not only by hidden buttons;
- a record cannot be published without required metadata and validation;
- retain reviewer identity and review/publish timestamps;
- preserve version history or immutable published versions where feasible;
- edits to published content must not silently alter historical learner progress semantics.

Every imported or externally derived record must be able to retain:

- source name;
- source URL;
- external identifier;
- source licence;
- attribution text;
- source version/commit/date;
- import date;
- reviewer;
- review notes.

Do not copy paid-course content. Do not import the full external French dictionary in this section. Original pilot content is preferred; cite and review any external source used.

## 11. Admin/editor experience

Provide the smallest useful admin/editor workflow for this phase. Reuse existing admin patterns and roles.

Authorized editors/reviewers should be able to:

- list/filter curriculum records by language, CEFR, skill, age band and status;
- preview a lesson as a selected age band;
- inspect objectives, prerequisites and structured exercises;
- validate required metadata;
- move content through permitted review states;
- publish only when authorized;
- archive content without deleting learner history.

Do not build a large visual course-builder if the repository does not already have one. A safe, functional structured editor or seeded-content review interface is sufficient.

Do not add a public self-service content-authoring feature.

## 12. Pilot French curriculum slice

Create a small reviewed vertical slice demonstrating the architecture. Prioritize quality over volume.

Target approximately:

- one French Pre-A1/A1 starter track;
- 2 modules;
- 3 lessons per module, approximately 6 lessons total;
- 3–5 explicit learning objectives per module;
- a balanced set of implemented exercise types;
- age variants sufficient to demonstrate child, teen and adult presentation;
- a total core vocabulary of approximately 40–60 reviewed words/phrases.

Suggested modules:

### Module 1 — Greetings and Introductions

- hello/goodbye;
- asking and saying a name;
- asking how someone is;
- polite basic expressions.

### Module 2 — Numbers and Everyday Basics

- numbers 0–10;
- age-appropriate everyday objects or simple classroom/home vocabulary;
- short recognition and sentence-building activities.

Requirements:

- accurate French spelling and accents;
- natural, age-appropriate examples;
- English explanations/translations initially, unless the existing interface language differs;
- clear provenance marking original Bonjour Bloom content;
- no copied commercial exercises;
- no unsupported claim of official CEFR certification;
- audio controls may reuse existing/browser speech capabilities, but do not build new neural TTS infrastructure.

If high-quality French review cannot be verified during implementation, keep content in `draft` or `in_review` rather than falsely marking it reviewed/published. Clearly report the limitation.

## 13. Learner-facing curriculum discovery

Integrate the published pilot content with the active learner profile from Section 1.

The learner should be able to see:

- current target language;
- selected CEFR level;
- modules/lessons appropriate to that level;
- age-appropriate lesson variant;
- lesson title, skills, objective and estimated duration;
- prerequisite/locked state where applicable;
- existing completion/progress state where the current app supports it.

Do not implement the Section 3 daily recommendation or spaced-repetition engine. For now use deterministic curriculum ordering and prerequisite rules.

If no exact content exists for a learner's level, show an honest, useful empty/coming-later state. Do not mislabel lower-level content as advanced.

## 14. Existing lesson and progress migration

Preserve existing lessons and progress.

Before migration:

- inventory existing lesson identifiers;
- identify identifiers referenced by progress records;
- define stable mappings into the new curriculum structure;
- decide whether existing content is migrated, wrapped by a compatibility layer, or temporarily retained.

The migration must be:

- idempotent;
- recoverable;
- free from destructive resets;
- tested against representative existing data;
- documented with mapping and rollback/recovery notes.

Never delete legacy content or progress merely because it does not yet fit the new model. If uncertain, preserve it and report the unresolved mapping.

## 15. Database, RLS and performance

Use migrations for every database change.

RLS must ensure:

- unauthenticated access follows the current application policy and can read only intentionally public published content, if public learning is supported;
- authenticated learners can read only published/active content available to their profile;
- learners cannot write or publish curriculum records;
- editor/reviewer/admin permissions are explicit and server-enforced;
- cross-tenant/account/profile learner progress remains isolated;
- archived/draft content is not exposed through ordinary learner queries;
- source/licence metadata needed for attribution remains retrievable.

Add indexes for common filters and lookups such as:

- language + status;
- CEFR level + ordering;
- skill + topic;
- curriculum/module/lesson ordering;
- stable slug/external identifier;
- age variant lookup;
- prerequisites.

Avoid sending the entire curriculum to every device. Query and cache only the required published slice.

## 16. Testing requirements

Add focused tests for:

- language-neutral records and French-specific metadata separation;
- CEFR and skill validation;
- curriculum ordering;
- prerequisite cycle prevention;
- age-variant selection and safe fallback;
- malformed exercise rejection;
- deterministic scoring for implemented exercise types;
- draft/review/publish transitions;
- learner denial from draft/archived content;
- unauthorized publish attempts;
- editor/reviewer/admin permissions;
- published-content discovery by learner profile;
- existing lesson/progress migration;
- idempotent seed/import behavior;
- phone/tablet curriculum and lesson rendering;
- keyboard and basic screen-reader accessibility.

Run and report actual results for:

- formatter/linter;
- type-checker;
- unit/integration tests;
- database/RLS tests;
- migration/seed dry run where available;
- production build;
- focused browser/end-to-end flows if supported.

Do not report success for tests that did not run. Identify environmental blockers precisely.

## 17. Explicit exclusions

Do not implement in Section 2:

- spaced-repetition scheduling;
- adaptive daily recommendations;
- placement, topic, skill or level examinations;
- AI generation, AI analysis or AI APIs;
- pronunciation scoring;
- new cloud speech recognition;
- neural/server TTS or generated-audio caching;
- notification delivery or streak logic;
- YouTube integration;
- human coaching, tutor marketplace, video calls or payments;
- complete dictionary import;
- bulk curriculum generation;
- another target-language curriculum;
- production deployment.

Do not create speculative infrastructure for all future features. Add clean extension points only where Section 2 requires them.

## 18. Documentation

Document:

- curriculum hierarchy and entity relationships;
- language-neutral design and language-specific metadata approach;
- CEFR/skill taxonomy;
- age-variant selection and fallback;
- objective/prerequisite model;
- exercise contract and validation;
- content lifecycle and publishing permissions;
- provenance/licensing fields;
- existing-content migration;
- seed/import procedure and idempotency;
- limitations and Section 3 integration points.

Add or update an architecture decision record explaining why shared objectives use audience-specific variants instead of duplicating complete curricula.

## 19. Completion report

When complete, report:

1. outcome summary;
2. branch and commit hash(es), if created;
3. files and migrations changed;
4. final curriculum model;
5. RLS and publishing controls;
6. existing lesson/progress migration behavior;
7. exact pilot curriculum delivered and its review status;
8. tests/build commands with actual results;
9. screenshots or precise browser-flow results;
10. remaining risks, blockers and manual steps;
11. confirmation that production was not modified;
12. recommended bounded scope for Section 3 without implementing it.

Open a draft pull request only when repository authentication and prior user authorization permit it. Never merge it.

## 20. Definition of done

Section 2 is complete only when:

- existing accounts, profiles, lessons and progress remain intact;
- curriculum data is language-neutral and supports future languages without pretending they are currently available;
- CEFR level, skills, objectives and prerequisites are normalized;
- age-appropriate variants reuse shared objectives;
- published content is separated from drafts through enforced authorization;
- provenance and licensing metadata are retained;
- approximately six coherent pilot French lessons demonstrate the complete model;
- learner discovery uses the active profile's language, level and age band;
- invalid exercise data fails safely;
- migrations, tests and production build pass, or blockers are reported accurately;
- excluded later-phase functionality was not introduced;
- production remains unchanged.


