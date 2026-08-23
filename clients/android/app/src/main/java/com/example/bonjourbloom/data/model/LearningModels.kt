package com.example.bonjourbloom.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class LearningState(val displayName: String) {
    NEW("New"),
    LEARNING("Learning"),
    REVIEW("Review"),
    RELEARNING("Relearning"),
    SUSPENDED("Suspended")
}

@Serializable
enum class ReviewGrade(val label: String, val note: String) {
    AGAIN("Again", "Need to practice again"),
    HARD("Hard", "Recalled with difficulty or hint"),
    GOOD("Good", "Recalled correctly"),
    EASY("Easy", "Recalled smoothly and quickly")
}

@Serializable
enum class EventType {
    EXPOSED,
    STARTED,
    ANSWERED,
    COMPLETED,
    ABANDONED
}

@Serializable
enum class UnitType {
    VOCABULARY,
    PHRASE,
    OBJECTIVE,
    EXERCISE_PROMPT
}

@Serializable
enum class ObjectiveStatus(val displayName: String, val levelPercentage: Int) {
    NOT_STARTED("Not Started", 0),
    INTRODUCED("Introduced", 20),
    PRACTISING("Practising", 50),
    FAMILIAR("Familiar", 75),
    MASTERED("Mastered", 100),
    NEEDS_REVIEW("Needs Review", 60)
}

@Serializable
enum class PlanStepType(val title: String) {
    DUE_REVIEW("Due Review"),
    RESUME_LESSON("Resume Lesson"),
    NEXT_LESSON("New Lesson"),
    NEW_VOCAB("New Words"),
    SESSION_RECAP("Session Recap")
}

@Serializable
data class ReviewableUnit(
    val unitId: String,
    val profileId: String,
    val language: String = "fr",
    val unitType: UnitType = UnitType.VOCABULARY,
    val title: String,
    val frenchText: String,
    val englishText: String,
    val topic: String,
    val emoji: String,
    val objectiveId: String,
    val learningState: LearningState = LearningState.NEW,
    val firstSeenEpochMs: Long = 0L,
    val lastAttemptEpochMs: Long = 0L,
    val lastSuccessEpochMs: Long = 0L,
    val nextReviewEpochMs: Long = 0L,
    val currentIntervalDays: Float = 0f,
    val consecutiveSuccesses: Int = 0,
    val lapseCount: Int = 0,
    val totalReviewCount: Int = 0,
    val recentGrade: ReviewGrade? = null,
    val schedulerVersion: String = "v1_deterministic",
    val isSuspended: Boolean = false
)

@Serializable
data class LearningAttempt(
    val id: String,
    val profileId: String,
    val targetLanguage: String = "fr",
    val lessonId: String? = null,
    val exerciseId: String? = null,
    val unitId: String? = null,
    val objectiveId: String? = null,
    val activityType: ExerciseKind,
    val eventType: EventType = EventType.ANSWERED,
    val submittedAnswer: String,
    val isCorrect: Boolean,
    val score: Float, // 0.0 to 1.0
    val hintUsed: Boolean = false,
    val retryNumber: Int = 0,
    val responseDurationMs: Long = 0L,
    val grade: ReviewGrade,
    val idempotencyKey: String,
    val timestampEpochMs: Long = System.currentTimeMillis(),
    val contentVersion: Int = 1
)

@Serializable
data class ObjectiveMastery(
    val objectiveId: String,
    val profileId: String,
    val title: String,
    val description: String,
    val cefrLevel: String = "Pre-A1",
    val status: ObjectiveStatus = ObjectiveStatus.NOT_STARTED,
    val masteryScore: Float = 0f, // 0.0 to 1.0
    val evidenceCount: Int = 0,
    val lastDemonstratedEpochMs: Long = 0L
)

@Serializable
data class PlanStep(
    val id: String,
    val stepType: PlanStepType,
    val title: String,
    val subtitle: String,
    val reasonCode: String,
    val lessonId: String? = null,
    val unitIds: List<String> = emptyList(),
    val estimatedMinutes: Int = 2,
    val isCompleted: Boolean = false
)

@Serializable
data class DailyLearningPlan(
    val id: String,
    val profileId: String,
    val dateString: String,
    val targetMinutes: Int,
    val estimatedMinutes: Int,
    val steps: List<PlanStep>,
    val isCompleted: Boolean = false,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
