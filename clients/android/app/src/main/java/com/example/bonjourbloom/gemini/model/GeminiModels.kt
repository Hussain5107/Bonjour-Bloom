package com.example.bonjourbloom.gemini.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val tools: List<JsonObject>? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val role: String? = null,
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String
)

@Serializable
data class GenerationConfig(
    val responseFormat: ResponseFormat? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val responseModalities: List<String>? = null,
    val speechConfig: SpeechConfig? = null
)

@Serializable
data class ResponseFormat(
    val text: ResponseFormatText? = null
)

@Serializable
data class ResponseFormatText(
    val mimeType: String,
    val schema: JsonObject? = null
)

@Serializable
data class SpeechConfig(
    val voiceConfig: VoiceConfig
)

@Serializable
data class VoiceConfig(
    val prebuiltVoiceConfig: PrebuiltVoiceConfig
)

@Serializable
data class PrebuiltVoiceConfig(
    val voiceName: String
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate> = emptyList(),
    val usageMetadata: UsageMetadata? = null
)

@Serializable
data class Candidate(
    val content: Content? = null,
    val finishReason: String? = null
)

@Serializable
data class UsageMetadata(
    val promptTokenCount: Int? = null,
    val candidatesTokenCount: Int? = null,
    val totalTokenCount: Int? = null
)

// UI & Functional Domain Models
@Serializable
data class VoiceExchange(
    val id: String,
    val speaker: String, // "user" or "milo"
    val frenchText: String,
    val englishTranslation: String = "",
    val phonetics: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedReplies: List<String> = emptyList(),
    val celebrationBadge: String? = null,
    val tip: String? = null
)

@Serializable
data class AudioTranscriptionResult(
    val fullText: String = "",
    val englishTranslation: String = "",
    val phonetics: String = "",
    val detectedLanguage: String = "French (France)",
    val confidence: Float = 0.95f,
    val detectedKeywords: List<VocabularyHighlight> = emptyList(),
    val segments: List<TranscriptSegment> = emptyList(),
    val pronunciationTip: String = ""
)

@Serializable
data class VocabularyHighlight(
    val french: String,
    val english: String,
    val icon: String = "🌱"
)

@Serializable
data class TranscriptSegment(
    val timestamp: String,
    val speaker: String,
    val frenchText: String,
    val englishText: String
)
