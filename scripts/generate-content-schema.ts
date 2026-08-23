import {writeFile} from 'node:fs/promises';
import {z} from 'zod';
import {lessonContentSchema} from '../lib/content-pipeline.ts';

const schema=z.toJSONSchema(lessonContentSchema,{target:'draft-2020-12'});await writeFile('content/lesson.schema.json',JSON.stringify({...schema,$id:'https://bonjour-bloom.app/schemas/lesson.schema.json',title:'Bonjour Bloom lesson content'},null,2)+'\n');console.log('Generated content/lesson.schema.json');
