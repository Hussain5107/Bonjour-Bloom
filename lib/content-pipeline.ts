import {z} from 'zod';

const id=z.string().regex(/^[a-z0-9]+(?:-[a-z0-9]+)*$/,'must be a lowercase kebab-case ID');
const nonEmpty=z.string().trim().min(1);
const sourceNoteSchema=z.object({url:z.string().url(),title:nonEmpty,accessed_at:z.string().date(),license:z.string().optional()}).strict();
const audioSchema=z.object({id,text:nonEmpty,slow_text:z.string().optional(),locale:z.literal('fr-FR')}).strict();
const vocabularySchema=z.object({id,french:nonEmpty,english:nonEmpty,article:z.string(),gender:z.enum(['masculine','feminine','neutral','not-applicable']),part_of_speech:nonEmpty,ipa:z.string().optional(),pronunciation_guide:nonEmpty,example_french:nonEmpty,example_english:nonEmpty,audio_id:id,image_ref:z.string().optional(),display_order:z.number().int().nonnegative()}).strict();
const dialogueLineSchema=z.object({id,speaker:nonEmpty,french:nonEmpty,english:nonEmpty,audio_id:id}).strict();
const dialogueSchema=z.object({id,title:nonEmpty,lines:z.array(dialogueLineSchema).min(2)}).strict();
const pronunciationPromptSchema=z.object({id,vocabulary_id:id,prompt_french:nonEmpty,tip_english:nonEmpty,audio_id:id}).strict();
const pairSchema=z.object({left:nonEmpty,right:nonEmpty}).strict();
const exerciseSchema=z.object({id,type:z.enum(['audio_to_word','audio_to_picture','matching','multiple_choice','missing_word','sentence_ordering','listening_comprehension','dictation','pronunciation','dialogue_response','review']),instruction:nonEmpty,prompt:nonEmpty,options:z.array(nonEmpty).max(4).default([]),pairs:z.array(pairSchema).optional(),words:z.array(nonEmpty).optional(),correct_answer:z.union([nonEmpty,z.record(z.string(),z.string())]),audio_id:id.optional(),vocabulary_refs:z.array(id).default([]),dialogue_ref:id.optional(),explanation:nonEmpty,age_modes:z.array(z.enum(['ages_5_7','ages_8_9','ages_10_11','adults'])).min(1)}).strict();
const adaptationSchema=z.object({approach:nonEmpty,activity:nonEmpty,excluded_exercise_types:z.array(z.string()).default([])}).strict();

