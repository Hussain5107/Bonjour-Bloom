create table if not exists public.user_learning_state (
  user_id uuid primary key references auth.users(id) on delete cascade,
  state jsonb not null default '{}'::jsonb,
  updated_at timestamptz not null default now()
);
alter table public.user_learning_state enable row level security;
create policy "Users read their own learning state" on public.user_learning_state for select using (auth.uid() = user_id);
create policy "Users insert their own learning state" on public.user_learning_state for insert with check (auth.uid() = user_id);
create policy "Users update their own learning state" on public.user_learning_state for update using (auth.uid() = user_id) with check (auth.uid() = user_id);
create index if not exists idx_user_learning_state_updated_at on public.user_learning_state(updated_at);
