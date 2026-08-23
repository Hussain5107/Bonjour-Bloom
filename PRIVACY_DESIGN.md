# Privacy design

Bonjour Bloom uses a parent-owned Supabase account with multiple private learner profiles. Row-level security binds every learner row to `auth.uid()`. The browser receives only the Supabase publishable key; service-role credentials are prohibited.

Progress is cached in IndexedDB for offline use and queued for account sync after reconnection. The legacy `bonjour-bloom` localStorage record is migrated without deleting it during this milestone. Microphone permission is requested only after an explanation and a learner action. Voice recordings remain device-local, are held only for the active practice session, and are never uploaded automatically.

No ads, public profiles, leaderboards, child chat, behavioral advertising, or social comparison are present. The parent gate is casual UI separation, not a second authentication factor. Production launch still requires jurisdiction-specific review for children’s privacy, consent, retention, access/export/deletion, and incident response.

Disabling email confirmation is a temporary delivery workaround. Configure trusted SMTP and restore email verification before wider production launch.
