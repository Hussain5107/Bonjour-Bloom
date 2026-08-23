package com.example.bonjourbloom.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.bonjourbloom.data.model.AgeBand
import com.example.bonjourbloom.data.model.PlanStep
import com.example.bonjourbloom.data.model.TeacherVoice
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromPlanSteps(value: List<PlanStep>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toPlanSteps(value: String): List<PlanStep> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromAgeBand(value: AgeBand): String = value.name

    @TypeConverter
    fun toAgeBand(value: String): AgeBand {
        return try {
            AgeBand.valueOf(value)
        } catch (e: Exception) {
            AgeBand.EARLY
        }
    }

    @TypeConverter
    fun fromTeacherVoice(value: TeacherVoice): String = value.name

    @TypeConverter
    fun toTeacherVoice(value: String): TeacherVoice {
        return try {
            TeacherVoice.valueOf(value)
        } catch (e: Exception) {
            TeacherVoice.FEMALE
        }
    }
}

@Entity(tableName = "profiles")
@TypeConverters(Converters::class)
data class ProfileEntity(
    @PrimaryKey val id: String,
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

@Entity(tableName = "family_settings")
data class FamilySettingsEntity(
    @PrimaryKey val id: Int = 1,
    val guardianReady: Boolean = false,
    val parentPin: String = "2468",
    val activeProfileId: String? = null,
    val soundEnabled: Boolean = true,
    val streakEnabled: Boolean = true,
    val downloadedOffline: Boolean = true
)

@Entity(tableName = "mastery_items")
data class MasteryEntity(
    @PrimaryKey val itemId: String,
    val profileId: String,
    val stability: Float = 0f,
    val difficulty: Float = 5f,
    val dueAtEpochMs: Long = System.currentTimeMillis(),
    val lastReviewedEpochMs: Long = System.currentTimeMillis(),
    val repetitions: Int = 0,
    val lapses: Int = 0
)

/**
 * Immutable/Append-only Learning Attempt record for scorable exercises and retrieval events.
 */
@Entity(
    tableName = "learning_attempts",
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["unitId"]),
        Index(value = ["idempotencyKey"], unique = true),
        Index(value = ["timestampEpochMs"])
    ]
)
data class LearningAttemptEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    val targetLanguage: String = "fr",
    val lessonId: String? = null,
    val exerciseId: String? = null,
    val unitId: String? = null,
    val objectiveId: String? = null,
    val activityType: String,
    val eventType: String,
    val submittedAnswer: String,
    val isCorrect: Boolean,
    val score: Float,
    val hintUsed: Boolean,
    val retryNumber: Int,
    val responseDurationMs: Long,
    val grade: String,
    val idempotencyKey: String,
    val timestampEpochMs: Long,
    val contentVersion: Int = 1
)

/**
 * Persistent Reviewable Unit state for spaced repetition scheduler.
 */
@Entity(
    tableName = "review_items",
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["profileId", "unitId"], unique = true),
        Index(value = ["nextReviewEpochMs"]),
        Index(value = ["topic"])
    ]
)
data class ReviewItemEntity(
    @PrimaryKey val primaryKey: String, // "${profileId}_${unitId}"
    val unitId: String,
    val profileId: String,
    val language: String = "fr",
    val unitType: String = "VOCABULARY",
    val title: String,
    val frenchText: String,
    val englishText: String,
    val topic: String,
    val emoji: String,
    val objectiveId: String,
    val learningState: String,
    val firstSeenEpochMs: Long,
    val lastAttemptEpochMs: Long,
    val lastSuccessEpochMs: Long,
    val nextReviewEpochMs: Long,
    val currentIntervalDays: Float,
    val consecutiveSuccesses: Int,
    val lapseCount: Int,
    val totalReviewCount: Int,
    val recentGrade: String?,
    val schedulerVersion: String = "v1_deterministic",
    val isSuspended: Boolean = false
)

/**
 * Persistent Objective Mastery state.
 */
@Entity(
    tableName = "objective_mastery",
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["profileId", "objectiveId"], unique = true)
    ]
)
data class ObjectiveMasteryEntity(
    @PrimaryKey val primaryKey: String, // "${profileId}_${objectiveId}"
    val objectiveId: String,
    val profileId: String,
    val title: String,
    val description: String,
    val cefrLevel: String = "Pre-A1",
    val status: String,
    val masteryScore: Float,
    val evidenceCount: Int,
    val lastDemonstratedEpochMs: Long
)

/**
 * Persistent Daily Learning Plan cache.
 */
@Entity(
    tableName = "daily_plans",
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["profileId", "dateString"], unique = true)
    ]
)
@TypeConverters(Converters::class)
data class DailyPlanEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    val dateString: String,
    val targetMinutes: Int,
    val estimatedMinutes: Int,
    val steps: List<PlanStep>,
    val isCompleted: Boolean = false,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
