export type TeacherVoice='female'|'male';
export type AudioSpeed='normal'|'slow';
export type AudioRequest={text:string;locale:'fr-FR';speed:AudioSpeed;voice:TeacherVoice;cacheKey?:string};
export type AudioResult={provider:string;source:'recorded'|'neural'|'browser'|'mock';cached:boolean};
export type AudioPlaybackState={status:'idle'|'loading'|'playing'|'paused'|'error';text:string;activeSentence:string;canPause:boolean;message?:string};
export interface AudioProvider{readonly id:string;canPlay(request:AudioRequest):Promise<boolean>;play(request:AudioRequest,onSentence?:(sentence:string)=>void):Promise<AudioResult>;pause?():void;resume?():void;stop?():void;preload?(requests:AudioRequest[]):Promise<void>}

export function splitFrenchSentences(text:string){
 const normalized=text.replace(/\s+/g,' ').trim();
 if(!normalized)return[];
 return normalized.match(/[^.!?…]+(?:[.!?…]+|$)/g)?.map(sentence=>sentence.trim()).filter(Boolean)||[normalized];
}

export class RecordedAudioProvider implements AudioProvider{
 readonly id='recorded';private current?:HTMLAudioElement;private finish?:()=>void;constructor(private readonly assets:Record<string,string>={}){}
 async canPlay(r:AudioRequest){return Boolean(r.cacheKey&&this.assets[r.cacheKey])}
 async play(r:AudioRequest){const url=r.cacheKey?this.assets[r.cacheKey]:undefined;if(!url)throw new Error('recording-missing');this.stop();const audio=new Audio(url);this.current=audio;audio.playbackRate=r.speed==='slow'?.78:1;await audio.play();await new Promise<void>((resolve,reject)=>{this.finish=resolve;audio.onended=()=>resolve();audio.onerror=()=>reject(new Error('recording-playback-failed'))});return{provider:this.id,source:'recorded' as const,cached:true}}
 pause(){this.current?.pause()} resume(){void this.current?.play()} stop(){if(this.current){this.current.pause();this.current.currentTime=0;this.current=undefined;this.finish?.();this.finish=undefined}}
}
export class NeuralTTSProvider implements AudioProvider{
 readonly id='neural';private current?:HTMLAudioElement;private objectUrl?:string;private finish?:()=>void;constructor(private readonly endpoint='/api/tts'){}
 async canPlay(){return typeof navigator!=='undefined'&&navigator.onLine}
 async play(r:AudioRequest){this.stop();const response=await fetch(this.endpoint,{method:'POST',headers:{'content-type':'application/json'},body:JSON.stringify(r)});if(!response.ok)throw new Error('neural-provider-failed');const blob=await response.blob();this.objectUrl=URL.createObjectURL(blob);this.current=new Audio(this.objectUrl);await this.current.play();await new Promise<void>((resolve,reject)=>{this.finish=resolve;this.current!.onended=()=>resolve();this.current!.onerror=()=>reject(new Error('neural-playback-failed'))});return{provider:this.id,source:'neural' as const,cached:false}}
 pause(){this.current?.pause()} resume(){void this.current?.play()} stop(){if(this.current){this.current.pause();this.current=undefined;this.finish?.();this.finish=undefined}if(this.objectUrl){URL.revokeObjectURL(this.objectUrl);this.objectUrl=undefined}}
}
export class BrowserSpeechFallbackProvider implements AudioProvider{
 readonly id='browser';private stopped=false;
 async canPlay(){return typeof window!=='undefined'&&'speechSynthesis'in window}
 private async voices(){const synth=window.speechSynthesis,current=synth.getVoices();if(current.length)return current;return await new Promise<SpeechSynthesisVoice[]>(resolve=>{const done=()=>{clearTimeout(timer);synth.removeEventListener('voiceschanged',done);resolve(synth.getVoices())},timer=window.setTimeout(done,1200);synth.addEventListener('voiceschanged',done,{once:true})})}
 async play(r:AudioRequest,onSentence?:(sentence:string)=>void){if(!await this.canPlay())throw new Error('speech-synthesis-unsupported');this.stop();this.stopped=false;const voices=(await this.voices()).filter(v=>v.lang.toLowerCase().startsWith('fr'));if(!voices.length)throw new Error('french-voice-unavailable');const preferred=voices.filter(v=>v.lang.toLowerCase()==='fr-fr'),pool=preferred.length?preferred:voices,voice=pool.find(v=>r.voice==='female'?/amelie|audrey|celine|denise|female/i.test(v.name):/thomas|henri|paul|male/i.test(v.name))||pool[0];for(const sentence of splitFrenchSentences(r.text)){if(this.stopped)throw new Error('speech-synthesis-cancelled');onSentence?.(sentence);await new Promise<void>((resolve,reject)=>{const utterance=new SpeechSynthesisUtterance(sentence);utterance.lang=r.locale;utterance.rate=r.speed==='slow'?.68:.92;utterance.voice=voice;utterance.onend=()=>resolve();utterance.onerror=event=>event.error==='canceled'?resolve():reject(new Error('speech-synthesis-failed'));window.speechSynthesis.speak(utterance)})}return{provider:this.id,source:'browser' as const,cached:false}}
 pause(){window.speechSynthesis.pause()} resume(){window.speechSynthesis.resume()} stop(){this.stopped=true;window.speechSynthesis.cancel()}
}
export class MockAudioProvider implements AudioProvider{readonly id='mock';requests:AudioRequest[]=[];sentences:string[]=[];async canPlay(){return true}async play(r:AudioRequest,onSentence?:(sentence:string)=>void){this.requests.push(r);for(const sentence of splitFrenchSentences(r.text)){this.sentences.push(sentence);onSentence?.(sentence)}return{provider:this.id,source:'mock' as const,cached:false}}}
export class AudioService{
 private voice:TeacherVoice='female';private active?:AudioProvider;private last?:AudioRequest;private listeners=new Set<(state:AudioPlaybackState)=>void>();private state:AudioPlaybackState={status:'idle',text:'',activeSentence:'',canPause:false};
 constructor(private readonly providers:AudioProvider[]){}
 setVoice(v:TeacherVoice){this.voice=v}
 subscribe(listener:(state:AudioPlaybackState)=>void){this.listeners.add(listener);listener(this.state);return()=>{this.listeners.delete(listener)}}
 private emit(next:Partial<AudioPlaybackState>){this.state={...this.state,...next};this.listeners.forEach(listener=>listener(this.state))}
 async play(text:string,speed:AudioSpeed='normal',cacheKey?:string){this.stop();const request:AudioRequest={text,locale:'fr-FR',speed,voice:this.voice,cacheKey};this.last=request;this.emit({status:'loading',text,activeSentence:'',message:undefined});for(const provider of this.providers){try{if(await provider.canPlay(request)){this.active=provider;this.emit({status:'playing',canPause:Boolean(provider.pause)});const result=await provider.play(request,sentence=>this.emit({activeSentence:sentence}));this.emit({status:'idle',activeSentence:'',canPause:false});return result}}catch(error){if(error instanceof Error&&error.message==='speech-synthesis-cancelled')return{provider:provider.id,source:'browser' as const,cached:false};continue}}this.emit({status:'error',canPause:false,message:'A French voice is not available on this device. You can still read and practise aloud.'});throw new Error('audio-unavailable')}
 pause(){this.active?.pause?.();this.emit({status:'paused'})}
 resume(){this.active?.resume?.();this.emit({status:'playing'})}
 stop(){this.active?.stop?.();this.active=undefined;if(this.state.status!=='idle')this.emit({status:'idle',activeSentence:'',canPause:false})}
 replay(){if(!this.last)return Promise.reject(new Error('nothing-to-replay'));return this.play(this.last.text,this.last.speed,this.last.cacheKey)}
}
export const audioService=new AudioService([new RecordedAudioProvider(),new NeuralTTSProvider(),new BrowserSpeechFallbackProvider()]);
