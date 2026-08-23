export type TeacherVoice='female'|'male';
export type AudioSpeed='normal'|'slow';
export type AudioRequest={text:string;locale:'fr-FR';speed:AudioSpeed;voice:TeacherVoice;cacheKey?:string};
export type AudioResult={provider:string;source:'recorded'|'neural'|'browser'|'mock';cached:boolean};
export interface AudioProvider{readonly id:string;canPlay(request:AudioRequest):Promise<boolean>;play(request:AudioRequest):Promise<AudioResult>;preload?(requests:AudioRequest[]):Promise<void>}

export class RecordedAudioProvider implements AudioProvider{
 readonly id='recorded';constructor(private readonly assets:Record<string,string>={}){}
 async canPlay(r:AudioRequest){return Boolean(r.cacheKey&&this.assets[r.cacheKey])}
 async play(r:AudioRequest){const url=r.cacheKey?this.assets[r.cacheKey]:undefined;if(!url)throw new Error('recording-missing');const audio=new Audio(url);audio.playbackRate=r.speed==='slow'?.78:1;await audio.play();return{provider:this.id,source:'recorded' as const,cached:true}}
}
export class NeuralTTSProvider implements AudioProvider{
 readonly id='neural';constructor(private readonly endpoint='/api/tts'){}
 async canPlay(){return typeof navigator!=='undefined'&&navigator.onLine}
 async play(r:AudioRequest){const response=await fetch(this.endpoint,{method:'POST',headers:{'content-type':'application/json'},body:JSON.stringify(r)});if(!response.ok)throw new Error('neural-provider-failed');const blob=await response.blob(),url=URL.createObjectURL(blob),audio=new Audio(url);try{await audio.play()}finally{audio.onended=()=>URL.revokeObjectURL(url)}return{provider:this.id,source:'neural' as const,cached:false}}
}
export class BrowserSpeechFallbackProvider implements AudioProvider{
 readonly id='browser';async canPlay(_request?:AudioRequest){return typeof window!=='undefined'&&'speechSynthesis'in window}
 async play(r:AudioRequest){if(!await this.canPlay(r))throw new Error('speech-synthesis-unsupported');window.speechSynthesis.cancel();const utterance=new SpeechSynthesisUtterance(r.text),voices=window.speechSynthesis.getVoices().filter(v=>v.lang.toLowerCase().startsWith('fr'));utterance.lang=r.locale;utterance.rate=r.speed==='slow'?.68:.92;utterance.voice=voices.find(v=>r.voice==='female'?/amelie|audrey|celine|denise|female/i.test(v.name):/thomas|henri|paul|male/i.test(v.name))||voices[0]||null;await new Promise<void>((resolve,reject)=>{utterance.onend=()=>resolve();utterance.onerror=()=>reject(new Error('speech-synthesis-failed'));window.speechSynthesis.speak(utterance)});return{provider:this.id,source:'browser' as const,cached:false}}
}
export class MockAudioProvider implements AudioProvider{readonly id='mock';requests:AudioRequest[]=[];async canPlay(){return true}async play(r:AudioRequest){this.requests.push(r);return{provider:this.id,source:'mock' as const,cached:false}}}
export class AudioService{private voice:TeacherVoice='female';constructor(private readonly providers:AudioProvider[]){}setVoice(v:TeacherVoice){this.voice=v}async play(text:string,speed:AudioSpeed='normal',cacheKey?:string){const request:AudioRequest={text,locale:'fr-FR',speed,voice:this.voice,cacheKey};for(const provider of this.providers){try{if(await provider.canPlay(request))return await provider.play(request)}catch{continue}}throw new Error('audio-unavailable')}}
export const audioService=new AudioService([new RecordedAudioProvider(),new NeuralTTSProvider(),new BrowserSpeechFallbackProvider()]);
