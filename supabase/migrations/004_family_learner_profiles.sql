-- Section 1: account-level authentication with profile-level learning data.
-- Additive and idempotent: legacy user_learning_state remains available during migration.
create table if not exists public.learning_accounts (
  id uuid primary key default gen_random_uuid(),
  owner_user_id uuid not null unique references auth.users(id) on delete cascade,
  account_mode text not null default 'individual' check (account_mode in ('individual','family')),
  active_profile_id uuid,
  onboarding_completed_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

alter table public.learner_profiles add column if not exists account_id uuid references public.learning_accounts(id) on delete cascade;
alter table public.learner_profiles add column if not exists profile_type text not null default 'adult' check (profile_type in ('child','teen','adult'));
alter table public.learner_profiles add column if not exists age_band text not null default '18+' check (age_band in ('5-7','8-11','12-15','16-17','18+'));
alter table public.learner_profiles add column if not exists target_language_code text not null default 'fr';
alter table public.learner_profiles add column if not exists interface_language_code text not null default 'en';
alter table public.learner_profiles add column if not exists proficiency_level text not null default 'pre_a1' check (proficiency_level in ('pre_a1','a1','a2','b1','b2','c1','c2'));
alter table public.learner_profiles add column if not exists level_confirmed boolean not null default false;
alter table public.learner_profiles add column if not exists learning_goal text not null default 'general' check (learning_goal in ('general','school','travel','conversation','work_business','exam_prep'));
alter table public.learner_profiles add column if not exists daily_target_minutes integer not null default 10 check (daily_target_minutes in (5,10,15,20,30));
alter table public.learner_profiles add column if not exists archived_at timestamptz;

create table if not exists public.learner_feature_permissions (
  profile_id uuid primary key references public.learner_profiles(id) on delete cascade,
  microphone_enabled boolean not null default false,
  ai_features_enabled boolean not null default false,
  external_videos_enabled boolean not null default false,
  live_coaching_enabled boolean not null default false,
  reminders_enabled boolean not null default false,
  updated_at timestamptz not null default now()
);

create table if not exists public.onboarding_states (
  account_id uuid primary key references public.learning_accounts(id) on delete cascade,
  current_step integer not null default 0 check (current_step between 0 and 8),
  draft jsonb not null default '{}'::jsonb,
  completed_at timestamptz,
  updated_at timestamptz not null default now()
);

insert into public.learning_accounts (owner_user_id,account_mode,onboarding_completed_at)
select id,'individual',now() from auth.users on conflict (owner_user_id) do nothing;

update public.learner_profiles p set account_id=a.id
from public.learning_accounts a where p.user_id=a.owner_user_id and p.account_id is null;

-- Neutral migration defaults: do not infer child status or exact age from legacy modes.
update public.learner_profiles set
  profile_type='adult',age_band='18+',target_language_code='fr',interface_language_code='en',
  proficiency_level='pre_a1',level_confirmed=false,learning_goal='general',
  daily_target_minutes=case when daily_goal<=1 then 5 when daily_goal=2 then 10 else 15 end
where account_id is not null;

insert into public.learner_feature_permissions(profile_id,microphone_enabled,ai_features_enabled,external_videos_enabled,live_coaching_enabled,reminders_enabled)
select id,false,false,false,false,false from public.learner_profiles on conflict(profile_id) do nothing;

update public.learning_accounts a set active_profile_id=(
  select p.id from public.learner_profiles p where p.account_id=a.id and p.archived_at is null order by p.created_at limit 1
) where a.active_profile_id is null;

create or replace function public.ensure_learning_account()
returns public.learning_accounts language plpgsql security definer set search_path=public as $$
declare result public.learning_accounts;
begin
  insert into public.learning_accounts(owner_user_id) values(auth.uid()) on conflict(owner_user_id) do update set updated_at=now() returning * into result;
  return result;
end $$;
revoke all on function public.ensure_learning_account() from public;
grant execute on function public.ensure_learning_account() to authenticated;

alter table public.learning_accounts enable row level security;
alter table public.learner_feature_permissions enable row level security;
alter table public.onboarding_states enable row level security;

drop policy if exists "Owners manage learning account" on public.learning_accounts;
create policy "Owners manage learning account" on public.learning_accounts for all using(auth.uid()=owner_user_id) with check(auth.uid()=owner_user_id);
drop policy if exists "Owners manage feature permissions" on public.learner_feature_permissions;
create policy "Owners manage feature permissions" on public.learner_feature_permissions for all using(exists(select 1 from public.learner_profiles p join public.learning_accounts a on a.id=p.account_id where p.id=profile_id and a.owner_user_id=auth.uid())) with check(exists(select 1 from public.learner_profiles p join public.learning_accounts a on a.id=p.account_id where p.id=profile_id and a.owner_user_id=auth.uid()));
drop policy if exists "Owners manage onboarding" on public.onboarding_states;
create policy "Owners manage onboarding" on public.onboarding_states for all using(exists(select 1 from public.learning_accounts a where a.id=account_id and a.owner_user_id=auth.uid())) with check(exists(select 1 from public.learning_accounts a where a.id=account_id and a.owner_user_id=auth.uid()));

drop policy if exists "Account owns learner profiles" on public.learner_profiles;
create policy "Account owns learner profiles" on public.learner_profiles for all using(exists(select 1 from public.learning_accounts a where a.id=account_id and a.owner_user_id=auth.uid())) with check(user_id=auth.uid() and exists(select 1 from public.learning_accounts a where a.id=account_id and a.owner_user_id=auth.uid()));
drop policy if exists "Account owns learner progress" on public.learner_progress;
create policy "Account owns learner progress" on public.learner_progress for all using(exists(select 1 from public.learner_profiles p join public.learning_accounts a on a.id=p.account_id where p.id=profile_id and a.owner_user_id=auth.uid())) with check(user_id=auth.uid() and exists(select 1 from public.learner_profiles p join public.learning_accounts a on a.id=p.account_id where p.id=profile_id and a.owner_user_id=auth.uid()));
drop policy if exists "Account owns lesson completions" on public.lesson_completions;
create policy "Account owns lesson completions" on public.lesson_completions for all using(exists(select 1 from public.learner_profiles p join public.learning_accounts a on a.id=p.account_id where p.id=profile_id and a.owner_user_id=auth.uid())) with check(user_id=auth.uid() and exists(select 1 from public.learner_profiles p join public.learning_accounts a on a.id=p.account_id where p.id=profile_id and a.owner_user_id=auth.uid()));

create index if not exists learner_profiles_account_active_idx on public.learner_profiles(account_id,archived_at);
create index if not exists learner_profiles_language_level_idx on public.learner_profiles(target_language_code,proficiency_level);
create index if not exists learner_progress_profile_user_idx on public.learner_progress(profile_id,user_id);

alter table public.learning_accounts drop constraint if exists learning_accounts_active_profile_id_fkey;
alter table public.learning_accounts add constraint learning_accounts_active_profile_id_fkey foreign key(active_profile_id) references public.learner_profiles(id) on delete set null;

comment on table public.learning_accounts is 'Authentication remains account-level; active learner and account mode are owner-managed.';
comment on table public.learner_feature_permissions is 'Privacy-by-design product settings. Device/browser permissions are separate.';
