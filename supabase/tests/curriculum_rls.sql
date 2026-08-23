begin;
select plan(5);
select has_table('public','curriculum_lessons','curriculum lessons exist');
select has_table('public','lesson_variants','audience variants exist');
select isnt_empty($$select 1 from pg_tables where schemaname='public' and tablename='curriculum_lessons' and rowsecurity$$,'lessons use RLS');
select isnt_empty($$select 1 from pg_policies where schemaname='public' and tablename='curriculum_lessons' and qual like '%published%'$$,'learner read policy filters publication');
select has_function('public','prevent_objective_cycle',array[]::text[],'cycle guard exists');
select * from finish();rollback;
