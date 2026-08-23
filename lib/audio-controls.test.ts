import {describe,expect,it} from 'vitest';
import {AudioService,MockAudioProvider,splitFrenchSentences} from './audio';

describe('French audio controls',()=>{
 it('normalizes and splits long text into sentence-sized chunks',()=>{
  expect(splitFrenchSentences(' Bonjour !  Comment ça va ? Très bien. ')).toEqual(['Bonjour !','Comment ça va ?','Très bien.']);
 });
 it('reports the active sentence and returns to idle after playback',async()=>{
  const provider=new MockAudioProvider(),service=new AudioService([provider]),states:string[]=[];
  service.subscribe(state=>states.push(`${state.status}:${state.activeSentence}`));
  await service.play('Bonjour ! Au revoir.');
  expect(provider.sentences).toEqual(['Bonjour !','Au revoir.']);
  expect(states).toContain('playing:Bonjour !');
  expect(states.at(-1)).toBe('idle:');
 });
 it('preserves voice and speed when replaying',async()=>{
  const provider=new MockAudioProvider(),service=new AudioService([provider]);
  service.setVoice('male');
  await service.play('Bonjour','slow');
  await service.replay();
  expect(provider.requests).toHaveLength(2);
  expect(provider.requests[1]).toMatchObject({text:'Bonjour',speed:'slow',voice:'male',locale:'fr-FR'});
 });
});
