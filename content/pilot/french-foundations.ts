import {curriculumSchema,type AudienceBand} from '../../lib/curriculum-foundation';

const rows=[
 ['greetings','Greetings','bonjour|hello;salut|hi;bonsoir|good evening;au revoir|goodbye;à bientôt|see you soon;merci|thank you;s’il vous plaît|please;de rien|you’re welcome'],
 ['introductions','Introductions','je m’appelle…|my name is…;comment tu t’appelles ?|what is your name?;moi, c’est…|I’m…;enchanté|nice to meet you (m.);enchantée|nice to meet you (f.);je suis…|I am…;et toi ?|and you?;bienvenue|welcome'],
 ['wellbeing','How are you?','ça va ?|how are you?;ça va bien|I’m well;très bien|very well;comme ci, comme ça|so-so;pas mal|not bad;ça ne va pas|I’m not well;je suis content|I am happy (m.);je suis contente|I am happy (f.)'],
 ['numbers','Numbers 1–8','un|one;deux|two;trois|three;quatre|four;cinq|five;six|six;sept|seven;huit|eight'],
 ['classroom','In the classroom','un livre|a book;un stylo|a pen;une table|a table;une chaise|a chair;une porte|a door;une fenêtre|a window;écoutez|listen;regardez|look'],
 ['questions','Simple questions','qui ?|who?;quoi ?|what?;où ?|where?;quand ?|when?;comment ?|how?;oui|yes;non|no;je ne sais pas|I don’t know']
] as const;
const moduleIds=['first-conversations','first-building-blocks'] as const;
const source='Original Bonjour Bloom pilot copy; accents and translations require qualified French editorial sign-off.';
function items(raw:string){return raw.split(';').map(pair=>{const [target,translation]=pair.split('|');return {target,translation}})}
function variant(audience:AudienceBand,title:string,entries:ReturnType<typeof items>){const tone=audience==='child'?'Try these friendly words with Milo.':audience==='teen'?'Use these phrases in a short real-life exchange.':'Build a practical first exchange.';return {audience,title:intlTitle(audience,title),intro:tone,sections:[{heading:'Core language',body:'Listen, notice, then respond.',items:entries}],exercises:[{id:`${title.toLowerCase().replace(/\W+/g,'-')}-${audience}`,type:'select' as const,prompt:`Choose the meaning of “${entries[0].target}”.`,options:[entries[0].translation,entries[1].translation,'not yet'],answer:entries[0].translation,points:1}]}}
function intlTitle(audience:AudienceBand,title:string){return audience==='child'?`${title} with Milo`:audience==='teen'?`${title} in action`:title}
const objectives=rows.map(([id,title],index)=>({id:`obj-${id}`,language:'fr',cefr:'pre_a1' as const,skills:index<3?['listening','speaking','interaction'] as const:['reading','vocabulary'] as const,statement:`Use essential ${title.toLowerCase()} language in a supported exchange.`,prerequisiteIds:index===0?[]:[`obj-${rows[index-1][0]}`]}));
const lessons=rows.map(([id,title,raw],index)=>({id:`fr-prea1-${id}`,moduleId:moduleIds[index<3?0:1],language:'fr',cefr:'pre_a1' as const,status:(id==='wellbeing'?'in_review':'published') as 'in_review'|'published',sequence:index+1,objectiveIds:[`obj-${id}`],legacyLessonId:id==='greetings'?'bonjour':id==='introductions'?'introductions':id==='numbers'?'numbers':undefined,provenance:{createdBy:'Bonjour Bloom curriculum team',sourceNotes:[source],license:'Project-authored content'},variants:(['child','teen','adult'] as const).map(a=>variant(a,title,items(raw)))}));

export const frenchFoundationPilot=curriculumSchema.parse({schemaVersion:2,track:{id:'fr-foundations',language:'fr',title:'French foundations',active:true},objectives,modules:[{id:moduleIds[0],trackId:'fr-foundations',title:'First conversations',sequence:1,lessonIds:lessons.slice(0,3).map(l=>l.id)},{id:moduleIds[1],trackId:'fr-foundations',title:'First building blocks',sequence:2,lessonIds:lessons.slice(3).map(l=>l.id)}],lessons});
