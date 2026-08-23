package com.example.bonjourbloom.data.repository

import com.example.bonjourbloom.data.curriculum.CurriculumData
import com.example.bonjourbloom.data.engine.DailyLearningPlanner
import com.example.bonjourbloom.data.engine.DeterministicReviewScheduler
import com.example.bonjourbloom.data.engine.MasteryCalculator
import com.example.bonjourbloom.data.local.AppDatabase
import com.example.bonjourbloom.data.local.DailyPlanEntity
import com.example.bonjourbloom.data.local.FamilySettingsEntity
import com.example.bonjourbloom.data.local.LearningAttemptEntity
import com.example.bonjourbloom.data.local.MasteryEntity
import com.example.bonjourbloom.data.local.ObjectiveMasteryEntity
import com.example.bonjourbloom.data.local.ProfileEntity
import com.example.bonjourbloom.data.local.ReviewItemEntity
import com.example.bonjourbloom.data.model.AgeBand
import com.example.bonjourbloom.data.model.DailyLearningPlan
import com.example.bonjourbloom.data.model.EventType
import com.example.bonjourbloom.data.model.ExerciseKind
import com.example.bonjourbloom.data.model.FamilySettings
import com.example.bonjourbloom.data.model.LearningAttempt
import com.example.bonjourbloom.data.model.LearningState
import com.example.bonjourbloom.data.model.MasteryState
import com.example.bonjourbloom.data.model.ObjectiveMastery
import com.example.bonjourbloom.data.model.ObjectiveStatus
import com.example.bonjourbloom.data.model.Profile
import com.example.bonjourbloom.data.model.ReviewGrade
import com.example.bonjourbloom.data.model.ReviewableUnit
import com.example.bonjourbloom.data.model.TeacherVoice
import com.example.bonjourbloom.data.model.UnitType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class BonjourBloomRepository(private val database: AppDatabase) {

    private val profileDao = database.profileDao()
    private val settingsDao = database.familySettingsDao()
    private val masteryDao = database.masteryDao()
    private val attemptDao = database.learningAttemptDao()
    private val reviewItemDao = database.reviewItemDao()
    private val objectiveMasteryDao = database.objectiveMasteryDao()
    private val dailyPlanDao = database.dailyPlanDao()

    val scheduler = DeterministicReviewScheduler()

    val profilesFlow: Flow<List<Profile>> = profileDao.getAllProfiles().map { list ->
        list.map { it.toDomain() }
    }

    val familySettingsFlow: Flow<FamilySettings> = settingsDao.getSettings().map { entity ->
        entity?.toDomain() ?: FamilySettings()
    }

    fun getMasteryFlow(profileId: String): Flow<List<MasteryState>> =
        masteryDao.getMasteryForProfile(profileId).map { list ->
            list.map { it.toDomain() }
        }

    fun getReviewItemsFlow(profileId: String): Flow<List<ReviewableUnit>> =
        reviewItemDao.getReviewItemsForProfile(profileId).map { list ->
            list.map { it.toDomain() }
        }

    fun getObjectiveMasteriesFlow(profileId: String): Flow<List<ObjectiveMastery>> =
        objectiveMasteryDao.getMasteryForProfile(profileId).map { list ->
            list.map { it.toDomain() }
        }

    fun getDailyPlanFlow(profileId: String, dateString: String): Flow<DailyLearningPlan?> =
        dailyPlanDao.getPlanFlow(profileId, dateString).map { entity ->
            entity?.toDomain()
        }

    fun getAttemptsFlow(profileId: String): Flow<List<LearningAttempt>> =
        attemptDao.getAttemptsForProfile(profileId).map { list ->
            list.map { it.toDomain() }
        }

    suspend fun saveFamilySettings(settings: FamilySettings) {
        settingsDao.insertOrUpdateSettings(
            FamilySettingsEntity(
                id = 1,
                guardianReady = settings.guardianReady,
                parentPin = settings.parentPin,
                activeProfileId = settings.activeProfileId,
                soundEnabled = settings.soundEnabled,
                streakEnabled = settings.streakEnabled,
                downloadedOffline = settings.downloadedOffline
            )
        )
    }

    suspend fun createProfile(
        name: String,
        ageBand: AgeBand,
        avatar: String,
        dailyGoal: Int,
        teacherVoice: TeacherVoice
    ): Profile {
        val id = UUID.randomUUID().toString()
        val newProfile = Profile(
            id = id,
            name = name.trim(),
            ageBand = ageBand,
            avatar = avatar,
            dailyGoal = dailyGoal,
            teacherVoice = teacherVoice,
            completedLessons = emptyList(),
            xp = 0,
            minutes = 0,
            reviews = 0,
            streakDays = 1,
            achievements = emptyList()
        )
        profileDao.insertOrUpdateProfile(newProfile.toEntity())
        ensureReviewUnitsInitialized(id)
        return newProfile
    }

    suspend fun updateProfile(profile: Profile) {
        profileDao.insertOrUpdateProfile(profile.toEntity())
    }

    suspend fun deleteProfile(profileId: String) {
        profileDao.deleteProfileById(profileId)
        masteryDao.clearMasteryForProfile(profileId)
        attemptDao.clearAttemptsForProfile(profileId)
        reviewItemDao.clearReviewItemsForProfile(profileId)
        objectiveMasteryDao.clearObjectiveMasteryForProfile(profileId)
        dailyPlanDao.clearPlansForProfile(profileId)
    }

    /**
     * Initialize/backfill Review Items from curriculum vocabulary for a profile
     */
    suspend fun ensureReviewUnitsInitialized(profileId: String) {
        val existing = reviewItemDao.getReviewItemsForProfileDirect(profileId)
        if (existing.size >= CurriculumData.vocabularyList.size) return

        val existingIds = existing.map { it.unitId }.toSet()
        val newItems = CurriculumData.vocabularyList
            .filter { !existingIds.contains(it.id) }
            .map { vocab ->
                val objectiveId = when (vocab.topic) {
                    "greetings" -> "obj_greetings"
                    "introductions" -> "obj_introductions"
                    "numbers" -> "obj_numbers"
                    "colors" -> "obj_colors"
                    "family" -> "obj_family"
                    else -> "obj_greetings"
                }

                ReviewItemEntity(
                    primaryKey = "${profileId}_${vocab.id}",
                    unitId = vocab.id,
                    profileId = profileId,
                    language = "fr",
                    unitType = UnitType.VOCABULARY.name,
                    title = vocab.french,
                    frenchText = vocab.french,
                    englishText = vocab.english,
                    topic = vocab.topic,
                    emoji = vocab.emoji,
                    objectiveId = objectiveId,
                    learningState = LearningState.NEW.name,
                    firstSeenEpochMs = 0L,
                    lastAttemptEpochMs = 0L,
                    lastSuccessEpochMs = 0L,
                    nextReviewEpochMs = 0L,
                    currentIntervalDays = 0f,
                    consecutiveSuccesses = 0,
                    lapseCount = 0,
                    totalReviewCount = 0,
                    recentGrade = null,
                    schedulerVersion = DeterministicReviewScheduler.SCHEDULER_VERSION,
                    isSuspended = false
                )
            }

        if (newItems.isNotEmpty()) {
            reviewItemDao.insertOrUpdateReviewItems(newItems)
        }

        // Also initialize Objective Mastery entries
        val existingMasteries = objectiveMasteryDao.getMasteryForProfileDirect(profileId)
        if (existingMasteries.isEmpty()) {
            val masteries = MasteryCalculator.DEFINED_OBJECTIVES.map { def ->
                ObjectiveMasteryEntity(
                    primaryKey = "${profileId}_${def.id}",
                    objectiveId = def.id,
                    profileId = profileId,
                    title = def.title,
                    description = def.description,
                    cefrLevel = def.cefrLevel,
                    status = ObjectiveStatus.NOT_STARTED.name,
                    masteryScore = 0f,
                    evidenceCount = 0,
                    lastDemonstratedEpochMs = 0L
                )
            }
            objectiveMasteryDao.insertOrUpdateObjectiveMasteries(masteries)
        }
    }

    /**
     * Submit an attempt, compute spaced review schedule, and update mastery state.
     * Idempotency key guarantees duplicate calls will not corrupt counts or intervals.
     */
    suspend fun recordAttemptAndReview(
        profileId: String,
        unitId: String,
        lessonId: String? = null,
        exerciseId: String? = null,
        activityType: ExerciseKind,
        submittedAnswer: String,
        isCorrect: Boolean,
        hintUsed: Boolean,
        responseDurationMs: Long,
        idempotencyKey: String = UUID.randomUUID().toString()
    ): ReviewableUnit? {
        ensureReviewUnitsInitialized(profileId)

        // 1. Idempotency Check
        val existingAttempt = attemptDao.getAttemptByIdempotencyKey(idempotencyKey)
        if (existingAttempt != null) {
            val existingUnit = reviewItemDao.getReviewItem(profileId, unitId)
            return existingUnit?.toDomain()
        }

        val now = System.currentTimeMillis()

        // 2. Derive Review Grade
        val grade = if (!isCorrect) {
            ReviewGrade.AGAIN
        } else if (hintUsed) {
            ReviewGrade.HARD
        } else if (responseDurationMs < 2500L && responseDurationMs > 0L) {
            ReviewGrade.EASY
        } else {
            ReviewGrade.GOOD
        }

        // 3. Record Immutable Attempt
        val attemptEntity = LearningAttemptEntity(
            id = UUID.randomUUID().toString(),
            profileId = profileId,
            targetLanguage = "fr",
            lessonId = lessonId,
            exerciseId = exerciseId,
            unitId = unitId,
            objectiveId = when {
                unitId.startsWith("bonjour") || unitId in listOf("bonjour", "salut", "bonsoir", "au-revoir", "merci", "svp", "oui", "non") -> "obj_greetings"
                unitId.startsWith("intro") || unitId in listOf("je-mappelle", "comment-tu", "enchante", "je-suis") -> "obj_introductions"
                unitId.startsWith("num") || unitId in listOf("un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix", "age") -> "obj_numbers"
                unitId.startsWith("col") || unitId in listOf("rouge", "bleu", "vert", "jaune", "orange", "rose", "noir", "blanc", "marron", "violet") -> "obj_colors"
                else -> "obj_family"
            },
            activityType = activityType.name,
            eventType = EventType.ANSWERED.name,
            submittedAnswer = submittedAnswer,
            isCorrect = isCorrect,
            score = if (isCorrect) 1.0f else 0.0f,
            hintUsed = hintUsed,
            retryNumber = 0,
            responseDurationMs = responseDurationMs,
            grade = grade.name,
            idempotencyKey = idempotencyKey,
            timestampEpochMs = now,
            contentVersion = 1
        )
        attemptDao.insertAttempt(attemptEntity)

        // 4. Update Spaced Review Unit State
        val currentEntity = reviewItemDao.getReviewItem(profileId, unitId) ?: return null
        val currentUnit = currentEntity.toDomain()

        val scheduleResult = scheduler.calculateNextReview(
            unit = currentUnit,
            grade = grade,
            hintUsed = hintUsed,
            nowEpochMs = now
        )

        val updatedUnitEntity = currentEntity.copy(
            learningState = scheduleResult.newState.name,
            firstSeenEpochMs = if (currentEntity.firstSeenEpochMs == 0L) now else currentEntity.firstSeenEpochMs,
            lastAttemptEpochMs = now,
            lastSuccessEpochMs = if (isCorrect) now else currentEntity.lastSuccessEpochMs,
            nextReviewEpochMs = scheduleResult.nextReviewEpochMs,
            currentIntervalDays = scheduleResult.intervalDays,
            consecutiveSuccesses = scheduleResult.consecutiveSuccesses,
            lapseCount = scheduleResult.lapseCount,
            totalReviewCount = currentEntity.totalReviewCount + 1,
            recentGrade = scheduleResult.effectiveGrade.name,
            schedulerVersion = scheduler.version
        )
        reviewItemDao.insertOrUpdateReviewItem(updatedUnitEntity)

        // 5. Recompute Objective Mastery
        recalculateObjectiveMastery(profileId)

        // 6. Update Profile XP & reviews
        val profileEntity = profileDao.getProfileById(profileId)
        if (profileEntity != null) {
            val achievements = profileEntity.achievements.toMutableSet()
            achievements.add("review_gardener")
            val updatedProfile = profileEntity.copy(
                reviews = profileEntity.reviews + 1,
                xp = profileEntity.xp + if (isCorrect) 15 else 5,
                achievements = achievements.toList()
            )
            profileDao.insertOrUpdateProfile(updatedProfile)
        }

        return updatedUnitEntity.toDomain()
    }

    /**
     * Recalculate and persist all Objective Masteries for a profile
     */
    suspend fun recalculateObjectiveMastery(profileId: String) {
        val units = reviewItemDao.getReviewItemsForProfileDirect(profileId).map { it.toDomain() }
        val attempts = attemptDao.getAttemptsForProfileDirect(profileId).map { it.toDomain() }

        val updatedMasteries = MasteryCalculator.DEFINED_OBJECTIVES.map { def ->
            val objectiveUnits = units.filter { it.objectiveId == def.id }
            val computed = MasteryCalculator.computeObjectiveMastery(
                profileId = profileId,
                objectiveDef = def,
                unitsForObjective = objectiveUnits,
                attempts = attempts
            )
            ObjectiveMasteryEntity(
                primaryKey = "${profileId}_${def.id}",
                objectiveId = def.id,
                profileId = profileId,
                title = def.title,
                description = def.description,
                cefrLevel = def.cefrLevel,
                status = computed.status.name,
                masteryScore = computed.masteryScore,
                evidenceCount = computed.evidenceCount,
                lastDemonstratedEpochMs = computed.lastDemonstratedEpochMs
            )
        }
        objectiveMasteryDao.insertOrUpdateObjectiveMasteries(updatedMasteries)
    }

    /**
     * Get or generate today's learning plan for profile
     */
    suspend fun getOrGenerateDailyPlan(profileId: String, dateString: String = DailyLearningPlanner.getTodayDateString()): DailyLearningPlan {
        ensureReviewUnitsInitialized(profileId)
        val cached = dailyPlanDao.getPlanForDate(profileId, dateString)
        if (cached != null) {
            return cached.toDomain()
        }

        val profile = profileDao.getProfileById(profileId)?.toDomain()
            ?: Profile(id = profileId, name = "Learner")
        val reviewUnits = reviewItemDao.getReviewItemsForProfileDirect(profileId).map { it.toDomain() }

        val newPlan = DailyLearningPlanner.generateDailyPlan(
            profile = profile,
            reviewUnits = reviewUnits,
            dateString = dateString
        )

        dailyPlanDao.insertOrUpdatePlan(newPlan.toEntity())
        return newPlan
    }

    suspend fun markDailyPlanCompleted(planId: String, profileId: String, dateString: String) {
        val plan = dailyPlanDao.getPlanForDate(profileId, dateString) ?: return
        val updated = plan.copy(isCompleted = true)
        dailyPlanDao.insertOrUpdatePlan(updated)

        // Increment streak & XP
        val profile = profileDao.getProfileById(profileId) ?: return
        val achievements = profile.achievements.toMutableSet()
        achievements.add("daily_champion")
        profileDao.insertOrUpdateProfile(
            profile.copy(
                xp = profile.xp + 50,
                streakDays = profile.streakDays + 1,
                achievements = achievements.toList()
            )
        )
    }

    suspend fun setReviewItemSuspended(profileId: String, unitId: String, isSuspended: Boolean) {
        reviewItemDao.setSuspended(profileId, unitId, isSuspended)
    }

    suspend fun resetReviewItem(profileId: String, unitId: String) {
        val item = reviewItemDao.getReviewItem(profileId, unitId) ?: return
        val reset = item.copy(
            learningState = LearningState.LEARNING.name,
            currentIntervalDays = 1.0f,
            consecutiveSuccesses = 0,
            nextReviewEpochMs = System.currentTimeMillis() // Due immediately
        )
        reviewItemDao.insertOrUpdateReviewItem(reset)
        recalculateObjectiveMastery(profileId)
    }

    suspend fun completeLesson(profileId: String, lessonId: String, minutes: Int) {
        val currentEntity = profileDao.getProfileById(profileId) ?: return
        val current = currentEntity.toDomain()
        val alreadyCompleted = current.completedLessons.contains(lessonId)
        val newCompleted = if (alreadyCompleted) current.completedLessons else current.completedLessons + lessonId
        val newXp = if (alreadyCompleted) current.xp else current.xp + 40
        val newMinutes = if (alreadyCompleted) current.minutes else current.minutes + minutes

        val achievements = current.achievements.toMutableSet()
        if (newCompleted.isNotEmpty()) achievements.add("first_seed")
        if (newCompleted.size >= 3) achievements.add("careful_listener")
        if (current.speakingAttempts > 0) achievements.add("brave_voice")
        if (newCompleted.size >= 5) achievements.add("starter_bloom")

        val updated = current.copy(
            completedLessons = newCompleted,
            xp = newXp,
            minutes = newMinutes,
            currentLessonId = null,
            currentActivityIndex = 0,
            achievements = achievements.toList()
        )
        profileDao.insertOrUpdateProfile(updated.toEntity())
    }

    suspend fun recordSpeakingAttempt(profileId: String) {
        val current = profileDao.getProfileById(profileId)?.toDomain() ?: return
        val achievements = current.achievements.toMutableSet()
        achievements.add("brave_voice")
        val updated = current.copy(
            speakingAttempts = current.speakingAttempts + 1,
            xp = current.xp + 10,
            achievements = achievements.toList()
        )
        profileDao.insertOrUpdateProfile(updated.toEntity())
    }

    suspend fun saveLessonProgress(profileId: String, lessonId: String, activityIndex: Int) {
        val current = profileDao.getProfileById(profileId)?.toDomain() ?: return
        val updated = current.copy(
            currentLessonId = lessonId,
            currentActivityIndex = activityIndex
        )
        profileDao.insertOrUpdateProfile(updated.toEntity())
    }

    suspend fun recordReview(profileId: String, itemId: String, isCorrect: Boolean) {
        recordAttemptAndReview(
            profileId = profileId,
            unitId = itemId,
            activityType = ExerciseKind.REVIEW,
            submittedAnswer = if (isCorrect) "Correct" else "Incorrect",
            isCorrect = isCorrect,
            hintUsed = false,
            responseDurationMs = 3000L
        )
    }

    // Mapping helpers
    private fun ProfileEntity.toDomain(): Profile = Profile(
        id = id,
        name = name,
        ageBand = ageBand,
        avatar = avatar,
        dailyGoal = dailyGoal,
        completedLessons = completedLessons,
        xp = xp,
        minutes = minutes,
        reviews = reviews,
        teacherVoice = teacherVoice,
        currentLessonId = currentLessonId,
        currentActivityIndex = currentActivityIndex,
        speakingAttempts = speakingAttempts,
        streakDays = streakDays,
        achievements = achievements
    )

    private fun Profile.toEntity(): ProfileEntity = ProfileEntity(
        id = id,
        name = name,
        ageBand = ageBand,
        avatar = avatar,
        dailyGoal = dailyGoal,
        completedLessons = completedLessons,
        xp = xp,
        minutes = minutes,
        reviews = reviews,
        teacherVoice = teacherVoice,
        currentLessonId = currentLessonId,
        currentActivityIndex = currentActivityIndex,
        speakingAttempts = speakingAttempts,
        streakDays = streakDays,
        achievements = achievements
    )

    private fun FamilySettingsEntity.toDomain(): FamilySettings = FamilySettings(
        guardianReady = guardianReady,
        parentPin = parentPin,
        activeProfileId = activeProfileId,
        soundEnabled = soundEnabled,
        streakEnabled = streakEnabled,
        downloadedOffline = downloadedOffline
    )

    private fun MasteryEntity.toDomain(): MasteryState = MasteryState(
        itemId = itemId,
        stability = stability,
        difficulty = difficulty,
        dueAtEpochMs = dueAtEpochMs,
        lastReviewedEpochMs = lastReviewedEpochMs,
        repetitions = repetitions,
        lapses = lapses
    )

    private fun ReviewItemEntity.toDomain(): ReviewableUnit = ReviewableUnit(
        unitId = unitId,
        profileId = profileId,
        language = language,
        unitType = try { UnitType.valueOf(unitType) } catch (e: Exception) { UnitType.VOCABULARY },
        title = title,
        frenchText = frenchText,
        englishText = englishText,
        topic = topic,
        emoji = emoji,
        objectiveId = objectiveId,
        learningState = try { LearningState.valueOf(learningState) } catch (e: Exception) { LearningState.NEW },
        firstSeenEpochMs = firstSeenEpochMs,
        lastAttemptEpochMs = lastAttemptEpochMs,
        lastSuccessEpochMs = lastSuccessEpochMs,
        nextReviewEpochMs = nextReviewEpochMs,
        currentIntervalDays = currentIntervalDays,
        consecutiveSuccesses = consecutiveSuccesses,
        lapseCount = lapseCount,
        totalReviewCount = totalReviewCount,
        recentGrade = recentGrade?.let { try { ReviewGrade.valueOf(it) } catch (e: Exception) { null } },
        schedulerVersion = schedulerVersion,
        isSuspended = isSuspended
    )

    private fun LearningAttemptEntity.toDomain(): LearningAttempt = LearningAttempt(
        id = id,
        profileId = profileId,
        targetLanguage = targetLanguage,
        lessonId = lessonId,
        exerciseId = exerciseId,
        unitId = unitId,
        objectiveId = objectiveId,
        activityType = try { ExerciseKind.valueOf(activityType) } catch (e: Exception) { ExerciseKind.REVIEW },
        eventType = try { EventType.valueOf(eventType) } catch (e: Exception) { EventType.ANSWERED },
        submittedAnswer = submittedAnswer,
        isCorrect = isCorrect,
        score = score,
        hintUsed = hintUsed,
        retryNumber = retryNumber,
        responseDurationMs = responseDurationMs,
        grade = try { ReviewGrade.valueOf(grade) } catch (e: Exception) { ReviewGrade.GOOD },
        idempotencyKey = idempotencyKey,
        timestampEpochMs = timestampEpochMs,
        contentVersion = contentVersion
    )

    private fun ObjectiveMasteryEntity.toDomain(): ObjectiveMastery = ObjectiveMastery(
        objectiveId = objectiveId,
        profileId = profileId,
        title = title,
        description = description,
        cefrLevel = cefrLevel,
        status = try { ObjectiveStatus.valueOf(status) } catch (e: Exception) { ObjectiveStatus.NOT_STARTED },
        masteryScore = masteryScore,
        evidenceCount = evidenceCount,
        lastDemonstratedEpochMs = lastDemonstratedEpochMs
    )

    private fun DailyPlanEntity.toDomain(): DailyLearningPlan = DailyLearningPlan(
        id = id,
        profileId = profileId,
        dateString = dateString,
        targetMinutes = targetMinutes,
        estimatedMinutes = estimatedMinutes,
        steps = steps,
        isCompleted = isCompleted,
        createdAtEpochMs = createdAtEpochMs
    )

    private fun DailyLearningPlan.toEntity(): DailyPlanEntity = DailyPlanEntity(
        id = id,
        profileId = profileId,
        dateString = dateString,
        targetMinutes = targetMinutes,
        estimatedMinutes = estimatedMinutes,
        steps = steps,
        isCompleted = isCompleted,
        createdAtEpochMs = createdAtEpochMs
    )
}
