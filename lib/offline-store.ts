import {normalizeAppData,type AppData} from './progress';
const DB='bonjour-bloom',VERSION=1,STATE='state',QUEUE='sync-queue';
export type SyncJob={id:string;createdAt:string;state:AppData};
function openDb(){return new Promise<IDBDatabase>((resolve,reject)=>{const request=indexedDB.open(DB,VERSION);request.onupgradeneeded=()=>{const db=request.result;if(!db.objectStoreNames.contains(STATE))db.createObjectStore(STATE);if(!db.objectStoreNames.contains(QUEUE))db.createObjectStore(QUEUE,{keyPath:'id'})};request.onsuccess=()=>resolve(request.result);request.onerror=()=>reject(request.error)})}
function requestValue<T>(request:IDBRequest<T>){return new Promise<T>((resolve,reject)=>{request.onsuccess=()=>resolve(request.result);request.onerror=()=>reject(request.error)})}
export async function loadAppState(){if(typeof indexedDB==='undefined')return null;const db=await openDb(),tx=db.transaction(STATE,'readonly'),value=await requestValue(tx.objectStore(STATE).get('app'));return value?normalizeAppData(value):null}
export async function saveAppState(state:AppData){if(typeof indexedDB==='undefined')return;const db=await openDb(),tx=db.transaction(STATE,'readwrite');await requestValue(tx.objectStore(STATE).put(state,'app'))}
export async function migrateLegacyState(){const current=await loadAppState();if(current)return current;const raw=localStorage.getItem('bonjour-bloom');if(!raw)return null;try{const state=normalizeAppData(JSON.parse(raw));await saveAppState(state);return state}catch{return null}}
export async function enqueueSync(state:AppData){if(typeof indexedDB==='undefined')return;const db=await openDb(),tx=db.transaction(QUEUE,'readwrite'),job:SyncJob={id:crypto.randomUUID(),createdAt:new Date().toISOString(),state};await requestValue(tx.objectStore(QUEUE).put(job))}
export async function listSyncJobs(){if(typeof indexedDB==='undefined')return[];const db=await openDb(),tx=db.transaction(QUEUE,'readonly');return await requestValue(tx.objectStore(QUEUE).getAll()) as SyncJob[]}
export async function removeSyncJob(id:string){const db=await openDb(),tx=db.transaction(QUEUE,'readwrite');await requestValue(tx.objectStore(QUEUE).delete(id))}
export function coalesceJobs(jobs:SyncJob[]){return jobs.sort((a,b)=>a.createdAt.localeCompare(b.createdAt)).at(-1)||null}
