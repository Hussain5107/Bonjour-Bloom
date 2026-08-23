> Historical planning document preserved for context. Some branch names, baseline commits, task boundaries, or deployment instructions may be stale. Current repository state, AGENTS.md, and docs/PROJECT_STATUS.md take precedence.\n\n# Bonjour Bloom — Codex Development Prompt

## Section 3: Personalized Daily Learning Path, Mastery and Spaced Review

Copy this complete prompt into a new Codex conversation. Provide these references with it:

1. `Bonjour_Bloom_Codex_Handoff.md` — authoritative master project brief.
2. `Bonjour_Bloom_Section_1_Codex_Prompt.md` and its implementation report/PR.
3. `Bonjour_Bloom_Section_2_Codex_Prompt.md` and its implementation report/PR.

This prompt controls the bounded scope of Section 3. All safety, privacy, licensing and engineering constraints from the master handoff remain applicable.

---

You are continuing development of the existing **Bonjour Bloom** application.

Repository: `https://github.com/Hussain5107/Bonjour-Bloom`  
Default branch: `main`  
Original verified baseline commit: `5da7b9b613ee895e3dcce98519653005c7dd67b0`

Do not rebuild the application, reset learner data, commit directly to `main`, merge a pull request, or deploy to production without explicit user authorization.

## 1. Objective

Implement the core learning engine that turns published curriculum into a useful daily learning experience.

Section 3 must add:

1. a deterministic, profile-specific daily learning plan;
2. spaced review scheduling for eligible vocabulary, phrases, objectives and exercise items;
3. clear separation between exposure, lesson completion and demonstrated mastery;
4. immutable or append-only learning-attempt events with safely derived mastery state;
5. a focused daily-session interface that fits the learner's saved time target;
6. due-review, continue-learning and new-learning prioritization;
7. transparent progress explanations and manual review controls;
8. age-appropriate presentation without changing scoring standards arbitrarily;
9. robust timezone, retry, duplicate-submission and cross-profile handling;
10. tests proving deterministic scheduling, data isolation and responsive behavior.

Do not use AI to choose or score the daily path. The first implementation must be understandable, testable and reproducible.

## 2. Preconditions and dependency checks

Section 3 depends on:

- Section 1 account/family ownership and learner profiles;
- active learner profile selection;
- stored target language, CEFR level, age band, goal and daily time target;
- profile-specific progress authorization;
- Section 2 language-neutral curriculum hierarchy;
- published lessons, learning objectives, skills and prerequisites;
- validated structured/scorable exercises;
- age-appropriate lesson variants;
- stable curriculum identifiers and content versions.

Before editing, confirm that these dependencies exist on the chosen base branch.

If Section 1 or Section 2 is unmerged but exists on a feature branch/PR, determine the correct dependency branch and report it. Do not duplicate profile, curriculum, objective, lesson or exercise tables. If the dependency state is ambiguous, stop and ask the user which branch/PR is authoritative.

Also confirm that the previously reported mobile horizontal-offset defect is fixed. At mobile widths, main content must not retain a desktop sidebar offset or overflow horizontally. If it is still present, fix the shared layout defect first as a bounded prerequisite and report the change separately.

## 3. Required working method

Before implementation:

1. Read all supplied handoff and prior-section documents completely.
2. Inspect Git status, current branch, HEAD, recent history and open dependency branches available locally/remotely.
3. Inspect learner profiles, curriculum, progress, exercise scoring, authentication, RLS, offline/PWA behavior, routing and test infrastructure.
4. Inventory existing progress/completion records and determine how they map to attempts, completion and mastery without data loss.
5. Run the existing formatter/linter, type-checker, tests and production build.
6. Produce a concise pre-implementation report containing:
   - dependency status;
   - current progress model;
   - proposed attempt/mastery/review model;
   - exact deterministic scheduling rules;
   - daily-plan selection algorithm;
   - migration/backfill strategy;
   - RLS strategy;
   - exact files and migrations expected to change;
   - risks, assumptions and blockers.

