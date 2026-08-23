import {describe,expect,it} from 'vitest';
import {normalizeAppData} from './progress';
import {preferPersistentState} from './offline-store';

describe('mobile profile persistence',()=>{
 const empty=normalizeAppData({profiles:[],activeProfileId:''});
 const saved=normalizeAppData({guardianReady:true,profiles:[{id:'p1',name:'Camille',ageBand:'junior',avatar:'🦊',dailyGoal:1,completed:[],xp:0,minutes:0,reviews:0}],activeProfileId:'p1'});
 it('recovers a learner from IndexedDB when local storage is empty',()=>{expect(preferPersistentState(saved,empty)?.profiles[0].name).toBe('Camille')});
 it('recovers a learner from local storage when IndexedDB is stale',()=>{expect(preferPersistentState(empty,saved)?.profiles[0].name).toBe('Camille')});
 it('prefers the copy with more learner progress',()=>{const progressed=normalizeAppData({...saved,profiles:[{...saved.profiles[0],completed:['hello'],minutes:8}]});expect(preferPersistentState(saved,progressed)?.profiles[0].completed).toEqual(['hello'])});
});
