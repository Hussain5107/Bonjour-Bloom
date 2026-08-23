package com.example.bonjourbloom.data.engine

import com.example.bonjourbloom.data.curriculum.CurriculumData
import com.example.bonjourbloom.data.model.AgeBand
import com.example.bonjourbloom.data.model.DailyLearningPlan
import com.example.bonjourbloom.data.model.LearningState
import com.example.bonjourbloom.data.model.PlanStep
import com.example.bonjourbloom.data.model.PlanStepType
import com.example.bonjourbloom.data.model.Profile
import com.example.bonjourbloom.data.model.ReviewableUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

/**
 * Deterministic Daily Learning Planner
 *
 * Constructs a balanced, age-adapted learning plan strictly respecting the profile's
 * target time and published curriculum availability.
 */
object DailyLearningPlanner {

    fun generateDailyPlan(
        profile: Profile,
        reviewUnits: List<ReviewableUnit>,
        dateString: String = getTodayDateString(),
        nowEpochMs: Long = System.currentTimeMillis()
    ): DailyLearningPlan {
        val targetMinutes = max(5, profile.dailyGoal * 5) // convert goal slots (1..6) to 5..30 min
        val steps = mutableListOf<PlanStep>()
        var allocatedMinutes = 0

        // 1. Identify due reviews
        val dueUnits = reviewUnits.filter {
            !it.isSuspended && (it.nextReviewEpochMs <= nowEpochMs || it.learningState == LearningState.RELEARNING)
        }.sortedBy { it.nextReviewEpochMs }

        // Cap due reviews by time target
        val maxReviews = when {
            targetMinutes <= 5 -> 3
            targetMinutes <= 10 -> 6
            targetMinutes <= 15 -> 9
            targetMinutes <= 20 -> 12
            else -> 15
        }

        val selectedDueUnits = dueUnits.take(maxReviews)
        if (selectedDueUnits.isNotEmpty()) {
            val estMin = max(1, (selectedDueUnits.size * 0.5f).toInt())
            steps.add(
                PlanStep(
                    id = "step_due_reviews_${dateString}",
                    stepType = PlanStepType.DUE_REVIEW,
                    title = "Memory Review (${selectedDueUnits.size} words)",
                    subtitle = "Strengthen words scheduled for review today",
                    reasonCode = "DUE_FOR_REVIEW",
                    unitIds = selectedDueUnits.map { it.unitId },
                    estimatedMinutes = estMin
                )
            )
            allocatedMinutes += estMin
        }

        // 2. Check for Incomplete Lesson to resume
        val incompleteLessonId = profile.currentLessonId
        val incompleteLesson = CurriculumData.lessonsList.find { it.id == incompleteLessonId }
        if (incompleteLesson != null && !profile.completedLessons.contains(incompleteLessonId)) {
            val estMin = min(incompleteLesson.minutes, max(2, targetMinutes - allocatedMinutes))
            steps.add(
                PlanStep(
                    id = "step_resume_${incompleteLesson.id}",
                    stepType = PlanStepType.RESUME_LESSON,
                    title = "Resume: ${incompleteLesson.title}",
                    subtitle = incompleteLesson.subtitle,
                    reasonCode = "RESUME_INCOMPLETE_LESSON",
                    lessonId = incompleteLesson.id,
                    estimatedMinutes = estMin
                )
            )
            allocatedMinutes += estMin
        } else {
            // 3. Find Next Published Lesson (Prerequisites satisfied)
            val nextLesson = CurriculumData.lessonsList.firstOrNull { lesson ->
                !profile.completedLessons.contains(lesson.id)
            }

            if (nextLesson != null && allocatedMinutes < targetMinutes) {
                val estMin = nextLesson.minutes
                steps.add(
                    PlanStep(
                        id = "step_next_${nextLesson.id}",
                        stepType = PlanStepType.NEXT_LESSON,
                        title = "New Lesson: ${nextLesson.title}",
                        subtitle = nextLesson.subtitle,
                        reasonCode = "NEXT_CURRICULUM_LESSON",
                        lessonId = nextLesson.id,
                        estimatedMinutes = estMin
                    )
                )
                allocatedMinutes += estMin
            }
        }

        // 4. Introduce New Words if budget remains
        if (allocatedMinutes < targetMinutes) {
            val maxNewWords = when (profile.ageBand) {
                AgeBand.EARLY -> 2
                AgeBand.JUNIOR -> 3
                AgeBand.EXPLORER, AgeBand.ADULT -> 4
            }

            val existingUnitIds = reviewUnits.map { it.unitId }.toSet()
            val unencounteredVocab = CurriculumData.vocabularyList.filter {
                !existingUnitIds.contains(it.id) || reviewUnits.find { u -> u.unitId == it.id }?.learningState == LearningState.NEW
            }.take(maxNewWords)

            if (unencounteredVocab.isNotEmpty()) {
                val estMin = 2
                steps.add(
                    PlanStep(
                        id = "step_new_vocab_${dateString}",
                        stepType = PlanStepType.NEW_VOCAB,
                        title = "Discover New Words (${unencounteredVocab.size})",
                        subtitle = "Expand your French vocabulary step by step",
                        reasonCode = "EXPAND_VOCABULARY",
                        unitIds = unencounteredVocab.map { it.id },
                        estimatedMinutes = estMin
                    )
                )
                allocatedMinutes += estMin
            }
        }

        // 5. Session Recap
        steps.add(
            PlanStep(
                id = "step_recap_${dateString}",
                stepType = PlanStepType.SESSION_RECAP,
                title = "Daily Recap & Petals",
                subtitle = "Celebrate your learning progress",
                reasonCode = "SESSION_RECAP",
                estimatedMinutes = 1
            )
        )
        allocatedMinutes += 1

        val planId = "plan_${profile.id}_${dateString}"

        return DailyLearningPlan(
            id = planId,
            profileId = profile.id,
            dateString = dateString,
            targetMinutes = targetMinutes,
            estimatedMinutes = allocatedMinutes,
            steps = steps,
            isCompleted = false,
            createdAtEpochMs = nowEpochMs
        )
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }
}
