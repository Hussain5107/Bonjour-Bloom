package com.example.bonjourbloom

import com.example.bonjourbloom.data.curriculum.CurriculumData
import com.example.bonjourbloom.data.engine.DailyLearningPlanner
import com.example.bonjourbloom.data.engine.DeterministicReviewScheduler
import com.example.bonjourbloom.data.engine.MasteryCalculator
import com.example.bonjourbloom.data.model.AgeBand
import com.example.bonjourbloom.data.model.ExerciseKind
import com.example.bonjourbloom.data.model.ExerciseType
import com.example.bonjourbloom.data.model.LearningAttempt
import com.example.bonjourbloom.data.model.LearningState
import com.example.bonjourbloom.data.model.ObjectiveStatus
import com.example.bonjourbloom.data.model.PlanStepType
import com.example.bonjourbloom.data.model.Profile
import com.example.bonjourbloom.data.model.ReviewGrade
import com.example.bonjourbloom.data.model.ReviewableUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BonjourBloomUnitTest {

    @Test
    fun testCurriculumDataLoaded() {
        val lessons = CurriculumData.lessonsList
        assertEquals(5, lessons.size)

        val firstLesson = lessons.first()
        assertEquals("prea1-greetings-001", firstLesson.id)
        assertEquals("Hello & Goodbye", firstLesson.title)
        assertTrue(firstLesson.exercises.isNotEmpty())

        val vocabulary = CurriculumData.vocabularyList
        assertTrue(vocabulary.size >= 12)
        val bonjour = vocabulary.find { it.id == "bonjour" }
        assertNotNull(bonjour)
        assertEquals("Hello / Good morning", bonjour?.english)
    }

    @Test
    fun testAgeBandModes() {
        val early = AgeBand.EARLY
        assertEquals("5–7", early.ageRange)
        val junior = AgeBand.JUNIOR
        assertEquals("8–9", junior.ageRange)
        val explorer = AgeBand.EXPLORER
        assertEquals("10–11", explorer.ageRange)
        val adult = AgeBand.ADULT
        assertEquals("12+", adult.ageRange)
    }

    @Test
    fun testExerciseTypes() {
        val lesson = CurriculumData.lessonsList.first()
        val listenEx = lesson.exercises.find { it.type == ExerciseType.LISTEN }
        assertNotNull(listenEx)
        val choiceEx = lesson.exercises.find { it.type == ExerciseType.MULTIPLE_CHOICE }
        assertNotNull(choiceEx)
    }

    @Test
    fun testGeminiVoiceOfflineFallback() {
        val exchange = com.example.bonjourbloom.gemini.GeminiLiveClient
            .generateOfflineFallbackVoiceExchange("bonjour", "Greetings & Names")
        assertEquals("milo", exchange.speaker)
        assertTrue(exchange.frenchText.isNotEmpty())
        assertTrue(exchange.suggestedReplies.isNotEmpty())
    }

    @Test
    fun testAudioFeedPresetsAndTranscription() {
        val presets = com.example.bonjourbloom.data.curriculum.SampleAudioFeeds.presets
        assertTrue(presets.isNotEmpty())
        val bakery = presets.find { it.id == "bakery" }
        assertNotNull(bakery)

        val transcription = com.example.bonjourbloom.gemini.GeminiLiveClient
            .generateFallbackTranscription("Bakery dialogue")
        assertTrue(transcription.fullText.contains("croissant", ignoreCase = true))
        assertTrue(transcription.detectedKeywords.isNotEmpty())
    }

    @Test
    fun testDeterministicReviewSchedulerProgression() {
        val scheduler = DeterministicReviewScheduler()
        val now = 1700000000000L

        val initialUnit = ReviewableUnit(
            unitId = "bonjour",
            profileId = "p1",
            title = "Bonjour",
            frenchText = "Bonjour",
            englishText = "Hello",
            topic = "greetings",
            emoji = "👋",
            objectiveId = "obj_greetings",
            learningState = LearningState.NEW
        )

        // 1st GOOD answer: 1 day interval, state -> LEARNING
        val r1 = scheduler.calculateNextReview(initialUnit, ReviewGrade.GOOD, false, now)
        assertEquals(LearningState.LEARNING, r1.newState)
        assertEquals(1.0f, r1.intervalDays, 0.01f)
        assertEquals(1, r1.consecutiveSuccesses)

        // 2nd GOOD answer: 3 days interval, state -> REVIEW
        val unit2 = initialUnit.copy(
            learningState = r1.newState,
            currentIntervalDays = r1.intervalDays,
            consecutiveSuccesses = r1.consecutiveSuccesses
        )
        val r2 = scheduler.calculateNextReview(unit2, ReviewGrade.GOOD, false, now + 86400000L)
        assertEquals(LearningState.REVIEW, r2.newState)
        assertEquals(3.0f, r2.intervalDays, 0.01f)
        assertEquals(2, r2.consecutiveSuccesses)

        // 3rd GOOD answer: 7 days interval
        val unit3 = unit2.copy(
            learningState = r2.newState,
            currentIntervalDays = r2.intervalDays,
            consecutiveSuccesses = r2.consecutiveSuccesses
        )
        val r3 = scheduler.calculateNextReview(unit3, ReviewGrade.GOOD, false, now + 3 * 86400000L)
        assertEquals(7.0f, r3.intervalDays, 0.01f)
        assertEquals(3, r3.consecutiveSuccesses)

        // Lapse with AGAIN: resets interval to 1 day, state -> RELEARNING, consecutive -> 0
        val rLapse = scheduler.calculateNextReview(unit3, ReviewGrade.AGAIN, false, now + 7 * 86400000L)
        assertEquals(LearningState.RELEARNING, rLapse.newState)
        assertEquals(1.0f, rLapse.intervalDays, 0.01f)
        assertEquals(0, rLapse.consecutiveSuccesses)
        assertEquals(1, rLapse.lapseCount)
    }

    @Test
    fun testMasteryCalculatorProgression() {
        val now = System.currentTimeMillis()
        val objDef = MasteryCalculator.DEFINED_OBJECTIVES.first { it.id == "obj_greetings" }

        val unit = ReviewableUnit(
            unitId = "bonjour",
            profileId = "p1",
            title = "Bonjour",
            frenchText = "Bonjour",
            englishText = "Hello",
            topic = "greetings",
            emoji = "👋",
            objectiveId = "obj_greetings",
            learningState = LearningState.NEW
        )

        // Empty attempts -> NOT_STARTED
        val m0 = MasteryCalculator.computeObjectiveMastery("p1", objDef, listOf(unit), emptyList(), now)
        assertEquals(ObjectiveStatus.NOT_STARTED, m0.status)
        assertEquals(0f, m0.masteryScore, 0.01f)

        // 5 consecutive successes across multiple activity types -> MASTERED
        val masteredUnit = unit.copy(
            learningState = LearningState.REVIEW,
            consecutiveSuccesses = 5,
            currentIntervalDays = 7.0f,
            lapseCount = 0
        )
        val attempts = listOf(
            LearningAttempt(
                id = "a1", profileId = "p1", activityType = ExerciseKind.LISTEN,
                unitId = "bonjour", submittedAnswer = "Hello", isCorrect = true,
                score = 1f, timestampEpochMs = now - 7 * 86400000L
            ),
            LearningAttempt(
                id = "a2", profileId = "p1", activityType = ExerciseKind.REVIEW,
                unitId = "bonjour", submittedAnswer = "Hello", isCorrect = true,
                score = 1f, timestampEpochMs = now
            )
        )

        val (unitStatus, score) = MasteryCalculator.calculateUnitStatus(masteredUnit, attempts, now)
        assertEquals(ObjectiveStatus.MASTERED, unitStatus)
        assertEquals(1.0f, score, 0.01f)
    }

    @Test
    fun testDailyLearningPlannerDeterminism() {
        val profile = Profile(
            id = "learner1",
            name = "Camille",
            ageBand = AgeBand.EARLY,
            dailyGoal = 2 // 10 min target
        )

        val units = listOf(
            ReviewableUnit(
                unitId = "bonjour",
                profileId = "learner1",
                title = "Bonjour",
                frenchText = "Bonjour",
                englishText = "Hello",
                topic = "greetings",
                emoji = "👋",
                objectiveId = "obj_greetings",
                learningState = LearningState.LEARNING,
                nextReviewEpochMs = 1000L // due
            )
        )

        val plan1 = DailyLearningPlanner.generateDailyPlan(profile, units, "2026-08-23", 5000L)
        val plan2 = DailyLearningPlanner.generateDailyPlan(profile, units, "2026-08-23", 5000L)

        // Verify deterministic reproduction
        assertEquals(plan1.id, plan2.id)
        assertEquals(plan1.targetMinutes, plan2.targetMinutes)
        assertEquals(plan1.steps.size, plan2.steps.size)

        // Verify priority 1: Due reviews
        val firstStep = plan1.steps.first()
        assertEquals(PlanStepType.DUE_REVIEW, firstStep.stepType)
        assertEquals("DUE_FOR_REVIEW", firstStep.reasonCode)
        assertTrue(firstStep.unitIds.contains("bonjour"))

        // Verify recap step at the end
        val lastStep = plan1.steps.last()
        assertEquals(PlanStepType.SESSION_RECAP, lastStep.stepType)
    }
}
