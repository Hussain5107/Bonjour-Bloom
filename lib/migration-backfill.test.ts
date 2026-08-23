import {readFileSync} from 'node:fs';
import {describe,expect,it} from 'vitest';

describe('legacy profile migration defaults',()=>{
 it('never inserts a null daily target for legacy profiles',()=>{
  const sql=readFileSync('supabase/migrations/005_legacy_profile_backfill.sql','utf8');
  expect(sql).toContain("then coalesce((profile->>'dailyTargetMinutes')::int,10) else 10 end");
 });
});
