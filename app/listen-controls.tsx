'use client';
import {useEffect,useState} from 'react';
import {Pause,RotateCcw,Square,Volume2} from 'lucide-react';
import {audioService,type AudioPlaybackState,type AudioSpeed} from '../lib/audio';

const initial:AudioPlaybackState={status:'idle',text:'',activeSentence:'',canPause:false};
export function ListenControls({text,compact=false}:{text:string;compact?:boolean}){
 const[state,setState]=useState(initial),[speed,setSpeed]=useState<AudioSpeed>('normal');
 useEffect(()=>audioService.subscribe(setState),[]);
 const owns=state.text===text,busy=owns&&(state.status==='loading'||state.status==='playing'||state.status==='paused');
 const play=()=>void audioService.play(text,speed).catch(()=>undefined);
 return <div className={compact?'listen-controls compact':'listen-controls'} aria-label="French audio controls">
  <button type="button" className="listen-primary" onClick={state.status==='paused'&&owns?()=>audioService.resume():state.status==='playing'&&owns?()=>audioService.pause():play} aria-label={state.status==='paused'&&owns?'Resume French audio':state.status==='playing'&&owns?'Pause French audio':'Play French audio'}>{state.status==='playing'&&owns?<Pause/>:<Volume2/>}<span>{state.status==='paused'&&owns?'Resume':state.status==='playing'&&owns?'Pause':'Listen'}</span></button>
  {!compact&&<button type="button" className="speed-button" aria-pressed={speed==='slow'} onClick={()=>setSpeed(value=>value==='normal'?'slow':'normal')}>{speed==='slow'?'Slow':'Normal'}</button>}
  {busy&&<button type="button" onClick={()=>audioService.stop()} aria-label="Stop French audio"><Square/></button>}
  {!compact&&state.status==='idle'&&owns&&<button type="button" onClick={()=>void audioService.replay().catch(()=>undefined)} aria-label="Replay French audio"><RotateCcw/></button>}
  {owns&&state.activeSentence&&<span className="active-sentence" aria-live="polite">{state.activeSentence}</span>}
  {owns&&state.status==='error'&&<span className="audio-message" role="status">{state.message}</span>}
 </div>
}
