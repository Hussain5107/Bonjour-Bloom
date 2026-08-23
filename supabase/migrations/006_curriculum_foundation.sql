-- Section 2: language-neutral curriculum foundation. Additive to migrations 003-005.
create type public.curriculum_state as enum ('draft','in_review','reviewed','published','archived');
create type public.curriculum_role as enum ('author','reviewer','publisher');

create table public.curriculum_tracks (
  id uuid primary key default gen_random_uuid(), slug text unique not null, language_code text not null check (language_code ~ '^[a-z]{2,3}$'),
  title text not null, is_active boolean not null default true, created_at timestamptz not null default now(), updated_at timestamptz not null default now()
);
create table public.curriculum_modules (
  id uuid primary key default gen_random_uuid(), track_id uuid not null references public.curriculum_tracks on delete cascade,
  slug text not null, title text not null, sequence smallint not null check(sequence>0), unique(track_id,slug), unique(track_id,sequence)
);
create table public.learning_objectives (
  id uuid primary key default gen_random_uuid(), track_id uuid not null references public.curriculum_tracks on delete cascade,
  slug text not null, cefr_level text not null check(cefr_level in ('pre_a1','a1','a2','b1','b2','c1','c2')),
  skill_codes text[] not null check(cardinality(skill_codes)>0), statement text not null, unique(track_id,slug)
);
create table public.objective_prerequisites (
  objective_id uuid not null references public.learning_objectives on delete cascade,
  prerequisite_id uuid not null references public.learning_objectives on delete restrict,
  primary key(objective_id,prerequisite_id), check(objective_id<>prerequisite_id)
);
create table public.curriculum_lessons (
  id uuid primary key default gen_random_uuid(), module_id uuid not null references public.curriculum_modules on delete cascade,
  slug text not null, language_code text not null check(language_code ~ '^[a-z]{2,3}$'), cefr_level text not null check(cefr_level in ('pre_a1','a1','a2','b1','b2','c1','c2')),
  sequence smallint not null check(sequence>0), state public.curriculum_state not null default 'draft', provenance jsonb not null,
  reviewed_by uuid references auth.users, reviewed_at timestamptz, published_by uuid references auth.users, published_at timestamptz,
  version integer not null default 1 check(version>0), created_by uuid references auth.users, created_at timestamptz not null default now(), updated_at timestamptz not null default now(),
  unique(module_id,slug), unique(module_id,sequence), check(state not in ('reviewed','published') or (reviewed_by is not null and reviewed_at is not null)),
  check(state <> 'published' or (published_by is not null and published_at is not null))
);
create table public.lesson_objectives (lesson_id uuid not null references public.curriculum_lessons on delete cascade,objective_id uuid not null references public.learning_objectives on delete restrict,primary key(lesson_id,objective_id));
create table public.lesson_variants (
  id uuid primary key default gen_random_uuid(), lesson_id uuid not null references public.curriculum_lessons on delete cascade,
  audience text not null check(audience in ('child','teen','adult')), title text not null, intro text not null, content jsonb not null,
  unique(lesson_id,audience), check(jsonb_typeof(content)='object')
);
create table public.curriculum_editor_roles (user_id uuid not null references auth.users on delete cascade,role public.curriculum_role not null,primary key(user_id,role));
create table public.legacy_lesson_mappings (legacy_lesson_id text primary key,lesson_id uuid unique not null references public.curriculum_lessons on delete cascade,notes text);

create or replace function public.curriculum_has_role(required public.curriculum_role) returns boolean language sql stable security definer set search_path=public as $$
 select exists(select 1 from public.curriculum_editor_roles where user_id=auth.uid() and (role=required or role='publisher'));
$$;
create or replace function public.prevent_objective_cycle() returns trigger language plpgsql as $$
begin
 if exists(with recursive chain(id) as (select new.prerequisite_id union all select op.prerequisite_id from public.objective_prerequisites op join chain c on op.objective_id=c.id) select 1 from chain where id=new.objective_id) then raise exception 'Objective prerequisite cycle'; end if;
 return new;
end $$;
create trigger objective_cycle_guard before insert or update on public.objective_prerequisites for each row execute function public.prevent_objective_cycle();
create or replace function public.transition_curriculum_lesson(lesson uuid, target public.curriculum_state) returns public.curriculum_lessons language plpgsql security definer set search_path=public as $$
declare current public.curriculum_lessons;
begin
 select * into current from public.curriculum_lessons where id=lesson for update;
 if current.id is null then raise exception 'Lesson not found'; end if;
 if target='in_review' and current.state='draft' and public.curriculum_has_role('author') then null;
 elsif target='reviewed' and current.state='in_review' and public.curriculum_has_role('reviewer') then update public.curriculum_lessons set reviewed_by=auth.uid(),reviewed_at=now() where id=current.id;
 elsif target='published' and current.state='reviewed' and public.curriculum_has_role('publisher') then update public.curriculum_lessons set published_by=auth.uid(),published_at=now() where id=current.id;
 elsif target='archived' and current.state<>'archived' and public.curriculum_has_role('publisher') then null;
 else raise exception 'Invalid or unauthorized curriculum transition'; end if;
 update public.curriculum_lessons set state=target,updated_at=now() where id=current.id returning * into current; return current;
