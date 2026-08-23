import {beforeEach,describe,expect,it,vi} from 'vitest';
import {archiveProfile,createLearnerProfile,normalizeAppData,safePermissions} from './progress';

describe('family learner profiles',()=>{
 let id=0;
 beforeEach(()=>{id=0;vi.stubGlobal('crypto',{randomUUID:()=>`00000000-0000-4000-8000-${String(++id).padStart(12,'0')}`})});
 it('migrates a legacy profile without losing progress',()=>{const data=normalizeAppData({guardianReady:true,parentPin:'2468',activeProfileId:'p1',profiles:[{id:'p1',name:'Existing learner',ageBand:'junior',avatar:'🦊',dailyGoal:2,completed:['hello'],xp:40,minutes:8,reviews:0}],downloaded:false,sound:true,streak:true});expect(data.profiles[0]).toMatchObject({completed:['hello'],learnerAgeBand:'8-11',proficiency:'pre_a1',targetLanguage:'fr'});expect(data.accountMode).toBe('individual')});
 it('uses safe defaults for every under-18 learner',()=>{const p=createLearnerProfile({name:'Camille',learnerAgeBand:'12-15',avatar:'🐼',proficiency:'a1',levelConfirmed:true,learningGoal:'school',dailyTargetMinutes:15});expect(p.profileType).toBe('teen');expect(p.permissions).toEqual(safePermissions)});
 it('keeps age and proficiency independent',()=>{const p=createLearnerProfile({name:'Alex',learnerAgeBand:'18+',avatar:'🦊',proficiency:'c1',levelConfirmed:true,learningGoal:'work_business',dailyTargetMinutes:20});expect(p.learnerAgeBand).toBe('18+');expect(p.proficiency).toBe('c1')});
 it('does not archive the last usable profile',()=>{const p=createLearnerProfile({name:'Solo',learnerAgeBand:'18+',avatar:'🦊',proficiency:'pre_a1',levelConfirmed:false,learningGoal:'general',dailyTargetMinutes:5}),data=normalizeAppData({profiles:[p],activeProfileId:p.id});expect(archiveProfile(data,p.id).profiles[0].archived).toBe(false)});
 it('switches safely when the active profile is archived',()=>{const a=createLearnerProfile({name:'A',learnerAgeBand:'18+',avatar:'🦊',proficiency:'pre_a1',levelConfirmed:false,learningGoal:'general',dailyTargetMinutes:5}),b=createLearnerProfile({name:'B',learnerAgeBand:'8-11',avatar:'🐼',proficiency:'a1',levelConfirmed:true,learningGoal:'school',dailyTargetMinutes:10}),next=archiveProfile(normalizeAppData({profiles:[a,b],activeProfileId:a.id,accountMode:'family'}),a.id);expect(next.activeProfileId).toBe(b.id);expect(next.profiles[0].completed).toEqual([])});
});
