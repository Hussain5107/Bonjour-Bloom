create table if not exists public.learner_profiles (
  id uuid primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  display_name text not null check (char_length(display_name) between 1 and 40),
  avatar text not null,
  age_mode text not null check (age_mode in ('early','junior','explorer','adult')),
  daily_goal integer not null default 1 check (daily_goal between 1 and 180),
  audio_settings jsonb not null default '{}'::jsonb,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (user_id, id)
);

create table if not exists public.learner_progress (
  profile_id uuid primary key references public.learner_profiles(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  current_lesson text,
  current_activity integer not null default 0 check (current_activity >= 0),
  completed_lessons jsonb not null default '[]'::jsonb,
  vocabulary_mastery jsonb not null default '{}'::jsonb,
  exercise_attempts jsonb not null default '[]'::jsonb,
  accuracy numeric not null default 0 check (accuracy between 0 and 1),
  voice_practice_count integer not null default 0,
  streak integer not null default 0,
  stars integer not null default 0,
  last_activity_at timestamptz,
  updated_at timestamptz not null default now(),
  foreign key (user_id, profile_id) references public.learner_profiles(user_id, id) on delete cascade
);

create table if not exists public.lesson_completions (
  profile_id uuid not null references public.learner_profiles(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  lesson_id text not null,
  stars integer not null default 0,
  accuracy numeric not null default 0 check (accuracy between 0 and 1),
  completed_at timestamptz not null default now(),
  primary key (profile_id, lesson_id),
  foreign key (user_id, profile_id) references public.learner_profiles(user_id, id) on delete cascade
);

alter table public.learner_profiles enable row level security;
alter table public.learner_progress enable row level security;
alter table public.lesson_completions enable row level security;

drop policy if exists "Account owns learner profiles" on public.learner_profiles;
create policy "Account owns learner profiles" on public.learner_profiles for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
drop policy if exists "Account owns learner progress" on public.learner_progress;
create policy "Account owns learner progress" on public.learner_progress for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
drop policy if exists "Account owns lesson completions" on public.lesson_completions;
create policy "Account owns lesson completions" on public.lesson_completions for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create index if not exists learner_profiles_user_id_idx on public.learner_profiles(user_id);
create index if not exists learner_progress_user_id_idx on public.learner_progress(user_id);
create index if not exists lesson_completions_user_id_idx on public.lesson_completions(user_id);
