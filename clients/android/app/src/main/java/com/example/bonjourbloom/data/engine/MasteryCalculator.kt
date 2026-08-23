package com.example.bonjourbloom.data.engine

import com.example.bonjourbloom.data.model.LearningAttempt
import com.example.bonjourbloom.data.model.LearningState
import com.example.bonjourbloom.data.model.ObjectiveMastery
import com.example.bonjourbloom.data.model.ObjectiveStatus
import com.example.bonjourbloom.data.model.ReviewableUnit

/**
 * Deterministic Mastery Calculator
 *
 * Implements transparent, explainable mastery progression rules:
 * - Exposure != Mastery
 * - Single correct answer != Mastery
 * - Mastery requires repeated spaced evidence across time and multiple exercise types.
 */
object MasteryCalculator {

    data class ObjectiveDefinition(
        val id: String,
        val title: String,
        val description: String,
        val topic: String,
        val cefrLevel: String = "Pre-A1"
    )

    val DEFINED_OBJECTIVES = listOf(
        ObjectiveDefinition(
            id = "obj_greetings",
            title = "Greetings & Polite Phrases",
            description = "Greet warmly, say please, thank you and goodbye",
            topic = "greetings"
        ),
        ObjectiveDefinition(
            id = "obj_introductions",
            title = "Introductions & Names",
            description = "Say your name, ask someone's name and say nice to meet you",
            topic = "introductions"
        ),
        ObjectiveDefinition(
            id = "obj_numbers",
            title = "Numbers 1 to 10 & Age",
            description = "Count accurately from 1 to 10 and state your age in French",
            topic = "numbers"
        ),
        ObjectiveDefinition(
            id = "obj_colors",
            title = "Basic Colors",
            description = "Identify and name everyday colors in French",
            topic = "colors"
        ),
        ObjectiveDefinition(
            id = "obj_family",
            title = "Family Members",
            description = "Name family members and describe who is who",
            topic = "family"
        )
    )

    /**
     * Compute the unit's mastery status and score
     */
    fun calculateUnitStatus(
        unit: ReviewableUnit,
        attempts: List<LearningAttempt>,
        nowEpochMs: Long = System.currentTimeMillis()
    ): Pair<ObjectiveStatus, Float> {
        val unitAttempts = attempts.filter { it.unitId == unit.unitId }
        val successfulAttempts = unitAttempts.filter { it.isCorrect }

        // Rule: 0 attempts = NOT_STARTED
        if (unitAttempts.isEmpty()) {
            return Pair(ObjectiveStatus.NOT_STARTED, 0f)
        }

        // Rule: Overdue check for lapsed/neglected review items
        val isSeverelyOverdue = unit.nextReviewEpochMs > 0 &&
                (nowEpochMs - unit.nextReviewEpochMs) > (7 * 86_400_000L) // 7 days overdue

        if (unit.learningState == LearningState.RELEARNING || isSeverelyOverdue) {
            return Pair(ObjectiveStatus.NEEDS_REVIEW, 0.45f)
        }

        // Rule: 1 attempt or only exposures = INTRODUCED
        if (unitAttempts.size == 1 || successfulAttempts.isEmpty()) {
            return Pair(ObjectiveStatus.INTRODUCED, 0.20f)
        }

        // Count unique exercise varieties (e.g. LISTEN, PICTURE, ORDER, SPEAK, REVIEW)
        val exerciseVarieties = unitAttempts.map { it.activityType }.distinct().size

        // Rule: MASTERED criteria (>= 5 successes, >= 7d interval, >= 2 exercise varieties, 0 recent lapses)
        if (unit.consecutiveSuccesses >= 5 &&
            unit.currentIntervalDays >= 7f &&
            unit.lapseCount == 0 &&
            exerciseVarieties >= 2
        ) {
            return Pair(ObjectiveStatus.MASTERED, 1.0f)
        }

        // Rule: FAMILIAR criteria (>= 3 successes, >= 3d interval)
        if (unit.consecutiveSuccesses >= 3 && unit.currentIntervalDays >= 3f) {
            return Pair(ObjectiveStatus.FAMILIAR, 0.75f)
        }

        // Otherwise: PRACTISING
        return Pair(ObjectiveStatus.PRACTISING, 0.50f)
    }

    /**
     * Compute objective-level mastery by aggregating review units for that objective
     */
    fun computeObjectiveMastery(
        profileId: String,
        objectiveDef: ObjectiveDefinition,
        unitsForObjective: List<ReviewableUnit>,
        attempts: List<LearningAttempt>,
        nowEpochMs: Long = System.currentTimeMillis()
    ): ObjectiveMastery {
        if (unitsForObjective.isEmpty()) {
            return ObjectiveMastery(
                objectiveId = objectiveDef.id,
                profileId = profileId,
                title = objectiveDef.title,
                description = objectiveDef.description,
                cefrLevel = objectiveDef.cefrLevel,
                status = ObjectiveStatus.NOT_STARTED,
                masteryScore = 0f,
                evidenceCount = 0,
                lastDemonstratedEpochMs = 0L
            )
        }

        var totalScore = 0f
        var totalEvidence = 0
        var lastDemonstrated = 0L
        var masteredCount = 0
        var familiarOrMasteredCount = 0
        var needsReviewCount = 0
        var notStartedCount = 0

        for (unit in unitsForObjective) {
            val (status, score) = calculateUnitStatus(unit, attempts, nowEpochMs)
            totalScore += score
            val unitAttempts = attempts.filter { it.unitId == unit.unitId && it.isCorrect }
            totalEvidence += unitAttempts.size
            if (unit.lastSuccessEpochMs > lastDemonstrated) {
                lastDemonstrated = unit.lastSuccessEpochMs
            }

            when (status) {
                ObjectiveStatus.MASTERED -> {
                    masteredCount++
                    familiarOrMasteredCount++
                }
                ObjectiveStatus.FAMILIAR -> familiarOrMasteredCount++
                ObjectiveStatus.NEEDS_REVIEW -> needsReviewCount++
                ObjectiveStatus.NOT_STARTED -> notStartedCount++
                else -> {}
            }
        }

        val averageScore = totalScore / unitsForObjective.size

        val objectiveStatus = when {
            notStartedCount == unitsForObjective.size -> ObjectiveStatus.NOT_STARTED
            needsReviewCount >= (unitsForObjective.size / 2) -> ObjectiveStatus.NEEDS_REVIEW
            masteredCount >= (unitsForObjective.size * 0.7) && familiarOrMasteredCount == unitsForObjective.size -> ObjectiveStatus.MASTERED
            familiarOrMasteredCount >= (unitsForObjective.size * 0.6) -> ObjectiveStatus.FAMILIAR
            averageScore >= 0.35f -> ObjectiveStatus.PRACTISING
            else -> ObjectiveStatus.INTRODUCED
        }

        return ObjectiveMastery(
            objectiveId = objectiveDef.id,
            profileId = profileId,
            title = objectiveDef.title,
            description = objectiveDef.description,
            cefrLevel = objectiveDef.cefrLevel,
            status = objectiveStatus,
            masteryScore = averageScore,
            evidenceCount = totalEvidence,
            lastDemonstratedEpochMs = lastDemonstrated
        )
    }
}
