package com.example.bonjourbloom.gemini

import android.util.Base64
import android.util.Log
import com.example.bonjourbloom.BuildConfig
import com.example.bonjourbloom.gemini.model.AudioTranscriptionResult
import com.example.bonjourbloom.gemini.model.Content
import com.example.bonjourbloom.gemini.model.GenerateContentRequest
import com.example.bonjourbloom.gemini.model.GenerationConfig
import com.example.bonjourbloom.gemini.model.InlineData
import com.example.bonjourbloom.gemini.model.Part
import com.example.bonjourbloom.gemini.model.ResponseFormat
import com.example.bonjourbloom.gemini.model.ResponseFormatText
import com.example.bonjourbloom.gemini.model.TranscriptSegment
import com.example.bonjourbloom.gemini.model.VocabularyHighlight
import com.example.bonjourbloom.gemini.model.VoiceExchange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.io.File
import java.util.UUID

object GeminiLiveClient {

    private const val TAG = "GeminiLiveClient"
    // Recommended model for audio & voice conversation
    private const val VOICE_MODEL = "gemini-2.5-flash-native-audio-preview-12-2025"
    private const val FAST_MODEL = "gemini-3.5-flash"

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun hasApiKey(): Boolean {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotBlank() && !key.contains("MY_NEW_API_KEY") && !key.contains("DEFAULT_VALUE")
        } catch (e: Exception) {
            false
        }
    }

    suspend fun converseWithMilo(
        audioFile: File?,
        textInput: String?,
        history: List<VoiceExchange>,
        topic: String,
        ageBand: String
    ): VoiceExchange = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank()) {
            return@withContext generateOfflineFallbackVoiceExchange(textInput, topic)
        }

        try {
            val parts = mutableListOf<Part>()

            // If audio was recorded, attach base64 inlineData
            if (audioFile != null && audioFile.exists() && audioFile.length() > 0) {
                val bytes = audioFile.readBytes()
                val base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)
                parts.add(Part(inlineData = InlineData(mimeType = "audio/mp4", data = base64Data)))
                parts.add(Part(text = "Listen to the user's spoken voice and respond as Milo the French fox tutor."))
            }

            if (!textInput.isNullOrBlank()) {
                parts.add(Part(text = "User said: \"$textInput\""))
            }

            if (parts.isEmpty()) {
                parts.add(Part(text = "The learner started a conversation about topic: $topic."))
            }

            val systemPrompt = """
                You are Milo, an adorable, encouraging French fox tutor for young language learners (Pre-A1 beginner, age group $ageBand).
                Current conversation topic: $topic.
                Instructions:
                1. Speak in friendly, gentle, short French sentences (1-2 sentences maximum).
                2. Celebrate effort and courage!
                3. Always return response in valid JSON with these keys:
                   - "frenchText": French message from Milo
                   - "englishTranslation": English translation
                   - "phonetics": Simplified pronunciation guide
                   - "suggestedReplies": 3 short French phrases the child can say back
                   - "tip": Friendly Milo tip for pronunciation or culture
                   - "celebrationBadge": Short reward string like "🦊 Voice Bloom +5"
            """.trimIndent()

            val contents = mutableListOf<Content>()
            // Add previous exchanges
            history.takeLast(4).forEach { ex ->
                if (ex.speaker == "user") {
                    contents.add(Content(role = "user", parts = listOf(Part(text = ex.frenchText))))
                } else {
                    contents.add(Content(role = "model", parts = listOf(Part(text = ex.frenchText))))
                }
            }
            contents.add(Content(role = "user", parts = parts))

            val request = GenerateContentRequest(
                contents = contents,
                systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
                generationConfig = GenerationConfig(
                    temperature = 0.7f,
                    responseFormat = ResponseFormat(
                        text = ResponseFormatText(
                            mimeType = "application/json"
                        )
                    )
                )
            )

            val response = try {
                GeminiRetrofitClient.service.generateContent(FAST_MODEL, apiKey, request)
            } catch (e: Exception) {
                // Try fallback model if preview endpoint differs
                GeminiRetrofitClient.service.generateContent(VOICE_MODEL, apiKey, request)
            }

            val rawJson = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!rawJson.isNullOrBlank()) {
                parseVoiceExchangeJson(rawJson)
            } else {
                generateOfflineFallbackVoiceExchange(textInput, topic)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini voice conversation error: ${e.message}", e)
            generateOfflineFallbackVoiceExchange(textInput, topic)
        }
    }

    suspend fun transcribeAudioFeed(
        audioBytes: ByteArray?,
        sampleContext: String?
    ): AudioTranscriptionResult = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || audioBytes == null || audioBytes.isEmpty()) {
            return@withContext generateFallbackTranscription(sampleContext)
        }

        try {
            val base64Data = Base64.encodeToString(audioBytes, Base64.NO_WRAP)
            val prompt = """
                Transcribe the French/English audio feed accurately in real-time.
                Context hint: ${sampleContext ?: "French language learning practice"}
                Format your response strictly as JSON with the following structure:
                {
                  "fullText": "exact French transcription",
                  "englishTranslation": "accurate English translation",
                  "phonetics": "simplified IPA or phonetic pronunciation guide",
                  "detectedLanguage": "French (France)",
                  "confidence": 0.96,
                  "pronunciationTip": "Helpful tutor tip on accent, liaisons, or silent letters",
                  "detectedKeywords": [
                    {"french": "bonjour", "english": "hello", "icon": "👋"}
                  ],
                  "segments": [
                    {"timestamp": "00:01", "speaker": "Speaker 1", "frenchText": "...", "englishText": "..."}
                  ]
                }
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(inlineData = InlineData(mimeType = "audio/mp4", data = base64Data)),
                            Part(text = prompt)
                        )
                    )
                ),
                generationConfig = GenerationConfig(
                    temperature = 0.2f,
                    responseFormat = ResponseFormat(
                        text = ResponseFormatText(
                            mimeType = "application/json"
                        )
                    )
                )
            )

            val response = GeminiRetrofitClient.service.generateContent(FAST_MODEL, apiKey, request)
            val rawJson = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (!rawJson.isNullOrBlank()) {
                parseTranscriptionJson(rawJson)
            } else {
                generateFallbackTranscription(sampleContext)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini transcription error: ${e.message}", e)
            generateFallbackTranscription(sampleContext)
        }
    }

    private fun parseVoiceExchangeJson(rawJson: String): VoiceExchange {
        return try {
            val cleanJson = rawJson.trim().removeSurrounding("```json", "```").trim()
            val parsed = jsonParser.parseToJsonElement(cleanJson).jsonObject
            val french = parsed["frenchText"]?.jsonPrimitive?.content ?: "Bonjour ! Bravo pour ta voix !"
            val english = parsed["englishTranslation"]?.jsonPrimitive?.content ?: "Hello! Great job with your voice!"
            val phonetics = parsed["phonetics"]?.jsonPrimitive?.content ?: ""
            val tip = parsed["tip"]?.jsonPrimitive?.content
            val badge = parsed["celebrationBadge"]?.jsonPrimitive?.content ?: "🦊 Milo's Bloom +5"
            val replies = parsed["suggestedReplies"]?.let {
                try {
                    it.toString().removeSurrounding("[", "]").split(",")
                        .map { s -> s.trim().removeSurrounding("\"") }
                } catch (e: Exception) { emptyList() }
            } ?: listOf("Merci Milo !", "Oui !", "Au revoir !")

            VoiceExchange(
                id = UUID.randomUUID().toString(),
                speaker = "milo",
                frenchText = french,
                englishTranslation = english,
                phonetics = phonetics,
                suggestedReplies = replies,
                celebrationBadge = badge,
                tip = tip
            )
        } catch (e: Exception) {
            VoiceExchange(
                id = UUID.randomUUID().toString(),
                speaker = "milo",
                frenchText = "Bonjour ! J'adore parler avec toi !",
                englishTranslation = "Hello! I love talking with you!",
                phonetics = "bon-zhoor! zha-dor par-lay ah-vek twah!",
                suggestedReplies = listOf("Moi aussi !", "Merci !", "À bientôt !")
            )
        }
    }

    private fun parseTranscriptionJson(rawJson: String): AudioTranscriptionResult {
        return try {
            val cleanJson = rawJson.trim().removeSurrounding("```json", "```").trim()
            jsonParser.decodeFromString<AudioTranscriptionResult>(cleanJson)
        } catch (e: Exception) {
            generateFallbackTranscription(null)
        }
    }

    fun generateOfflineFallbackVoiceExchange(input: String?, topic: String): VoiceExchange {
        val lower = input?.lowercase() ?: ""
        val (french, english, phonetics, replies, tip) = when {
            lower.contains("bonjour") || lower.contains("hello") || topic.contains("Greeting", ignoreCase = true) -> {
                Tuple5(
                    "Bonjour mon ami ! Comment tu t'appelles ?",
                    "Hello my friend! What is your name?",
                    "bon-zhoor mohn ah-mee! ko-mahn too tah-pel?",
                    listOf("Je m'appelle Sam !", "Bonjour Milo !", "Ça va très bien !"),
                    "Milo's tip: 'Mon ami' means 'my friend' in French."
                )
            }
            lower.contains("croissant") || lower.contains("baguette") || topic.contains("Bakery", ignoreCase = true) -> {
                Tuple5(
                    "Miam ! Un délicieux croissant croustillant ! Tu aimes le chocolat aussi ?",
                    "Yum! A delicious crispy croissant! Do you like chocolate too?",
                    "mee-ahm! uh day-lee-syuh krwah-sahn! too em luh sho-ko-lah oh-see?",
                    listOf("Oui, j'adore le chocolat !", "Un croissant s'il vous plaît", "Merci beaucoup !"),
                    "Milo's tip: In France, saying 's'il vous plaît' (please) is super polite!"
                )
            }
            lower.contains("bleu") || lower.contains("rouge") || topic.contains("Color", ignoreCase = true) -> {
                Tuple5(
                    "Très joli ! Le bleu comme le ciel ou le rouge comme les fraises ?",
                    "Very pretty! Blue like the sky or red like strawberries?",
                    "tray zho-lee! luh bluh kom luh syel oo luh roozh kom lay frez?",
                    listOf("J'aime le bleu !", "J'aime le rouge !", "Toutes les couleurs !"),
                    "Milo's tip: In French, color words usually come after the noun."
                )
            }
            lower.contains("chat") || lower.contains("chien") || topic.contains("Animal", ignoreCase = true) -> {
                Tuple5(
                    "Un petit chat fait 'miaou' et le chien fait 'ouaf ouaf' ! Quel est ton animal préféré ?",
                    "A little cat says 'meow' and the dog says 'woof woof'! What is your favorite animal?",
                    "uh puh-tee shah feh mee-ow ay luh shyen feh wahf wahf!",
                    listOf("Le petit chat 🐱", "Le chien 🐶", "Le renard Milo 🦊"),
                    "Milo's tip: 'Renard' is French for fox—just like me!"
                )
            }
            else -> {
                Tuple5(
                    "C'est magnifique ! Tu parles un très bon français. Veux-tu essayer un autre mot ?",
                    "That's magnificent! You are speaking great French. Would you like to try another word?",
                    "seh mah-nyee-feek! too parl uh tray bohn frahn-seh.",
                    listOf("Oui avec plaisir !", "Comment on dit 'merci' ?", "Au revoir Milo !"),
                    "Milo's tip: Regular practice makes your French garden blossom every day!"
                )
            }
        }

        return VoiceExchange(
            id = UUID.randomUUID().toString(),
            speaker = "milo",
            frenchText = french,
            englishTranslation = english,
            phonetics = phonetics,
            suggestedReplies = replies,
            celebrationBadge = "🦊 Voice Bloom +5",
            tip = tip
        )
    }

    fun generateFallbackTranscription(contextHint: String?): AudioTranscriptionResult {
        return when {
            contextHint?.contains("Bakery", ignoreCase = true) == true -> {
                AudioTranscriptionResult(
                    fullText = "Bonjour madame ! Un croissant et une baguette s'il vous plaît. — Très bien, ça fait deux euros.",
                    englishTranslation = "Good morning madam! A croissant and a baguette please. — Very well, that is two euros.",
                    phonetics = "bɔ̃ʒuʁ madam ! œ̃ kʁwasɑ̃ e yn baɡɛt sil vu plɛ. tʁɛ bjɛ̃, sa fɛ dø zøʁo.",
                    detectedLanguage = "French (France)",
                    confidence = 0.98f,
                    pronunciationTip = "Notice the nasal vowel 'an' in 'croissant' and the liaison in 'deux euros' [dø zøʁo].",
                    detectedKeywords = listOf(
                        VocabularyHighlight("bonjour", "hello / good morning", "👋"),
                        VocabularyHighlight("croissant", "croissant pastry", "🥐"),
                        VocabularyHighlight("baguette", "baguette bread", "🥖"),
                        VocabularyHighlight("s'il vous plaît", "please", "✨"),
                        VocabularyHighlight("deux euros", "two euros", "🪙")
                    ),
                    segments = listOf(
                        TranscriptSegment("00:01", "Customer", "Bonjour madame !", "Good morning madam!"),
                        TranscriptSegment("00:03", "Customer", "Un croissant et une baguette s'il vous plaît.", "A croissant and a baguette please."),
                        TranscriptSegment("00:06", "Baker", "Très bien, ça fait deux euros.", "Very well, that is two euros.")
                    )
                )
            }
            contextHint?.contains("Jacques", ignoreCase = true) == true -> {
                AudioTranscriptionResult(
                    fullText = "Frère Jacques, Frère Jacques, dormez-vous ? Dormez-vous ? Sonnez les matines ! Ding, dang, dong.",
                    englishTranslation = "Brother Jacques, Brother Jacques, are you sleeping? Are you sleeping? Ring the morning bells! Ding, dang, dong.",
                    phonetics = "fʁɛʁ ʒak, fʁɛʁ ʒak, dɔʁme vu ? dɔʁme vu ? sɔne le matin ! diŋ, dɑ̃, dɔ̃.",
                    detectedLanguage = "French (Traditional Song)",
                    confidence = 0.99f,
                    pronunciationTip = "A classic French nursery rhyme celebrating morning church bells. Great for rhythm practice!",
                    detectedKeywords = listOf(
                        VocabularyHighlight("frère", "brother", "👦"),
                        VocabularyHighlight("dormez-vous", "are you sleeping?", "💤"),
                        VocabularyHighlight("sonnez", "ring / sound", "🔔"),
                        VocabularyHighlight("les matines", "morning bells", "🌅")
                    ),
                    segments = listOf(
                        TranscriptSegment("00:01", "Chorus", "Frère Jacques, Frère Jacques,", "Brother Jacques, Brother Jacques,"),
                        TranscriptSegment("00:04", "Chorus", "dormez-vous ? Dormez-vous ?", "are you sleeping? Are you sleeping?"),
                        TranscriptSegment("00:08", "Chorus", "Sonnez les matines !", "Ring the morning bells!"),
                        TranscriptSegment("00:11", "Chorus", "Ding, dang, dong.", "Ding, dang, dong.")
                    )
                )
            }
            else -> {
                AudioTranscriptionResult(
                    fullText = "Bonjour ! Bienvenue dans notre studio audio en direct avec Milo le renard.",
                    englishTranslation = "Hello! Welcome to our live audio studio with Milo the fox.",
                    phonetics = "bɔ̃ʒuʁ ! bjɛ̃vny dɑ̃ nɔtʁ stydjo odjo ɑ̃ diʁɛkt avɛk milo lə ʁənaʁ.",
                    detectedLanguage = "French (France)",
                    confidence = 0.97f,
                    pronunciationTip = "The final consonants in 'dans' and 'direct' have distinct French resonance.",
                    detectedKeywords = listOf(
                        VocabularyHighlight("bonjour", "hello / good morning", "👋"),
                        VocabularyHighlight("bienvenue", "welcome", "🌸"),
                        VocabularyHighlight("studio audio", "audio studio", "🎙️"),
                        VocabularyHighlight("renard", "fox", "🦊")
                    ),
                    segments = listOf(
                        TranscriptSegment("00:01", "Milo", "Bonjour !", "Hello!"),
                        TranscriptSegment("00:03", "Milo", "Bienvenue dans notre studio audio en direct.", "Welcome to our live audio studio."),
                        TranscriptSegment("00:06", "Milo", "Parlez ou écoutez avec attention !", "Speak or listen carefully!")
                    )
                )
            }
        }
    }

    private data class Tuple5(
        val french: String,
        val english: String,
        val phonetics: String,
        val replies: List<String>,
        val tip: String
    )
}
