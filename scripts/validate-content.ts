import {loadContent} from './content-files.ts';

const input=process.argv[2]||'content/examples',result=await loadContent(input);if(result.errors.length){console.error(result.errors.join('\n'));process.exitCode=1}else console.log(`Validated ${result.lessons.length} lesson file(s) from ${input}.`);
