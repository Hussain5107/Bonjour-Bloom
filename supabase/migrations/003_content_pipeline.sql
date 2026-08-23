create table if not exists public.content_admins (
  user_id uuid primary key references auth.users(id) on delete cascade,
  created_at timestamptz not null default now()
);

create table if not exists public.lesson_content (
  lesson_id text primary key,
  content_version integer not null check (content_version > 0),
  status text not null check (status in ('draft','reviewed','published')),
  title text not null,
  level text not null,
  topic text not null,
  payload jsonb not null,
  provenance jsonb not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

alter table public.content_admins enable row level security;
alter table public.lesson_content enable row level security;

drop policy if exists "Admins read their role" on public.content_admins;
create policy "Admins read their role" on public.content_admins for select using (auth.uid() = user_id);
drop policy if exists "Learners read published content" on public.lesson_content;
create policy "Learners read published content" on public.lesson_content for select using (
  status = 'published' or exists (select 1 from public.content_admins where user_id = auth.uid())
);

create index if not exists lesson_content_status_idx on public.lesson_content(status);

comment on table public.lesson_content is 'Versioned authored lesson content only. Learner profiles and progress are intentionally stored in separate tables.';
