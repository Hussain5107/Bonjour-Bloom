import {getSupabase} from './supabase';
import {lessonContentSchema,publishedOnly,type LessonContent} from './content-pipeline';

export async function listPublishedContent(){const supabase=getSupabase();if(!supabase)return[];const{data,error}=await supabase.from('lesson_content').select('payload').eq('status','published').order('lesson_id');if(error)throw error;const lessons=(data||[]).map(row=>lessonContentSchema.parse(row.payload));return publishedOnly(lessons)}
export async function isContentAdmin(){const supabase=getSupabase();if(!supabase)return false;const{data:{user}}=await supabase.auth.getUser();if(!user)return false;const{data}=await supabase.from('content_admins').select('user_id').eq('user_id',user.id).maybeSingle();return Boolean(data)}
export async function listAdminContent(){if(!await isContentAdmin())throw new Error('administrator access required');const supabase=getSupabase()!;const{data,error}=await supabase.from('lesson_content').select('payload').order('updated_at',{ascending:false});if(error)throw error;return(data||[]).map(row=>lessonContentSchema.parse(row.payload)) as LessonContent[]}
