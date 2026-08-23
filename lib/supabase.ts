import {createClient,SupabaseClient} from '@supabase/supabase-js';

const rawUrl = process.env.NEXT_PUBLIC_SUPABASE_URL?.trim();
const rawKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY?.trim();

function isValidHttpUrl(str?: string): boolean {
  if (!str) return false;
  if (str.startsWith('your-') || str.includes('placeholder') || str.includes('example.com')) return false;
  try {
    const parsed = new URL(str);
    return parsed.protocol === 'http:' || parsed.protocol === 'https:';
  } catch {
    return false;
  }
}

function isValidKey(key?: string): boolean {
  if (!key) return false;
  return key.length > 10 && !key.startsWith('your-') && !key.includes('placeholder');
}

export const supabaseConfigured = Boolean(isValidHttpUrl(rawUrl) && isValidKey(rawKey));

let client: SupabaseClient | null = null;

export function getSupabase(): SupabaseClient | null {
  if (!supabaseConfigured || !rawUrl || !rawKey) return null;
  if (!client) {
    try {
      client = createClient(rawUrl, rawKey, {
        auth: {
          persistSession: true,
          autoRefreshToken: true,
          detectSessionInUrl: true,
        },
      });
    } catch (err) {
      console.warn('Failed to initialize Supabase client:', err);
      return null;
    }
  }
  return client;
}

