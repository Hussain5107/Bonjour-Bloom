# Bonjour Bloom content authoring

Course content lives in `lib/starter-course.ts` and is validated by the Zod models in `lib/course-model.ts`. UI components consume the validated course; adding lessons must not require a new page component.

## Add vocabulary

Each item needs a stable ID, French and English text, article/gender when applicable, part of speech, learner-friendly pronunciation, normal and slow audio references, an icon/image reference, example and translation, CEFR level, topic, order and review tags. Only add IPA after linguistic verification. Every French phrase must have a browser-TTS fallback using `fr-FR`.

## Add a lesson

Create objectives, vocabulary IDs, reusable sections, at least six exercises, a dialogue with audio references, and age-scoped grammar/cultural notes. Exercise types are defined in `exerciseSchema`. Keep Early Learner activities picture/audio-first and exclude required typing. Run `npm run validate:content`, `npm test`, and `npm run typecheck`.

## Review checklist

- Natural, correct French reviewed by a qualified speaker.
- No copyrighted third-party datasets, images, or recordings without recorded provenance.
- Every visible French line has normal and slow audio behavior.
- Answers are unambiguous and explanations are kind.
- Child instructions are short; adult explanations are practical and mature.
