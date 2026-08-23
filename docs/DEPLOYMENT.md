# Deployment and rollback runbook

## Production

- Live site: https://bonjour-bloom.ihussain876633.chatgpt.site
- Hosting: OpenAI Sites
- Project binding: `.openai/hosting.json`
- Production access: public
- Source of truth: GitHub `main`

## Prerequisites

- Explicit user approval to publish production
- Clean, reviewed source commit
- Passing typecheck, tests, content validation, and production build
- Access to the existing Sites project
- No credentials committed to Git

## Publish procedure

1. Confirm GitHub `main` contains the exact intended source.
2. Run the required verification commands in `docs/DEVELOPMENT.md`.
3. Build with `npm run build`.
4. Reuse the existing Sites `project_id`; never create a replacement site.
5. Push the exact validated source to the Site's configured source branch without rewriting newer hosting history.
6. Package the successful `dist/` output with the Sites packaging helper.
7. Save a Sites version using the pushed commit SHA.
8. Because this Site is public, obtain explicit approval for public production deployment.
9. Deploy the saved version and wait for a successful terminal status.
10. Open the live URL and smoke-test the changed flow.

## Smoke test

At minimum:

- App loads without an authentication-loop error.
- Existing learner profile and progress remain visible.
- Parent gate accepts the configured local code.
- Profile switching works.
- “Add another learner” opens profile setup and saves an independent profile.
- Phone/tablet navigation remains usable.
- No console error blocks the edited flow.

## Rollback

1. Identify the last known-good saved Sites version.
2. Confirm rollback scope and impact.
3. Redeploy that saved version to production.
4. Verify the live URL and affected user flow.
5. If source rollback is also needed, use a normal Git revert on a feature branch and preserve history; never rewrite `main`.

## Secrets and environment

Runtime secrets belong in Sites environment settings. Public client configuration may use the documented `NEXT_PUBLIC_*` variables, but service-role, speech-provider, database, and private API credentials must remain server-side.
