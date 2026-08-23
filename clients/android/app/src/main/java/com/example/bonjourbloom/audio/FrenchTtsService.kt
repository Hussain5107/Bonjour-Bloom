package com.example.bonjourbloom.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import com.example.bonjourbloom.data.model.TeacherVoice
import java.util.Locale

class FrenchTtsService(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false
    private var currentVoicePref: TeacherVoice = TeacherVoice.FEMALE

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.FRENCH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("FrenchTtsService", "French language not fully supported, trying default")
                tts?.setLanguage(Locale.FRANCE)
            }
            applyVoicePreference()
            isInitialized = true
        } else {
            Log.e("FrenchTtsService", "TTS Initialization failed with status $status")
        }
    }

    fun setTeacherVoice(voice: TeacherVoice) {
        currentVoicePref = voice
        if (isInitialized) {
            applyVoicePreference()
        }
    }

    private fun applyVoicePreference() {
        val ttsInstance = tts ?: return
        try {
            val voices = ttsInstance.voices ?: return
            val frenchVoices = voices.filter { it.locale.language.startsWith("fr") }
            if (frenchVoices.isNotEmpty()) {
                val matched = when (currentVoicePref) {
                    TeacherVoice.FEMALE -> frenchVoices.find {
                        it.name.contains("female", ignoreCase = true) ||
                        it.name.contains("femme", ignoreCase = true) ||
                        it.name.contains("audrey", ignoreCase = true) ||
                        it.name.contains("amelie", ignoreCase = true)
                    } ?: frenchVoices.first()

                    TeacherVoice.MALE -> frenchVoices.find {
                        it.name.contains("male", ignoreCase = true) ||
                        it.name.contains("homme", ignoreCase = true) ||
                        it.name.contains("thomas", ignoreCase = true) ||
                        it.name.contains("paul", ignoreCase = true)
                    } ?: frenchVoices.first()
                }
                ttsInstance.voice = matched
            }
        } catch (e: Exception) {
            Log.w("FrenchTtsService", "Voice selection fallback: ${e.message}")
        }
    }

    fun speak(text: String, isSlow: Boolean = false) {
        if (!isInitialized) return
        val ttsInstance = tts ?: return
        ttsInstance.stop()
        ttsInstance.setSpeechRate(if (isSlow) 0.68f else 0.92f)
        ttsInstance.setPitch(if (currentVoicePref == TeacherVoice.FEMALE) 1.05f else 0.95f)
        ttsInstance.speak(text, TextToSpeech.QUEUE_FLUSH, null, "bonjour_bloom_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
