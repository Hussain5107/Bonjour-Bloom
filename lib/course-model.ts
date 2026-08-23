import {z} from 'zod';

export const audioAssetSchema=z.object({id:z.string(),normal:z.string().optional(),slow:z.string().optional(),fallbackText:z.string(),locale:z.literal('fr-FR')});
export const vocabularyItemSchema=z.object({id:z.string(),french:z.string().min(1),english:z.string().min(1),article:z.string().default(''),gender:z.enum(['masculine','feminine','neutral','not-applicable']),partOfSpeech:z.string(),ipa:z.string().optional(),pronunciation:z.string(),audio:z.string(),slowAudio:z.string(),image:z.string(),example:z.string(),exampleTranslation:z.string(),cefr:z.literal('Pre-A1'),topic:z.string(),order:z.number().int().nonnegative(),reviewTags:z.array(z.string()).default([])});
export const exerciseOptionSchema=z.object({id:z.string(),label:z.string(),image:z.string().optional(),audioText:z.string().optional()});
export const exerciseSchema=z.object({id:z.string(),type:z.enum(['audio-picture','audio-word','fr-en','en-fr','multiple-choice','missing-word','sentence-order','listening','dictation','pronunciation','dialogue-response','review']),instruction:z.string(),prompt:z.string(),answer:z.string(),options:z.array(exerciseOptionSchema).max(4),audioText:z.string().optional(),ageModes:z.array(z.enum(['early','junior','explorer','adult'])).min(1),explanation:z.string().optional()});
export const lessonSectionSchema=z.object({id:z.string(),title:z.string(),kind:z.enum(['welcome','listen','vocabulary','repeat','practice','dialogue','review','results']),objective:z.string(),exerciseIds:z.array(z.string())});
export const dialogueLineSchema=z.object({speaker:z.string(),french:z.string(),english:z.string(),audio:z.string()});
export const lessonSchema=z.object({id:z.string(),title:z.string(),subtitle:z.string(),outcome:z.string(),minutes:z.number().int().positive(),color:z.string(),objectives:z.array(z.string()).min(1),vocabularyIds:z.array(z.string()).min(1),sections:z.array(lessonSectionSchema).min(4),exercises:z.array(exerciseSchema).min(6),dialogue:z.array(dialogueLineSchema),grammarNotes:z.array(z.object({title:z.string(),body:z.string(),ageModes:z.array(z.string())})),culturalNotes:z.array(z.object({title:z.string(),body:z.string()}))});
export const unitSchema=z.object({id:z.string(),title:z.string(),description:z.string(),lessonIds:z.array(z.string()).min(1)});
export const levelSchema=z.object({id:z.string(),cefr:z.string(),title:z.string(),unitIds:z.array(z.string()).min(1)});
export const courseSchema=z.object({id:z.string(),title:z.string(),version:z.number().int().positive(),levels:z.array(levelSchema),units:z.array(unitSchema),lessons:z.array(lessonSchema),vocabulary:z.array(vocabularyItemSchema),audioAssets:z.array(audioAssetSchema)}).superRefine((course,ctx)=>{const vocab=new Set(course.vocabulary.map(v=>v.id));for(const lesson of course.lessons)for(const id of lesson.vocabularyIds)if(!vocab.has(id))ctx.addIssue({code:'custom',message:`Unknown vocabulary ${id}`,path:['lessons',lesson.id,'vocabularyIds']});});

export type Course=z.infer<typeof courseSchema>;
export type Lesson=z.infer<typeof lessonSchema>;
export type Exercise=z.infer<typeof exerciseSchema>;
export type VocabularyItem=z.infer<typeof vocabularyItemSchema>;

export type LessonAttempt={id:string;profileId:string;lessonId:string;startedAt:string;completedAt?:string;currentActivity:number;secondsSpent:number};
export type ExerciseAttempt={id:string;lessonAttemptId:string;exerciseId:string;correct:boolean;answer:string;hintsUsed:number;createdAt:string};
export type LessonProgress={profileId:string;lessonId:string;status:'new'|'started'|'completed';currentActivity:number;accuracy:number;stars:number;updatedAt:string};
export type VocabularyMastery={profileId:string;vocabularyId:string;state:'new'|'learning'|'practising'|'familiar'|'mastered';dueAt:string;intervalDays:number;lapses:number};
export type DailyGoal={kind:'minutes'|'lesson';target:number};
export type ReviewItem={vocabularyId:string;dueAt:string;reason:'new'|'incorrect'|'scheduled'};
