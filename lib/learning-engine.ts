import type {Exercise} from './course-model';
import type {AgeBand,MasteryState,Profile} from './progress';

export function normalizeAnswer(value:string){return value.normalize('NFKC').trim().replace(/[.!?…]+$/g,'').trim().replace(/\s+/g,' ').toLocaleLowerCase('fr')}
export function checkAnswer(expected:string,actual:string){return normalizeAnswer(expected)===normalizeAnswer(actual)}
export function exercisesForAge(exercises:Exercise[],age:AgeBand){const suitable=exercises.filter(ex=>ex.ageModes.includes(age));return age==='early'?suitable.filter(ex=>!['dictation','missing-word'].includes(ex.type)):suitable}
export function masteryLabel(item:MasteryState|undefined):'new'|'learning'|'practising'|'familiar'|'mastered'{if(!item)return'new';if(item.lapses>2)return'learning';if(item.repetitions<3)return'practising';if(item.stability<7)return'familiar';return'mastered'}
export function courseProgress(profile:Profile,totalLessons:number){return totalLessons?Math.round((new Set(profile.completed).size/totalLessons)*100):0}