If any unresolved decision could corrupt progress, expose another profile's records or make scheduling nondeterministic, stop and ask the user. Otherwise, proceed after reporting the plan.

Create a feature branch such as `feature/daily-learning-engine`. Never commit directly to `main`. Do not deploy.

## 4. Core concepts that must remain separate

Implement distinct concepts for:

### Exposure

The learner viewed, heard or encountered an item. Exposure alone is not mastery.

### Attempt

The learner submitted a response to a scorable activity. Record what was attempted, when, the outcome and relevant context.

### Lesson completion

The learner completed the required sections/items of a lesson version. Completion does not automatically mean every objective is mastered.

### Review state

The current scheduling state for a reviewable item for one learner profile.

### Objective mastery

A derived estimate based on repeated successful evidence across time and, where appropriate, more than one exercise. Do not award mastery merely because content was opened or one easy multiple-choice question was answered correctly.

Use clear naming in schema and code so these states cannot be accidentally treated as interchangeable.

## 5. Learning event and attempt model

Adapt to the existing schema, but retain an append-only or effectively immutable record of meaningful learner activity.

Each attempt/event should be able to retain:

- stable event/attempt identifier;
- learner profile identifier;
- account ownership context where required by RLS;
- target language;
- curriculum/lesson/section/exercise identifiers;
- learning-objective identifier(s);
- content version presented;
- activity type;
- event type such as exposed, started, answered, completed or abandoned;
- submitted answer in a privacy-minimized form appropriate to the exercise;
- deterministic correctness/score;
- hint usage;
- retry number;
- response duration when reliable;
- session identifier;
- client-generated idempotency key;
- server timestamp;
- client timestamp only as supplemental metadata;
- app/version metadata if already supported.

Do not store unnecessary microphone recordings or sensitive free-form content. Section 3 does not add cloud speech or AI analysis.

Do not update historical attempts to rewrite past outcomes when content changes. Associate attempts with the content version that was presented.

## 6. Reviewable units

Support review scheduling for the smallest pedagogically meaningful units available in the Section 2 model, such as:

- vocabulary entries;
- phrases;
- sentence patterns;
- learning objectives;
- specific validated exercise prompts where stable reuse is appropriate.

Do not schedule every UI element independently. Avoid creating duplicate review states for the same underlying knowledge item merely because it appears in several lessons.

Each reviewable unit must have:

- stable identifier;
- language;
- primary skill;
- associated objective(s);
- source curriculum/version;
- active/deprecated status;
- review eligibility flag;
- suitable exercise/rendering modes;
- age/audience suitability inherited or resolved safely.

When content is archived or materially changed, preserve historical review evidence and define how the active review state maps to the replacement version. Never silently erase learner history.

## 7. Deterministic spaced-review scheduler

Implement a documented scheduler behind a provider/interface boundary so it can be tuned or replaced later without rewriting learner data.

Do not blindly copy an algorithm without documenting its assumptions. The initial algorithm may use explicit response grades such as:

- `again` — incorrect or not recalled;
- `hard` — correct with substantial difficulty or hint;
- `good` — correct independently;
- `easy` — correct independently and fluently.

For young learners, labels/icons may be simplified, but stored outcomes must remain normalized. For automatically scored activities, derive the grade using transparent rules. Do not let the learner self-award `easy` when the response was incorrect.

Each learner-profile/item review state should support:

- learning state such as new, learning, review, relearning or suspended;
- first-seen timestamp;
- last-attempt timestamp;
- last-success timestamp;
- next-review timestamp;
- current interval;
- consecutive-success count;
- lapse count;
- total review count;
- recent grade/outcome;
- scheduler version;
- source content version;
- created/updated timestamps.

### Required behavioral rules

At minimum:

- incorrect/new items return soon within a bounded learning sequence;
- successful recall increases the interval;
- `hard` grows more slowly than `good`;
- `easy` may grow faster but must not skip essential introductory evidence;
- a lapse shortens the interval and enters relearning;
- hint use prevents an answer from receiving the strongest grade;
- repeated immediate retries cannot manufacture long-term mastery;
- intervals have documented minimums and maximums;
- scheduler calculations are deterministic for the same inputs;
- all timestamps are stored consistently, preferably UTC;
- local calendar-day display uses the learner/account timezone safely;
- changing timezone cannot duplicate or erase reviews;
- scheduler version is retained for future migrations.

Do not present numerical memory strength or scientific precision that the implementation has not validated.

Place interval constants/configuration in one documented module rather than scattering magic numbers across components. Add unit tests for boundary values and time progression.

## 8. Mastery calculation

Implement an explainable first-version mastery model.

Mastery should be derived from evidence such as:

- independent correctness;
- repeated success separated over time;
- performance across more than one exercise mode where the curriculum supports it;
- reduced reliance on hints;
- successful review after a delay;
- recency and lapses.

Use bounded status categories rather than false precision. A suitable internal/learner-facing model may include:

- not started;
- introduced;
- practising;
- familiar;
- mastered;
- needs review.

Define exact transition rules and test them. A single exposure cannot produce `mastered`. Immediate repetition in one session should have less evidential weight than later retrieval.

When displaying a percentage, it must be based on a documented calculation and labelled as Bonjour Bloom progress/mastery, not a clinical or standardized assessment score.

Objective mastery should aggregate evidence from associated reviewable units without allowing one repeatedly practised item to hide failure across the rest of the objective.

## 9. Daily learning plan algorithm

Generate a bounded plan for the active learner profile using their:

- target language;
- CEFR level;
- age band;
- saved daily target of 5, 10, 15, 20 or 30 minutes;
- published curriculum availability;
- prerequisite completion;
- incomplete lesson/session state;
- due reviews;
- current mastery gaps.

The initial planner must be deterministic and explainable. Use this priority order unless repository evidence supports a safer equivalent:

1. overdue/due review items, capped so they do not consume the entire session indefinitely;
2. resume an incomplete eligible lesson;
3. continue the next published lesson whose prerequisites are met;
4. introduce a bounded number of new reviewable items;
5. finish with a short recap or session summary.

Requirements:

- estimate durations from content metadata and observed duration only when enough reliable data exists;
- never promise exact completion time;
- fit the plan approximately to the saved time target;
- define minimum viable plans for 5-minute learners;
- cap new items per session and make the cap age-aware/configurable;
- avoid overwhelming young learners with large review queues;
- do not permanently hide overdue work—carry it forward predictably;
- never select unpublished, archived, wrong-language or age-inappropriate content;
- enforce curriculum prerequisites;
- do not mislabel Pre-A1/A1 content as advanced when higher-level content is unavailable;
- provide an honest empty state when no eligible published content exists;
- make repeated plan generation for the same profile/day/session idempotent unless progress changes;
- do not let profile switching reuse another learner's plan.

Persist either the generated plan or sufficient versioned inputs to reproduce and audit it. Do not generate contradictory plans on every page refresh.

## 10. Daily-session learner experience

Create a focused, mobile-first flow using existing design components.

The home/dashboard should show:

- active learner;
- today's approximate target;
- due review count;
- continue/resume state;
- one primary `Start today's learning` action;
- an optional `Review now` action;
- concise explanation of why an item is recommended, such as `Due for review` or `Next in your course`.

The session flow should:

- show progress through the current session without trapping the learner;
- allow safe pause and resume;
- persist after refresh or connection interruption;
- prevent accidental double submission;
- show immediate deterministic feedback for supported scorable exercises;
- offer a short explanation/hint after an error;
- support retry without counting unlimited retries as independent mastery evidence;
- allow a learner to exit and return later;
- end with a concise factual summary of completed activities and due-next state.

Do not use manipulative countdowns, shame language or punitive loss messaging. Young learners should receive age-appropriate encouragement, but do not make unsupported performance claims.

## 11. Manual review area for this phase

Implement the minimum functional Review area needed to exercise the scheduler:

- due reviews;
- review by vocabulary/phrase/objective where supported;
- `review ahead` only if it does not corrupt normal scheduling;
- suspend/unsuspend an item for an authorized learner/parent;
- reset/relearn action with confirmation and clear consequences;
- display why an item is due and its current non-technical status.

Do not build the complete Section 4 skill-practice centres. Review modes should reuse Section 2 exercises and remain focused on scheduled retrieval.

## 12. Age-appropriate behavior

Use the profile's age band to adapt presentation, session density and interaction length—not to weaken authorization or fabricate easier proficiency scores.

Possible adaptations:

- ages 5–7: fewer new items, shorter activity groups, larger controls, minimal text instructions;
- ages 8–11: short varied review groups and simple progress language;
- ages 12–17: standard controls with age-appropriate scenarios;
- adults: concise progress detail and optional scheduling information.

Keep scheduler outcomes normalized so progress remains interpretable across profile updates. If age-band changes, preserve history and apply new presentation/session limits going forward.

## 13. Offline, retry and synchronization behavior

Inspect the current PWA/offline implementation before deciding scope.

At minimum:

- prevent lost progress when a response is submitted twice after retry;
- use idempotency keys for write operations;
- queue only safe learner events if the current app already supports offline mutation;
- reconcile server-confirmed events without double counting;
- prefer server timestamps for ordering/security while retaining client time as supplemental data;
- show honest offline/pending/sync-failed states;
- never mark a lesson mastered solely because a client says it was completed;
- do not create a large new offline-sync subsystem if the app has no foundation for it—document the limitation and preserve retry safety online.

## 14. Existing-progress migration

Preserve all existing progress.

Inventory current records and define how legacy fields map to:

- lesson completion;
- attempts, where evidence exists;
- exposure only, where correctness evidence does not exist;
- initial mastery status;
- review state.

Do not infer mastery from ambiguous legacy completion data. A safe default is to preserve completion while marking knowledge as introduced/familiar only when supported by evidence.

The migration/backfill must be:

- idempotent;
- reversible or recoverable within the project's migration practice;
- profile-aware;
- content-version-aware where possible;
- tested using representative existing records;
- documented with unresolved mappings.

## 15. Database, RLS and integrity

Use migrations for all database changes.

RLS and server-side checks must ensure:

- a profile can access only its own plans, attempts, mastery and review state through its owning account;
- switching active profile does not grant ownership of an arbitrary profile identifier;
- learners cannot submit attempts for unpublished or unauthorized content;
- clients cannot directly set themselves to `mastered` or choose arbitrary future review dates;
- scheduler-derived fields are written through trusted logic appropriate to the existing architecture;
- duplicate submissions are constrained/idempotent;
- cross-family/profile access is denied and tested;
- admin/editor curriculum permissions do not automatically grant access to private learner history unless explicitly required and authorized.

Add appropriate indexes for:

- profile + next-review time;
- profile + language + mastery state;
- session/plan lookup;
- attempt idempotency key;
- lesson/objective/item aggregation;
- content version;
- event timestamp.

Add database constraints for valid states, non-negative counters/intervals and unique review state per profile/reviewable unit.

## 16. Observability and privacy-safe diagnostics

Add sufficient diagnostics to investigate scheduling failures without logging sensitive learner content.

Support:

- scheduler version in stored state;
- reason codes for plan selection;
- failure/error category for plan generation or attempt submission;
- privacy-safe metrics such as plan generated, session started/completed, review answered and sync failed;
- no raw microphone recordings;
- no unnecessary child identifiers in logs;
- no secrets, tokens or service-role credentials.

Do not add third-party behavioral tracking or advertising analytics in this phase.

## 17. Testing requirements

Add focused automated tests for:

### Scheduler

- new item learning sequence;
- `again`, `hard`, `good` and `easy` outcomes;
- hint-limited grade;
- interval growth and maximum bounds;
- lapse/relearning behavior;
- repeated same-session retry not creating mastery;
- deterministic output for identical inputs;
- scheduler version persistence;
- UTC/day-boundary and timezone changes.

