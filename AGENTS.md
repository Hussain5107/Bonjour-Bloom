# Bonjour Bloom engineering rules

- Treat code, migrations, tests, and `docs/IMPLEMENTATION_STATUS.md` as the source of truth for completed phases. Read only the current file in `docs/phases/` unless a dependency is unclear.
- Never commit directly to `main`, merge a pull request, deploy, or modify production data without explicit authorization. Preserve existing learner data and unrelated worktree changes.
- Before editing, inspect Git state and the affected code/migrations. Prefer a focused shared fix over page-specific patches.
- Every database change requires an additive migration, RLS, ownership checks, indexes, rollback notes, and tests. Never expose service-role credentials or weaken profile isolation.
- Keep curriculum provenance and lifecycle controls intact. Learners may access only active, published, profile-appropriate content.
- Do not add AI/cloud speech, tracking, notifications, payments, or other later-phase features unless the current phase explicitly requires them.
- Validate proportionately: lint, typecheck, tests, production build, database checks when available, and focused browser flows. Report only commands that actually ran and name blockers precisely.
- Keep implementation reports under 400 words. Report diffs, migrations, test results, risks, and manual steps—never paste complete files.
- Continue one phase in the same conversation. Start a fresh conversation for the next major phase and provide only its phase document.

