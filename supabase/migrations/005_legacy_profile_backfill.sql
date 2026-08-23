-- Idempotent recovery/backfill from the legacy account JSON row.
-- Existing age data is not considered reliable enough to infer child status.
with legacy_profiles as (
 select s.user_id,a.id account_id,p.value profile,
   case when coalesce(p.value->>'id','') ~* '^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$' then (p.value->>'id')::uuid else gen_random_uuid() end profile_id
 from public.user_learning_state s
 join public.learning_accounts a on a.owner_user_id=s.user_id
 cross join lateral jsonb_array_elements(case when jsonb_typeof(s.state->'profiles')='array' then s.state->'profiles' else '[]'::jsonb end) p
)
insert into public.learner_profiles(id,user_id,account_id,display_name,avatar,age_mode,daily_goal,profile_type,age_band,target_language_code,interface_language_code,proficiency_level,level_confirmed,learning_goal,daily_target_minutes,audio_settings,updated_at)
select profile_id,user_id,account_id,left(coalesce(nullif(profile->>'name',''),'Learner'),40),coalesce(nullif(profile->>'avatar',''),'🦊'),'adult',greatest(1,least(180,coalesce((profile->>'dailyGoal')::int,1))),'adult','18+','fr','en',coalesce(nullif(profile->>'proficiency',''),'pre_a1'),coalesce((profile->>'levelConfirmed')::boolean,false),coalesce(nullif(profile->>'learningGoal',''),'general'),case when coalesce((profile->>'dailyTargetMinutes')::int,10) in(5,10,15,20,30) then coalesce((profile->>'dailyTargetMinutes')::int,10) else 10 end,'{}'::jsonb,now()
from legacy_profiles on conflict(id) do nothing;

-- Users with no usable legacy profile receive one neutral adult profile exactly once.
insert into public.learner_profiles(id,user_id,account_id,display_name,avatar,age_mode,daily_goal,profile_type,age_band,target_language_code,interface_language_code,proficiency_level,level_confirmed,learning_goal,daily_target_minutes)
select gen_random_uuid(),a.owner_user_id,a.id,'Learner','🦊','adult',1,'adult','18+','fr','en','pre_a1',false,'general',10
from public.learning_accounts a where not exists(select 1 from public.learner_profiles p where p.account_id=a.id)
on conflict do nothing;

with source as (
 select p.id profile_id,p.user_id,j.value profile from public.learner_profiles p
 join public.user_learning_state s on s.user_id=p.user_id
 cross join lateral jsonb_array_elements(case when jsonb_typeof(s.state->'profiles')='array' then s.state->'profiles' else '[]'::jsonb end) j
 where j.value->>'id'=p.id::text
)
insert into public.learner_progress(profile_id,user_id,current_lesson,current_activity,completed_lessons,vocabulary_mastery,exercise_attempts,voice_practice_count,streak,stars,last_activity_at,updated_at)
select profile_id,user_id,nullif(profile->>'currentLesson',''),greatest(0,coalesce((profile->>'currentActivity')::int,0)),coalesce(profile->'completed','[]'::jsonb),coalesce(profile->'mastery','{}'::jsonb),coalesce(profile->'attempts','[]'::jsonb),greatest(0,coalesce((profile->>'speakingAttempts')::int,0)),greatest(0,coalesce((profile->>'streakDays')::int,0)),greatest(0,coalesce((profile->>'xp')::int,0)),coalesce((profile->>'lastActivityDate')::timestamptz,now()),now()
from source on conflict(profile_id) do nothing;

insert into public.learner_progress(profile_id,user_id)
select p.id,p.user_id from public.learner_profiles p where not exists(select 1 from public.learner_progress x where x.profile_id=p.id)
on conflict(profile_id) do nothing;

insert into public.learner_feature_permissions(profile_id)
select p.id from public.learner_profiles p on conflict(profile_id) do nothing;

update public.learning_accounts a set active_profile_id=(
  select p.id from public.learner_profiles p where p.account_id=a.id and p.archived_at is null order by p.created_at,p.id limit 1
),onboarding_completed_at=coalesce(a.onboarding_completed_at,now()) where a.active_profile_id is null;

-- Recovery: the legacy JSON row is deliberately retained. Roll back application code
-- to migration 003 readers if recovery is required; do not drop new data automatically.