end $$;

alter table public.curriculum_tracks enable row level security;alter table public.curriculum_modules enable row level security;alter table public.learning_objectives enable row level security;alter table public.objective_prerequisites enable row level security;alter table public.curriculum_lessons enable row level security;alter table public.lesson_objectives enable row level security;alter table public.lesson_variants enable row level security;alter table public.curriculum_editor_roles enable row level security;alter table public.legacy_lesson_mappings enable row level security;
create policy "active tracks readable" on public.curriculum_tracks for select using(is_active or public.curriculum_has_role('author'));
create policy "modules readable for active tracks" on public.curriculum_modules for select using(exists(select 1 from public.curriculum_tracks t where t.id=track_id and t.is_active) or public.curriculum_has_role('author'));
create policy "objectives readable" on public.learning_objectives for select using(true);create policy "prerequisites readable" on public.objective_prerequisites for select using(true);
create policy "published lessons or editors" on public.curriculum_lessons for select using(state='published' or public.curriculum_has_role('author'));
create policy "published lesson objectives or editors" on public.lesson_objectives for select using(exists(select 1 from public.curriculum_lessons l where l.id=lesson_id and l.state='published') or public.curriculum_has_role('author'));
create policy "published variants or editors" on public.lesson_variants for select using(exists(select 1 from public.curriculum_lessons l where l.id=lesson_id and l.state='published') or public.curriculum_has_role('author'));
create policy "editors author tracks" on public.curriculum_tracks for all using(public.curriculum_has_role('author')) with check(public.curriculum_has_role('author'));
create policy "editors author modules" on public.curriculum_modules for all using(public.curriculum_has_role('author')) with check(public.curriculum_has_role('author'));
create policy "editors author objectives" on public.learning_objectives for all using(public.curriculum_has_role('author')) with check(public.curriculum_has_role('author'));
create policy "editors author prerequisites" on public.objective_prerequisites for all using(public.curriculum_has_role('author')) with check(public.curriculum_has_role('author'));
create policy "editors author lessons" on public.curriculum_lessons for insert with check(public.curriculum_has_role('author') and state='draft');
create policy "editors update lessons" on public.curriculum_lessons for update using(public.curriculum_has_role('author')) with check(public.curriculum_has_role('author'));
create policy "editors author objectives map" on public.lesson_objectives for all using(public.curriculum_has_role('author')) with check(public.curriculum_has_role('author'));
create policy "editors author variants" on public.lesson_variants for all using(public.curriculum_has_role('author')) with check(public.curriculum_has_role('author'));
create policy "own editor roles readable" on public.curriculum_editor_roles for select using(user_id=auth.uid());
create policy "legacy mappings readable" on public.legacy_lesson_mappings for select using(true);create policy "editors author mappings" on public.legacy_lesson_mappings for all using(public.curriculum_has_role('author')) with check(public.curriculum_has_role('author'));
grant select on public.curriculum_tracks,public.curriculum_modules,public.learning_objectives,public.objective_prerequisites,public.curriculum_lessons,public.lesson_objectives,public.lesson_variants,public.legacy_lesson_mappings to anon,authenticated;
grant insert,update,delete on public.curriculum_tracks,public.curriculum_modules,public.learning_objectives,public.objective_prerequisites,public.curriculum_lessons,public.lesson_objectives,public.lesson_variants,public.legacy_lesson_mappings to authenticated;
grant execute on function public.transition_curriculum_lesson(uuid,public.curriculum_state) to authenticated;

create or replace function public.enforce_curriculum_state_transition() returns trigger language plpgsql security definer set search_path=public as $$
begin
 if new.state=old.state then return new; end if;
 if old.state='draft' and new.state='in_review' and public.curriculum_has_role('author') then return new; end if;
 if old.state='in_review' and new.state='draft' and public.curriculum_has_role('author') then return new; end if;
 if old.state='in_review' and new.state='reviewed' and public.curriculum_has_role('reviewer') and new.reviewed_by=auth.uid() and new.reviewed_at is not null then return new; end if;
 if old.state='reviewed' and new.state='in_review' and public.curriculum_has_role('reviewer') then return new; end if;
 if old.state='reviewed' and new.state='published' and public.curriculum_has_role('publisher') and new.published_by=auth.uid() and new.published_at is not null then return new; end if;
 if new.state='archived' and public.curriculum_has_role('publisher') then return new; end if;
 raise exception 'Invalid or unauthorized curriculum transition';
