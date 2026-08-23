# Bonjour Bloom agent guide

This repository is the canonical source for Bonjour Bloom. Read this file, `README.md`, and `docs/PROJECT_STATUS.md` before changing code.

## Working rules

- Preserve authentication, learner profiles, progress, offline behavior, responsive navigation, child-safety constraints, and the Sites deployment configuration.
- Inspect the current branch, working tree, migrations, tests, and relevant architecture before editing.
- Work on a feature branch. Do not commit directly to `main`.
- Keep changes bounded and add focused tests.
- Run `npm run lint`, `npm run typecheck`, `npm test`, and `npm run build` before proposing a merge.
- Never commit `.env.local`, credentials, tokens, Supabase service-role keys, or recordings.
- Use migrations for database changes and retain least-privilege RLS.
- Treat child microphone data as transient by default.
- Do not deploy unless the user explicitly authorizes production publication.

## Start here

- Product and quick start: `README.md`
- Current implementation and backlog: `docs/PROJECT_STATUS.md`
- Architecture and data flow: `docs/ARCHITECTURE.md`
- Local development and verification: `docs/DEVELOPMENT.md`
- Production deployment and rollback: `docs/DEPLOYMENT.md`
- Content authoring: `CONTENT_AUTHORING.md`
- Privacy constraints: `PRIVACY_DESIGN.md`
- Licensing/reuse audit: `docs/AUDIT_AND_REUSE.md`
- Historical implementation prompts: `docs/history/`
