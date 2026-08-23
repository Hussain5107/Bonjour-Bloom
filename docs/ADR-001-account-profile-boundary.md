# ADR-001: Account authentication and learner-profile learning data

## Decision

Supabase authentication remains attached to one account owner. An individual account owns one active learner profile; a family account may own several. Children do not need email addresses. Language, age band, CEFR level, goals, permissions and all progress are attached to the learner profile.

Age and proficiency are deliberately independent. Age controls presentation and safe defaults. CEFR level controls difficulty. French (`fr`) is the only enabled target language in this release, while language columns use standard codes and do not encode French into account ownership.

## Authorization

RLS derives every profile, permission and progress decision through `learning_accounts.owner_user_id = auth.uid()`. Client-supplied profile IDs are never sufficient. Composite profile/user constraints remain as defense in depth. Under-18 settings default off for microphone, AI, external video, live coaching and reminders.

## Migration and recovery

Migrations 004–005 create one account per existing auth user, preserve existing profile IDs when valid, copy legacy progress into profile-bound rows, and create one neutral adult/default profile only when none exists. Operations are idempotent. The legacy JSON row is retained as a recovery source; rollback means returning application readers to the migration-003 path rather than dropping migrated data.

## Limitations

The parent code remains a casual on-device separation mechanism, not re-authentication. A secure parent challenge, permanent deletion/export workflow and jurisdiction-specific child-privacy/legal review remain required before launch. Placement assessment, notifications and future feature services are intentionally not implemented here.
