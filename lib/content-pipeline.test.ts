import {readFileSync} from 'node:fs';
import {describe,expect,it} from 'vitest';
import {formatZodIssues,lessonContentSchema,publishedOnly,validateLessonSet} from './content-pipeline';

const example=()=>JSON.parse(readFileSync(new URL('../content/examples/prea1-greetings-001.json',import.meta.url),'utf8'));

describe('lesson content pipeline',()=>{
 it('accepts the canonical example',()=>expect(lessonContentSchema.safeParse(example()).success).toBe(true));
 it('rejects unknown fields with the exact JSON field',()=>{const value=example();value.unexpected=true;const result=lessonContentSchema.safeParse(value);expect(result.success).toBe(false);if(!result.success)expect(formatZodIssues('lesson.json',result.error)).toContain('lesson.json:$: Unrecognized key: "unexpected"')});
 it('reports duplicate vocabulary, dialogue, and exercise IDs',()=>{const value=example();value.vocabulary.push({...value.vocabulary[0]});value.dialogues.push({...value.dialogues[0]});value.exercises.push({...value.exercises[0]});const result=lessonContentSchema.safeParse(value);expect(result.success).toBe(false);if(!result.success){const text=formatZodIssues('lesson.json',result.error).join('\n');expect(text).toContain('duplicate vocabulary ID');expect(text).toContain('duplicate dialogue ID');expect(text).toContain('duplicate exercise ID')}});
 it('rejects invalid answers and broken references',()=>{const value=example();value.exercises[0].correct_answer='wrong';value.exercises[0].audio_id='missing-audio';value.exercises[0].vocabulary_refs=['missing-word'];value.exercises[0].dialogue_ref='missing-dialogue';const result=lessonContentSchema.safeParse(value);expect(result.success).toBe(false);if(!result.success){const text=formatZodIssues('lesson.json',result.error).join('\n');expect(text).toContain('correct_answer must be one of options');expect(text).toContain('unknown audio ID');expect(text).toContain('unknown vocabulary ID');expect(text).toContain('unknown dialogue ID')}});
 it('detects duplicate lesson IDs across files',()=>expect(()=>validateLessonSet([example(),example()].map(value=>lessonContentSchema.parse(value)))).toThrow('duplicate lesson ID'));
 it('exposes only published lessons to learners',()=>{const draft=lessonContentSchema.parse(example());const published={...draft,status:'published' as const};expect(publishedOnly([draft,published])).toEqual([published])});
});