end $$;
create trigger curriculum_state_guard before update of state on public.curriculum_lessons for each row execute function public.enforce_curriculum_state_transition();

create or replace function public.curriculum_is_editor() returns boolean language sql stable security definer set search_path=public as $$
 select exists(select 1 from public.curriculum_editor_roles where user_id=auth.uid());
$$;
drop policy "published lessons or editors" on public.curriculum_lessons;
create policy "published lessons or editors" on public.curriculum_lessons for select using(state='published' or public.curriculum_is_editor());
drop policy "published lesson objectives or editors" on public.lesson_objectives;
create policy "published lesson objectives or editors" on public.lesson_objectives for select using(exists(select 1 from public.curriculum_lessons l where l.id=lesson_id and l.state='published') or public.curriculum_is_editor());
drop policy "published variants or editors" on public.lesson_variants;
create policy "published variants or editors" on public.lesson_variants for select using(exists(select 1 from public.curriculum_lessons l where l.id=lesson_id and l.state='published') or public.curriculum_is_editor());

-- Complete universal metadata and common query indexes.
alter table public.curriculum_tracks add column interface_language text not null default 'en', add column locale text not null default 'en', add column writing_direction text not null default 'ltr' check(writing_direction in ('ltr','rtl')), add column script text, add column metadata jsonb not null default '{}'::jsonb;
alter table public.learning_objectives add column primary_skill text not null default 'vocabulary', add column secondary_skills text[] not null default '{}', add column topic_tags text[] not null default '{}', add column authoring_notes text not null default '', add column mastery_evidence text not null default 'recognition', add column state public.curriculum_state not null default 'draft', add column cefr_rationale text not null default '';
alter table public.curriculum_lessons add column locale text not null default 'fr-FR', add column primary_skill text not null default 'vocabulary', add column secondary_skills text[] not null default '{}', add column topic_tags text[] not null default '{}', add column estimated_minutes smallint not null default 8 check(estimated_minutes>0), add column active boolean not null default true, add column deprecated boolean not null default false;
create index curriculum_tracks_language_active_idx on public.curriculum_tracks(language_code,is_active);
create index curriculum_modules_track_sequence_idx on public.curriculum_modules(track_id,sequence);
create index learning_objectives_level_skill_idx on public.learning_objectives(cefr_level,primary_skill);
create index learning_objectives_topics_idx on public.learning_objectives using gin(topic_tags);
create index curriculum_lessons_discovery_idx on public.curriculum_lessons(language_code,cefr_level,state,active,sequence);
create index curriculum_lessons_skill_idx on public.curriculum_lessons(primary_skill);
create index curriculum_lessons_topics_idx on public.curriculum_lessons using gin(topic_tags);
create index lesson_variants_audience_idx on public.lesson_variants(lesson_id,audience);
create index objective_prerequisites_lookup_idx on public.objective_prerequisites(prerequisite_id);

create or replace function public.protect_published_curriculum() returns trigger language plpgsql as $$
begin
 if old.state='published' and new.state='published' then raise exception 'Published curriculum versions are immutable; create a new version'; end if;
 return new;
end $$;
create trigger published_curriculum_immutable before update on public.curriculum_lessons for each row execute function public.protect_published_curriculum();

create or replace function public.transition_curriculum_lesson(lesson uuid, target public.curriculum_state) returns public.curriculum_lessons language plpgsql security definer set search_path=public as $$
declare current public.curriculum_lessons; variant_count integer; objective_count integer;
begin
 select * into current from public.curriculum_lessons where id=lesson for update;
 if current.id is null then raise exception 'Lesson not found'; end if;
 if target='in_review' and current.state='draft' and public.curriculum_has_role('author') then null;
 elsif target='reviewed' and current.state='in_review' and public.curriculum_has_role('reviewer') then
   select count(*) into variant_count from public.lesson_variants where lesson_id=current.id;
   select count(*) into objective_count from public.lesson_objectives where lesson_id=current.id;
   if variant_count<1 or objective_count<1 or not(current.provenance ?& array['createdBy','sourceName','license','attribution']) then raise exception 'Curriculum metadata validation failed'; end if;
   update public.curriculum_lessons set reviewed_by=auth.uid(),reviewed_at=now() where id=current.id;
 elsif target='published' and current.state='reviewed' and public.curriculum_has_role('publisher') then
   if current.reviewed_by is null or current.reviewed_at is null then raise exception 'Recorded review is required'; end if;
   update public.curriculum_lessons set published_by=auth.uid(),published_at=now() where id=current.id;
 elsif target='archived' and current.state<>'archived' and public.curriculum_has_role('publisher') then null;
 else raise exception 'Invalid or unauthorized curriculum transition'; end if;
 update public.curriculum_lessons set state=target,updated_at=now() where id=current.id returning * into current; return current;
end $$;
