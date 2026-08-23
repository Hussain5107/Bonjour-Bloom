# Bonjour Bloom content authoring

The reusable authoring format is defined by `lib/content-pipeline.ts`; the generated machine-readable contract is `content/lesson.schema.json`. Both reject unknown fields. `content/examples/prea1-greetings-001.json` is the single canonical example and remains a draft, so it is not shown to learners.

## Strict lesson contract

Every lesson requires `schema_version` (currently `1`), an increasing positive `content_version`, `status` (`draft`, `reviewed`, or `published`), a stable kebab-case `lesson_id`, title, topic, Pre-A1 level, English/French target languages, learning objectives, provenance, audio assets, vocabulary, dialogues, exercises, pronunciation prompts, and all four age adaptations.

- Provenance records `created_by`, optional `reviewed_by`, and at least one source note. Each source note requires a URL, title, ISO date in `accessed_at`, and optional license.
- Audio assets have a stable ID, French text, optional slow text, and the `fr-FR` locale.
- Vocabulary has a stable ID, French and English forms, article, gender, part of speech, pronunciation guide, examples, audio reference, optional IPA/image reference, and display order.
- Dialogues have stable dialogue and line IDs, bilingual lines, speakers, and audio references.
- Exercises have a stable ID, supported type, instruction, prompt, no more than four options, a non-empty answer (string or matching map), optional pairs/words/audio/dialogue references, vocabulary references, explanation, and at least one age mode. Choice answers must occur in their options; matching answers must exactly equal their pairs; sentence ordering requires words and a string answer.
- Pronunciation prompts have a stable ID plus valid vocabulary and audio references.
- Each age adaptation requires an approach and activity and may exclude exercise types.

IDs must be lowercase kebab-case. Lesson, vocabulary, dialogue, exercise, audio, and pronunciation IDs are duplicate-checked. All vocabulary, dialogue, exercise, pronunciation, and audio references are checked. Malformed JSON, missing values, broken references, unknown fields, and invalid answers fail validation with the file path and JSON field.

## Perplexity research workflow

1. Research only the requested lesson scope. Prefer primary sources and reputable French-language references; do not copy protected lesson text.
2. Record every source immediately in `provenance.source_notes`, including URL, descriptive title, access date, and license when known.
3. Draft against `content/lesson.schema.json`, using the canonical example for structure. Keep status `draft` and increment `content_version` whenever approved content changes.
4. Run `npm run validate:content -- path/to/file.json`. Correct every reported `file:json.path: message` error.
5. Have a qualified reviewer check French accuracy, age suitability, answer ambiguity, licensing, and audio text. Record the reviewer and change status to `reviewed`.
6. Import reviewed content into the private review store, inspect `/admin/content-preview`, and change status to `published` only after final approval. Only published rows are returned to learners.

Never paste service-role credentials into content or client code. Perplexity output is research input, not an authority and not automatically publishable.

## Commands and publishing

- `npm run validate:content -- content/examples` validates a directory recursively; pass a JSON path to validate one file.
- `npm run content:schema` regenerates the JSON Schema after a TypeScript schema change.
- `npm run content:import -- path` validates and upserts only `reviewed` or `published` lessons. It requires `NEXT_PUBLIC_SUPABASE_URL` and the server-only `SUPABASE_SERVICE_ROLE_KEY`.

The import writes only `lesson_content`, keyed by `lesson_id`; it never updates learner profile or progress tables. Content version and provenance are stored with each row. Database row-level security exposes all statuses only to users listed in `content_admins`, while ordinary authenticated users can select published content only. Assign the first administrator directly in Supabase using their authenticated user UUID.

The current built-in lessons remain in `lib/starter-course.ts` and are validated by `lib/course-model.ts` during the automated curriculum tests. New pipeline content should not be copied into those files.

Before importing or deploying, run `npm run validate:content -- content/examples`, `npm run lint`, `npm run typecheck`, `npm test`, and `npm run build`.