### Planner

- due reviews prioritized with a cap;
- incomplete lesson resumed;
- prerequisites enforced;
- correct language/CEFR/age variant selected;
- unpublished/archived content excluded;
- 5/10/15/20/30-minute target behavior;
- stable same-day plan when progress is unchanged;
- plan updates correctly after completed work;
- honest empty state.

### Mastery and data integrity

- exposure does not equal mastery;
- one correct answer does not equal mastery;
- delayed repeated success can advance mastery;
- lapses reduce/flag mastery appropriately;
- objective aggregation is not dominated by one item;
- duplicate submission is idempotent;
- legacy progress migration preserves completion without inventing evidence.

### Security and UI

- cross-profile and cross-family RLS denial;
- arbitrary profile-ID substitution denial;
- client cannot set mastery/next-review directly;
- mobile widths 360, 390 and 430 px;
- tablet widths 768 and 1024 px;
- no horizontal overflow;
- bottom navigation does not obscure session controls;
- keyboard navigation, focus and accessible feedback announcements;
- pause/resume and refresh recovery.

Run and report actual results for:

- formatter/linter;
- type-checker;
- unit/integration tests;
- database/RLS tests;
- migration/backfill dry run where available;
- production build;
- focused browser/end-to-end flows if supported.

Do not report a test as passed unless it actually ran. Report skipped tests and environmental blockers precisely.

## 18. Explicit exclusions

Do not implement in Section 3:

- the full free-practice centres for reading, writing, listening and speaking;
- placement tests, level exams or formal certification;
- AI planning, AI scoring, AI explanations or AI reports;
- any cloud AI API;
- pronunciation/accent scoring;
- new cloud speech recognition;
- neural/server TTS or generated-audio caching;
- daily push/email notifications;
- visible streak/reward economies, coins or leaderboards;
- YouTube integration;
- live tutors, human matching, video calls or payments;
- additional language curricula;
- production deployment.

Do not create speculative infrastructure for these phases. Add only clean interfaces needed by the current deterministic learning engine.

## 19. Documentation

Document:

- event, attempt, completion, review and mastery distinctions;
- reviewable-unit model;
- scheduler algorithm, constants and versioning;
- mastery transition rules;
- daily-plan priority and duration logic;
- reason codes shown to learners;
- timezone and day-boundary behavior;
- offline/retry/idempotency behavior;
- existing-progress migration;
- RLS and trusted-write model;
- known limitations and Section 4 integration points.

Add/update an architecture decision record explaining why the initial planner and scheduler are deterministic rather than AI-driven.

## 20. Completion report

When complete, report:

1. concise outcome summary;
2. branch and commit hash(es), if created;
3. files and migrations changed;
4. final attempt/mastery/review data model;
5. exact scheduler rules and version;
6. daily-plan algorithm and caps;
7. legacy-progress migration behavior;
8. RLS and idempotency controls;
9. test/build commands with actual results;
10. browser flows and viewport sizes tested;
11. remaining risks, blockers and manual steps;
12. confirmation that production was not modified;
13. recommended bounded scope for Section 4 without implementing it.

Open a draft pull request only when repository authentication and existing user authorization permit it. Never merge it.

## 21. Definition of done

Section 3 is complete only when:

- existing learners, profiles, curriculum and progress remain intact;
- every active learner receives a deterministic profile-specific daily plan from eligible published content;
- due review, continuation and new learning are balanced within the saved time target;
- attempts are recorded safely and duplicate submissions do not double count;
- exposure, completion and mastery remain distinct;
- review scheduling is deterministic, versioned and tested;
- mastery requires meaningful evidence across time;
- learner plans and review states are isolated by enforced ownership/RLS;
- pause/resume and retry behavior work safely;
- home and session screens fit mobile/tablet viewports without horizontal overflow;
- migrations, automated tests and production build pass, or blockers are reported accurately;
- excluded later-phase functionality was not introduced;
- production remains unchanged.


