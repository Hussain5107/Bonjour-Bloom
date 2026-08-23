import type {TeacherVoice} from './audio';
export type AgeBand = 'early' | 'junior' | 'explorer' | 'adult';
export type ReviewGrade='again'|'hard'|'good'|'easy';
export type MasteryState={itemId:string;stability:number;difficulty:number;dueAt:string;lastReviewedAt:string;repetitions:number;lapses:number};
export type Profile = { id:string; name:string; ageBand:AgeBand; avatar:string; dailyGoal:number; completed:string[]; xp:number; minutes:number; reviews:number; teacherVoice?:TeacherVoice; mastery?:Record<string,MasteryState>; speakingAttempts?:number; largeText?:boolean };
export type AppData = { guardianReady:boolean; parentPin:string; activeProfileId:string; profiles:Profile[]; downloaded:boolean; sound:boolean; streak:boolean };
export const initialData: AppData = { guardianReady:false,parentPin:'2468',activeProfileId:'',profiles:[],downloaded:false,sound:true,streak:true };
export function goalPercent(profile: Profile){ return Math.min(100, Math.round((profile.completed.length/profile.dailyGoal)*100)); }
export function reviewDue(completed:number, lastReviewedDaysAgo=2){ return completed > 0 && lastReviewedDaysAgo >= Math.min(7, completed*2); }
export function pronunciationLabel(confidence:number|null){ if(confidence===null || confidence<.35)return 'We couldn’t hear clearly'; if(confidence>=.82)return 'Great'; if(confidence>=.58)return 'Almost'; return 'Let’s practise'; }
export function scheduleReview(previous:MasteryState|undefined,itemId:string,grade:ReviewGrade,now=new Date()):MasteryState{const old=previous||{itemId,stability:0,difficulty:5,dueAt:now.toISOString(),lastReviewedAt:now.toISOString(),repetitions:0,lapses:0};const factor={again:.2,hard:1.2,good:2.2,easy:3.8}[grade],stability=grade==='again'?.5:Math.max(1,(old.stability||1)*factor),difficulty=Math.min(10,Math.max(1,old.difficulty+(grade==='again'?1:grade==='easy'?-.6:0)));return{...old,stability,difficulty,lastReviewedAt:now.toISOString(),dueAt:new Date(now.getTime()+stability*86400000).toISOString(),repetitions:old.repetitions+1,lapses:old.lapses+(grade==='again'?1:0)}}
export function dueItems(mastery:Record<string,MasteryState>|undefined,now=new Date()){return Object.values(mastery||{}).filter(x=>new Date(x.dueAt)<=now).sort((a,b)=>a.dueAt.localeCompare(b.dueAt))}
