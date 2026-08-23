package com.example.bonjourbloom.data.engine

import com.example.bonjourbloom.data.model.LearningState
import com.example.bonjourbloom.data.model.ReviewGrade
import com.example.bonjourbloom.data.model.ReviewableUnit
import kotlin.math.max
import kotlin.math.min

/**
 * Interface defining the spaced-review scheduler boundary.
 * Keeps scheduling logic decoupled, testable, and versioned.
 */
interface ReviewScheduler {
    val version: String

    fun calculateNextReview(
        unit: ReviewableUnit,
        grade: ReviewGrade,
        hintUsed: Boolean,
        nowEpochMs: Long = System.currentTimeMillis()
    ): ReviewScheduleResult
}

data class ReviewScheduleResult(
    val nextReviewEpochMs: Long,
    val intervalDays: Float,
    val newState: LearningState,
    val consecutiveSuccesses: Int,
    val lapseCount: Int,
    val effectiveGrade: ReviewGrade
)

/**
 * Deterministic Spaced-Review Scheduler (Version: v1_deterministic)
 *
 * Explicit rules:
 * 1. Hint usage forces grade down to at most HARD.
 * 2. AGAIN resets consecutive successes to 0, increments lapses, and schedules relearning (1 day or immediate queue).
 * 3. HARD increases interval conservatively (1.2x factor, min 1 day).
 * 4. GOOD follows a predictable expansion ladder (1d -> 3d -> 7d -> 14d -> 30d -> 60d -> 120d -> 180d).
 * 5. EASY accelerates expansion ladder (2d -> 5d -> 10d -> 21d -> 45d -> 90d -> 180d).
 * 6. Bounds: Min interval 0.04 days (~1 hr), Max interval 180 days.
 * 7. Rapid same-session retries (< 4 hours) do not advance long-term interval beyond 1 day if not previously graduated.
 */
class DeterministicReviewScheduler : ReviewScheduler {

    override val version: String = SCHEDULER_VERSION

    companion object {
        const val SCHEDULER_VERSION = "v1_deterministic"

        const val MIN_INTERVAL_DAYS = 0.04f // ~1 hour
        const val MAX_INTERVAL_DAYS = 180f
        const val MS_PER_DAY = 86_400_000L
        const val MIN_SESSION_GAP_MS = 4 * 3_600_000L // 4 hours

        // Stepped interval ladders for predictable progression
        val GOOD_LADDER = listOf(1f, 3f, 7f, 14f, 30f, 60f, 120f, 180f)
        val EASY_LADDER = listOf(2f, 5f, 10f, 21f, 45f, 90f, 180f)
    }

    override fun calculateNextReview(
        unit: ReviewableUnit,
        grade: ReviewGrade,
        hintUsed: Boolean,
        nowEpochMs: Long
    ): ReviewScheduleResult {
        // Enforce rule: Hint usage caps grade at HARD
        val effectiveGrade = if (hintUsed && (grade == ReviewGrade.GOOD || grade == ReviewGrade.EASY)) {
            ReviewGrade.HARD
        } else {
            grade
        }

        val timeSinceLastAttempt = nowEpochMs - unit.lastAttemptEpochMs
        val isRapidSameSessionRetry = unit.lastAttemptEpochMs > 0 && timeSinceLastAttempt in 1 until MIN_SESSION_GAP_MS

        val currentSuccesses = unit.consecutiveSuccesses
        val currentLapses = unit.lapseCount

        val (nextIntervalDays, newState, nextSuccesses, nextLapses) = when (effectiveGrade) {
            ReviewGrade.AGAIN -> {
                val nextLapseCount = currentLapses + 1
                val interval = if (unit.learningState == LearningState.NEW) {
                    MIN_INTERVAL_DAYS
                } else {
                    1.0f
                }
                ReviewResultTuple(
                    intervalDays = interval,
                    state = if (unit.learningState == LearningState.NEW) LearningState.LEARNING else LearningState.RELEARNING,
                    consecutiveSuccesses = 0,
                    lapses = nextLapseCount
                )
            }

            ReviewGrade.HARD -> {
                val nextSuccess = currentSuccesses + 1
                val baseInterval = if (unit.currentIntervalDays <= 0f) 1.0f else unit.currentIntervalDays * 1.2f
                val clampedInterval = min(MAX_INTERVAL_DAYS, max(1.0f, baseInterval))
                val state = if (unit.learningState == LearningState.NEW || unit.learningState == LearningState.LEARNING) {
                    LearningState.LEARNING
                } else {
                    LearningState.REVIEW
                }
                ReviewResultTuple(
                    intervalDays = clampedInterval,
                    state = state,
                    consecutiveSuccesses = nextSuccess,
                    lapses = currentLapses
                )
            }

            ReviewGrade.GOOD -> {
                val nextSuccess = currentSuccesses + 1
                val interval = if (isRapidSameSessionRetry && currentSuccesses == 0) {
                    // Rapid same-session retry only gives preliminary advancement
                    1.0f
                } else {
                    val ladderIndex = min(currentSuccesses, GOOD_LADDER.size - 1)
                    GOOD_LADDER[ladderIndex]
                }
                ReviewResultTuple(
                    intervalDays = min(MAX_INTERVAL_DAYS, max(1.0f, interval)),
                    state = LearningState.REVIEW,
                    consecutiveSuccesses = nextSuccess,
                    lapses = currentLapses
                )
            }

            ReviewGrade.EASY -> {
                val nextSuccess = currentSuccesses + 1
                val ladderIndex = min(currentSuccesses, EASY_LADDER.size - 1)
                val interval = EASY_LADDER[ladderIndex]
                ReviewResultTuple(
                    intervalDays = min(MAX_INTERVAL_DAYS, max(2.0f, interval)),
                    state = LearningState.REVIEW,
                    consecutiveSuccesses = nextSuccess,
                    lapses = currentLapses
                )
            }
        }

        val nextReviewEpochMs = nowEpochMs + (nextIntervalDays * MS_PER_DAY).toLong()

        return ReviewScheduleResult(
            nextReviewEpochMs = nextReviewEpochMs,
            intervalDays = nextIntervalDays,
            newState = newState,
            consecutiveSuccesses = nextSuccesses,
            lapseCount = nextLapses,
            effectiveGrade = effectiveGrade
        )
    }

    private data class ReviewResultTuple(
        val intervalDays: Float,
        val state: LearningState,
        val consecutiveSuccesses: Int,
        val lapses: Int
    )
}
