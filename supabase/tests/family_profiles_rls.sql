-- Run with the repository's future local Supabase test harness.
-- Negative assertions required for Section 1:
-- 1. Set request.jwt.claim.sub to account A and verify account B profiles return zero rows.
-- 2. Attempt learner_progress insert using account B profile_id; expect RLS violation.
-- 3. Attempt learner_feature_permissions update for account B child; expect RLS violation.
-- 4. Retry ensure_learning_account() and onboarding completion; expect one account/profile.
begin;
select plan(1);
select ok(
  position('learning_accounts' in pg_get_expr(polqual,polrelid))>0,
  'learner progress policy derives access through profile account ownership'
) from pg_policy where polname='Account owns learner progress';
select * from finish();
rollback;
