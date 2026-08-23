# Development and onboarding

## Prerequisites

- Node.js 22.13 or newer
- npm
- Git
- Optional: a Supabase project for account and cloud-progress work

## First run

```bash
git clone https://github.com/Hussain5107/Bonjour-Bloom.git
cd Bonjour-Bloom
npm ci
npm run dev
```

Open `http://localhost:3000`.

The local-only learner experience does not require credentials. For Supabase work, copy `.env.example` to `.env.local` and supply only your own environment values. Never commit that file.

## Required verification

Run before opening or updating a pull request:

```bash
npm run lint
npm run typecheck
npm test
npm run validate:content
npm run build
```

Lint may report known warnings, but new errors should not be introduced. Report the actual results; do not claim checks that did not run.

## Common tasks

### Change learner UI

Start with `app/bloom-app.tsx` and the responsive CSS files. Test phone and tablet widths and confirm bottom navigation does not hide content.

### Change curriculum

Read `CONTENT_AUTHORING.md`, edit validated content/models, and run `npm run validate:content` plus tests.

### Change progress or profiles

Inspect `lib/progress.ts`, `lib/offline-store.ts`, Supabase migrations, and RLS together. Preserve legacy data and profile isolation.

### Change audio or pronunciation

Keep playback/TTS separate from pronunciation evidence. Maintain browser French speech fallback and transient recording behavior.

### Change the database

Add a numbered migration under `supabase/migrations/`. Make it idempotent or safely repeatable where practical, add indexes/constraints, and test account/profile ownership through RLS.

## Git workflow

1. Update local `main`.
2. Create a focused feature branch.
3. Change only the bounded task.
4. Run verification.
5. Commit with a clear message.
6. Open a draft pull request.
7. Merge only after review/authorization.
8. Deploy only after explicit production approval.

## Troubleshooting

- If the app shows an expired email-link fragment, open the clean root URL and request a fresh authentication email.
- If local state looks stale, inspect IndexedDB/localStorage migration behavior before clearing any data.
- If the live site differs from GitHub, stop and reconcile the Sites source history; do not force-push or overwrite deployed features.
