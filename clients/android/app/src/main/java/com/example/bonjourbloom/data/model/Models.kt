package com.example.bonjourbloom.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class AgeBand(val label: String, val ageRange: String, val note: String) {
    EARLY("Early Learner", "5–7", "Audio-first · 5–8 min"),
    JUNIOR("Junior", "8–9", "Read & build · 8–12 min"),
    EXPLORER("Explorer", "10–11", "Speak & explore · 10–15 min"),
    ADULT("Adult Beginner", "12+", "Practical French · clear grammar")
}

@Serializable
enum class TeacherVoice(val displayName: String) {
    FEMALE("Adult female · standard French"),
    MALE("Adult male · standard French")
}

@Serializable
enum class ExerciseKind {
    LISTEN,
    PICTURE,
    ORDER,
    MISSING,
    SPEAK,
    DIALOGUE,
    REVIEW
}

@Serializable
data class VocabularyItem(
    val id: String,
    val french: String,
    val english: String,
    val article: String,
    val gender: String, // "m", "f", "n"
    val partOfSpeech: String,
    val hint: String,
    val level: String, // "Pre-A1"
    val topic: String, // "greetings", "introductions", "numbers", "colors", "family"
    val emoji: String,
    val example: String,
    val translation: String
)

@Serializable
data class ExerciseOption(
    val id: String,
    val label: String,
    val image: String? = null,
    val audioText: String? = null
)

@Serializable
data class Exercise(
    val id: String,
    val kind: ExerciseKind,
    val prompt: String,
    val answer: String,
    val options: List<String>,
    val emoji: String = "✦",
    val audioText: String? = null,
    val ageModes: List<AgeBand> = listOf(AgeBand.EARLY, AgeBand.JUNIOR, AgeBand.EXPLORER, AgeBand.ADULT),
    val explanation: String? = null
)

@Serializable
data class DialogueLine(
    val speaker: String,
    val french: String,
    val english: String
)

@Serializable
data class Lesson(
    val id: String,
    val title: String,
    val subtitle: String,
    val outcome: String,
    val minutes: Int,
    val colorHex: String,
    val exercises: List<Exercise>,
    val dialogue: List<DialogueLine> = emptyList(),
    val grammarNote: String? = null,
    val culturalNote: String? = null
)

@Serializable
data class MasteryState(
    val itemId: String,
    val stability: Float = 0f,
    val difficulty: Float = 5f,
    val dueAtEpochMs: Long = System.currentTimeMillis(),
    val lastReviewedEpochMs: Long = System.currentTimeMillis(),
    val repetitions: Int = 0,
    val lapses: Int = 0
)

@Serializable
data class Profile(
    val id: String,
    val name: String,
    val ageBand: AgeBand = AgeBand.EARLY,
    val avatar: String = "🦊",
    val dailyGoal: Int = 1,
    val completedLessons: List<String> = emptyList(),
    val xp: Int = 0,
    val minutes: Int = 0,
    val reviews: Int = 0,
    val teacherVoice: TeacherVoice = TeacherVoice.FEMALE,
    val currentLessonId: String? = null,
    val currentActivityIndex: Int = 0,
    val speakingAttempts: Int = 0,
    val streakDays: Int = 1,
    val achievements: List<String> = emptyList()
)

@Serializable
data class FamilySettings(
    val guardianReady: Boolean = false,
    val parentPin: String = "2468",
    val activeProfileId: String? = null,
    val soundEnabled: Boolean = true,
    val streakEnabled: Boolean = true,
    val downloadedOffline: Boolean = true
)

@Serializable
data class Achievement(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val isEarned: Boolean
)