export const lessonContentSchema=z.object({schema_version:z.literal(1),content_version:z.number().int().positive(),status:z.enum(['draft','reviewed','published']),lesson_id:id,title:nonEmpty,topic:nonEmpty,level:z.literal('Pre-A1'),target_languages:z.object({instruction_language:z.literal('English'),learning_language:z.literal('French')}).strict(),learning_objectives:z.array(nonEmpty).min(1),provenance:z.object({created_by:nonEmpty,reviewed_by:z.string().optional(),source_notes:z.array(sourceNoteSchema).min(1)}).strict(),audio_assets:z.array(audioSchema).min(1),vocabulary:z.array(vocabularySchema).min(1),dialogues:z.array(dialogueSchema),exercises:z.array(exerciseSchema).min(1),pronunciation_prompts:z.array(pronunciationPromptSchema),age_adaptations:z.object({ages_5_7:adaptationSchema,ages_8_9:adaptationSchema,ages_10_11:adaptationSchema,adults:adaptationSchema}).strict()}).strict().superRefine((lesson,ctx)=>{
 const duplicate=(items:{id:string}[],kind:string,path:string)=>{const seen=new Set<string>();items.forEach((item,index)=>{if(seen.has(item.id))ctx.addIssue({code:'custom',message:`duplicate ${kind} ID: ${item.id}`,path:[path,index,'id']});seen.add(item.id)})};
 duplicate(lesson.vocabulary,'vocabulary','vocabulary');duplicate(lesson.dialogues,'dialogue','dialogues');duplicate(lesson.exercises,'exercise','exercises');duplicate(lesson.audio_assets,'audio','audio_assets');duplicate(lesson.pronunciation_prompts,'pronunciation prompt','pronunciation_prompts');
 const vocab=new Set(lesson.vocabulary.map(v=>v.id)),audio=new Set(lesson.audio_assets.map(a=>a.id)),dialogues=new Set(lesson.dialogues.map(d=>d.id));
 lesson.vocabulary.forEach((item,index)=>{if(!audio.has(item.audio_id))ctx.addIssue({code:'custom',message:`unknown audio ID: ${item.audio_id}`,path:['vocabulary',index,'audio_id']})});
 lesson.dialogues.forEach((dialogue,di)=>{
  dialogue.lines.forEach((line,li)=>{
   if(!audio.has(line.audio_id))ctx.addIssue({code:'custom',message:`unknown audio ID: ${line.audio_id}`,path:['dialogues',di,'lines',li,'audio_id']});
  });
 });
 lesson.pronunciation_prompts.forEach((prompt,index)=>{if(!vocab.has(prompt.vocabulary_id))ctx.addIssue({code:'custom',message:`unknown vocabulary ID: ${prompt.vocabulary_id}`,path:['pronunciation_prompts',index,'vocabulary_id']});if(!audio.has(prompt.audio_id))ctx.addIssue({code:'custom',message:`unknown audio ID: ${prompt.audio_id}`,path:['pronunciation_prompts',index,'audio_id']})});
 lesson.exercises.forEach((exercise,index)=>{exercise.vocabulary_refs.forEach((ref,ri)=>{if(!vocab.has(ref))ctx.addIssue({code:'custom',message:`unknown vocabulary ID: ${ref}`,path:['exercises',index,'vocabulary_refs',ri]})});if(exercise.audio_id&&!audio.has(exercise.audio_id))ctx.addIssue({code:'custom',message:`unknown audio ID: ${exercise.audio_id}`,path:['exercises',index,'audio_id']});if(exercise.dialogue_ref&&!dialogues.has(exercise.dialogue_ref))ctx.addIssue({code:'custom',message:`unknown dialogue ID: ${exercise.dialogue_ref}`,path:['exercises',index,'dialogue_ref']});if(typeof exercise.correct_answer==='string'&&exercise.options.length&&!exercise.options.includes(exercise.correct_answer)&&exercise.type!=='sentence_ordering')ctx.addIssue({code:'custom',message:'correct_answer must be one of options',path:['exercises',index,'correct_answer']});if(exercise.type==='matching'){const expected=Object.fromEntries((exercise.pairs||[]).map(pair=>[pair.left,pair.right]));if(JSON.stringify(exercise.correct_answer)!==JSON.stringify(expected))ctx.addIssue({code:'custom',message:'matching correct_answer must exactly match pairs',path:['exercises',index,'correct_answer']})}if(exercise.type==='sentence_ordering'&&(!exercise.words?.length||typeof exercise.correct_answer!=='string'))ctx.addIssue({code:'custom',message:'sentence_ordering requires words and a string correct_answer',path:['exercises',index]})});
});

export type LessonContent=z.infer<typeof lessonContentSchema>;
export type ContentStatus=LessonContent['status'];
export function publishedOnly(lessons:LessonContent[]){return lessons.filter(lesson=>lesson.status==='published')}
export function validateLessonSet(lessons:LessonContent[]){const seen=new Set<string>();for(const lesson of lessons){if(seen.has(lesson.lesson_id))throw new Error(`duplicate lesson ID: ${lesson.lesson_id}`);seen.add(lesson.lesson_id)}return lessons}
export function formatZodIssues(file:string,error:z.ZodError){return error.issues.map(issue=>`${file}:${issue.path.length?issue.path.join('.'):'$'}: ${issue.message}`)}
