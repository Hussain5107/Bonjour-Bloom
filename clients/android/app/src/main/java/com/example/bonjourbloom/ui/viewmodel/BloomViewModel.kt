package com.example.bonjourbloom.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bonjourbloom.audio.AudioRecorderService
import com.example.bonjourbloom.audio.FrenchTtsService
import com.example.bonjourbloom.data.curriculum.CurriculumData
import com.example.bonjourbloom.data.engine.DailyLearningPlanner
import com.example.bonjourbloom.data.local.AppDatabase
import com.example.bonjourbloom.data.model.AgeBand
import com.example.bonjourbloom.data.model.DailyLearningPlan
import com.example.bonjourbloom.data.model.ExerciseKind
import com.example.bonjourbloom.data.model.FamilySettings
import com.example.bonjourbloom.data.model.LearningAttempt
import com.example.bonjourbloom.data.model.LearningState
import com.example.bonjourbloom.data.model.Lesson
import com.example.bonjourbloom.data.model.ObjectiveMastery
import com.example.bonjourbloom.data.model.PlanStepType
import com.example.bonjourbloom.data.model.Profile
import com.example.bonjourbloom.data.model.ReviewableUnit
import com.example.bonjourbloom.data.model.TeacherVoice
import com.example.bonjourbloom.data.repository.BonjourBloomRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

enum class AppNavDestination {
    HOME,
    LEARN,
    VOICE_CHAT,
    TRANSCRIBER,
    WORDS,
    REWARDS,
    OFFLINE,
    PARENT,
    LESSON,
    SPEAK,
    PROFILE_SETUP,
    WELCOME,
    DAILY_SESSION,
    REVIEW_CENTER
}

data class SessionExerciseItem(
    val id: String,
    val unitId: String,
    val stepType: PlanStepType,
    val kind: ExerciseKind,
    val prompt: String,
    val french: String,
    val english: String,
    val audioText: String,
    val answer: String,
    val options: List<String>,
    val emoji: String = "✦",
    val explanation: String = "",
    val hint: String = ""
)

data class DailySessionUiState(
    val isActive: Boolean = false,
    val title: String = "Today's Learning Session",
    val totalItems: Int = 0,
    val currentItemIndex: Int = 0,
    val items: List<SessionExerciseItem> = emptyList(),
    val selectedAnswer: String = "",
    val feedbackState: String? = null, // "correct", "incorrect", "try"
    val hintShown: Boolean = false,
    val isCompleted: Boolean = false,
    val correctCount: Int = 0,
    val reviewedWords: List<String> = emptyList(),
    val petalsEarned: Int = 0,
    val startTimeEpochMs: Long = 0L
)

data class BloomUiState(
    val isReady: Boolean = false,
    val currentDestination: AppNavDestination = AppNavDestination.HOME,
    val familySettings: FamilySettings = FamilySettings(),
    val profiles: List<Profile> = emptyList(),
    val activeProfile: Profile? = null,
    val activeLesson: Lesson? = null,
    val currentExerciseIndex: Int = 0,
    val selectedAnswer: String = "",
    val feedbackState: String? = null, // null, "correct", "try"
    val wordSearchQuery: String = "",
    val wordSelectedTopic: String = "all",
    val isOnline: Boolean = true,
    val parentUnlocked: Boolean = false,
    val showProfileSwitcherDialog: Boolean = false,
    val speakExpectedText: String = "",
    val dailyPlan: DailyLearningPlan? = null,
    val reviewUnits: List<ReviewableUnit> = emptyList(),
    val dueReviewsCount: Int = 0,
    val objectiveMasteries: List<ObjectiveMastery> = emptyList(),
    val sessionState: DailySessionUiState = DailySessionUiState()
)

@OptIn(ExperimentalCoroutinesApi::class)
class BloomViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BonjourBloomRepository(AppDatabase.getInstance(application))
    val ttsService = FrenchTtsService(application)
    val recorderService = AudioRecorderService(application)

    private val _currentDestination = MutableStateFlow(AppNavDestination.HOME)
    private val _activeLessonId = MutableStateFlow<String?>(null)
    private val _currentExerciseIndex = MutableStateFlow(0)
    private val _selectedAnswer = MutableStateFlow("")
    private val _feedbackState = MutableStateFlow<String?>(null)
    private val _wordSearchQuery = MutableStateFlow("")
    private val _wordSelectedTopic = MutableStateFlow("all")
    private val _isOnline = MutableStateFlow(true)
    private val _parentUnlocked = MutableStateFlow(false)
    private val _showProfileSwitcher = MutableStateFlow(false)
    private val _speakExpectedText = MutableStateFlow("")
    private val _dailySessionState = MutableStateFlow(DailySessionUiState())

    // Active profile ID flow for switching
    private val activeProfileIdFlow = repository.familySettingsFlow.map { it.activeProfileId }

    private val reviewUnitsFlow = activeProfileIdFlow.flatMapLatest { profileId ->
        if (profileId != null) repository.getReviewItemsFlow(profileId) else flowOf(emptyList())
    }

    private val objectiveMasteriesFlow = activeProfileIdFlow.flatMapLatest { profileId ->
        if (profileId != null) repository.getObjectiveMasteriesFlow(profileId) else flowOf(emptyList())
    }

    private val dailyPlanFlow = activeProfileIdFlow.flatMapLatest { profileId ->
        if (profileId != null) {
            val today = DailyLearningPlanner.getTodayDateString()
            repository.getDailyPlanFlow(profileId, today)
        } else {
            flowOf(null)
        }
    }

    val uiState: StateFlow<BloomUiState> = combine(
        repository.familySettingsFlow,
        repository.profilesFlow,
        _currentDestination,
        _activeLessonId,
        _currentExerciseIndex,
        _selectedAnswer,
        _feedbackState,
        _wordSearchQuery,
        _wordSelectedTopic,
        _isOnline,
        _parentUnlocked,
        _showProfileSwitcher,
        _speakExpectedText,
        dailyPlanFlow,
        reviewUnitsFlow,
        objectiveMasteriesFlow,
        _dailySessionState
    ) { args: Array<Any?> ->
        val settings = args[0] as FamilySettings
        @Suppress("UNCHECKED_CAST")
        val profiles = args[1] as List<Profile>
        val dest = args[2] as AppNavDestination
        val lessonId = args[3] as? String
        val exIndex = args[4] as Int
        val answer = args[5] as String
        val feedback = args[6] as? String
        val query = args[7] as String
        val topic = args[8] as String
        val online = args[9] as Boolean
        val parentUnlocked = args[10] as Boolean
        val showProfiles = args[11] as Boolean
        val speakText = args[12] as String
        val plan = args[13] as? DailyLearningPlan
        @Suppress("UNCHECKED_CAST")
        val units = (args[14] as? List<ReviewableUnit>) ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val masteries = (args[15] as? List<ObjectiveMastery>) ?: emptyList()
        val session = args[16] as DailySessionUiState

        val active = profiles.find { it.id == settings.activeProfileId } ?: profiles.firstOrNull()
        val lesson = CurriculumData.lessonsList.find { it.id == lessonId }

        // Sync TTS voice with profile
        active?.teacherVoice?.let { ttsService.setTeacherVoice(it) }

        val now = System.currentTimeMillis()
        val dueCount = units.count { !it.isSuspended && (it.nextReviewEpochMs <= now || it.learningState == LearningState.RELEARNING) }

        BloomUiState(
            isReady = true,
            currentDestination = if (!settings.guardianReady) {
                AppNavDestination.WELCOME
            } else if (profiles.isEmpty() && dest != AppNavDestination.PROFILE_SETUP) {
                AppNavDestination.PROFILE_SETUP
            } else {
                dest
            },
            familySettings = settings,
            profiles = profiles,
            activeProfile = active,
            activeLesson = lesson,
            currentExerciseIndex = exIndex,
            selectedAnswer = answer,
            feedbackState = feedback,
            wordSearchQuery = query,
            wordSelectedTopic = topic,
            isOnline = online,
            parentUnlocked = parentUnlocked,
            showProfileSwitcherDialog = showProfiles,
            speakExpectedText = speakText,
            dailyPlan = plan,
            reviewUnits = units,
            dueReviewsCount = dueCount,
            objectiveMasteries = masteries,
            sessionState = session
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BloomUiState()
    )

    init {
        // Auto initialize / refresh plan on launch
        viewModelScope.launch {
            uiState.collect { state ->
                val active = state.activeProfile
                if (active != null && state.dailyPlan == null) {
                    repository.getOrGenerateDailyPlan(active.id)
                }
            }
        }
    }

    fun navigateTo(destination: AppNavDestination) {
        _currentDestination.value = destination
    }

    fun setShowProfileSwitcher(show: Boolean) {
        _showProfileSwitcher.value = show
    }

    fun completeGuardianSetup(pin: String) {
        viewModelScope.launch {
            val current = uiState.value.familySettings
            repository.saveFamilySettings(
                current.copy(
                    guardianReady = true,
                    parentPin = pin
                )
            )
            _currentDestination.value = AppNavDestination.PROFILE_SETUP
        }
    }

    fun createProfile(
        name: String,
        ageBand: AgeBand,
        avatar: String,
        dailyGoal: Int,
        teacherVoice: TeacherVoice
    ) {
        viewModelScope.launch {
            val newProfile = repository.createProfile(name, ageBand, avatar, dailyGoal, teacherVoice)
            val currentSettings = uiState.value.familySettings
            repository.saveFamilySettings(currentSettings.copy(activeProfileId = newProfile.id))
            repository.getOrGenerateDailyPlan(newProfile.id)
            _currentDestination.value = AppNavDestination.HOME
        }
    }

    fun selectActiveProfile(profileId: String) {
        viewModelScope.launch {
            val current = uiState.value.familySettings
            repository.saveFamilySettings(current.copy(activeProfileId = profileId))
            repository.ensureReviewUnitsInitialized(profileId)
            repository.getOrGenerateDailyPlan(profileId)
            _showProfileSwitcher.value = false
        }
    }

    fun deleteProfile(profileId: String) {
        viewModelScope.launch {
            repository.deleteProfile(profileId)
            val remaining = uiState.value.profiles.filter { it.id != profileId }
            val nextActiveId = remaining.firstOrNull()?.id
            repository.saveFamilySettings(uiState.value.familySettings.copy(activeProfileId = nextActiveId))
        }
    }

    // ==========================================
    // SECTION 3: DAILY PLAN & SESSION ENGINE
    // ==========================================

    fun startDailyLearningSession() {
        val active = uiState.value.activeProfile ?: return
        viewModelScope.launch {
            val plan = repository.getOrGenerateDailyPlan(active.id)
            val sessionItems = buildSessionExerciseItems(plan, active)

            _dailySessionState.value = DailySessionUiState(
                isActive = true,
                title = "Today's Learning Session (${plan.targetMinutes} min)",
                totalItems = sessionItems.size,
                currentItemIndex = 0,
                items = sessionItems,
                selectedAnswer = "",
                feedbackState = null,
                hintShown = false,
                isCompleted = sessionItems.isEmpty(),
                correctCount = 0,
                reviewedWords = emptyList(),
                petalsEarned = 0,
                startTimeEpochMs = System.currentTimeMillis()
            )
            _currentDestination.value = AppNavDestination.DAILY_SESSION
        }
    }

    fun startReviewSession(unitIds: List<String>? = null) {
        val active = uiState.value.activeProfile ?: return
        val allUnits = uiState.value.reviewUnits
        val selectedUnits = if (unitIds != null) {
            allUnits.filter { unitIds.contains(it.unitId) }
        } else {
            val now = System.currentTimeMillis()
            allUnits.filter { !it.isSuspended && (it.nextReviewEpochMs <= now || it.learningState == LearningState.RELEARNING) }
        }.ifEmpty {
            // If none due, practice familiar/introduced
            allUnits.take(5)
        }

        val sessionItems = selectedUnits.map { unit ->
            val vocab = CurriculumData.vocabularyList.find { it.id == unit.unitId }
            val options = generateReviewOptions(unit.frenchText, unit.englishText)
            SessionExerciseItem(
                id = "rev_${unit.unitId}_${System.currentTimeMillis()}",
                unitId = unit.unitId,
                stepType = PlanStepType.DUE_REVIEW,
                kind = ExerciseKind.REVIEW,
                prompt = "What is the English meaning of '${unit.frenchText}'?",
                french = unit.frenchText,
                english = unit.englishText,
                audioText = unit.frenchText,
                answer = unit.englishText,
                options = options,
                emoji = unit.emoji,
                explanation = "In French: '${unit.frenchText}' means '${unit.englishText}'.",
                hint = vocab?.hint ?: "Listen closely to the sounds"
            )
        }

        _dailySessionState.value = DailySessionUiState(
            isActive = true,
            title = "Spaced Memory Review (${sessionItems.size} items)",
            totalItems = sessionItems.size,
            currentItemIndex = 0,
            items = sessionItems,
            selectedAnswer = "",
            feedbackState = null,
            hintShown = false,
            isCompleted = sessionItems.isEmpty(),
            correctCount = 0,
            reviewedWords = emptyList(),
            petalsEarned = 0,
            startTimeEpochMs = System.currentTimeMillis()
        )
        _currentDestination.value = AppNavDestination.DAILY_SESSION
    }

    private fun generateReviewOptions(french: String, correctEnglish: String): List<String> {
        val distractorList = CurriculumData.vocabularyList
            .filter { it.english != correctEnglish }
            .shuffled()
            .take(2)
            .map { it.english }
        return (distractorList + correctEnglish).shuffled()
    }

    private fun buildSessionExerciseItems(plan: DailyLearningPlan, profile: Profile): List<SessionExerciseItem> {
        val items = mutableListOf<SessionExerciseItem>()

        for (step in plan.steps) {
            when (step.stepType) {
                PlanStepType.DUE_REVIEW -> {
                    for (unitId in step.unitIds) {
                        val vocab = CurriculumData.vocabularyList.find { it.id == unitId } ?: continue
                        items.add(
                            SessionExerciseItem(
                                id = "step_rev_${unitId}",
                                unitId = unitId,
                                stepType = PlanStepType.DUE_REVIEW,
                                kind = ExerciseKind.REVIEW,
                                prompt = "What does '${vocab.french}' mean?",
                                french = vocab.french,
                                english = vocab.english,
                                audioText = vocab.french,
                                answer = vocab.english,
                                options = generateReviewOptions(vocab.french, vocab.english),
                                emoji = vocab.emoji,
                                explanation = "'${vocab.french}' translates to '${vocab.english}'.",
                                hint = vocab.hint
                            )
                        )
                    }
                }
                PlanStepType.RESUME_LESSON, PlanStepType.NEXT_LESSON -> {
                    val lessonId = step.lessonId
                    val lesson = CurriculumData.lessonsList.find { it.id == lessonId }
                    if (lesson != null) {
                        for (ex in lesson.exercises.take(4)) {
                            items.add(
                                SessionExerciseItem(
                                    id = "step_ex_${ex.id}",
                                    unitId = ex.id,
                                    stepType = step.stepType,
                                    kind = ex.kind,
                                    prompt = ex.prompt,
                                    french = ex.audioText ?: ex.prompt,
                                    english = ex.answer,
                                    audioText = ex.audioText ?: ex.prompt,
                                    answer = ex.answer,
                                    options = if (ex.options.isNotEmpty()) ex.options else listOf(ex.answer),
                                    emoji = ex.emoji,
                                    explanation = ex.explanation ?: "Well done!",
                                    hint = "Tap listen to hear pronunciation"
                                )
                            )
                        }
                    }
                }
                PlanStepType.NEW_VOCAB -> {
                    for (unitId in step.unitIds) {
                        val vocab = CurriculumData.vocabularyList.find { it.id == unitId } ?: continue
                        items.add(
                            SessionExerciseItem(
                                id = "step_new_${unitId}",
                                unitId = unitId,
                                stepType = PlanStepType.NEW_VOCAB,
                                kind = ExerciseKind.LISTEN,
                                prompt = "Listen and match: ${vocab.french}",
                                french = vocab.french,
                                english = vocab.english,
                                audioText = vocab.french,
                                answer = vocab.english,
                                options = generateReviewOptions(vocab.french, vocab.english),
                                emoji = vocab.emoji,
                                explanation = "'${vocab.french}' means '${vocab.english}'. Example: ${vocab.example}",
                                hint = vocab.hint
                            )
                        )
                    }
                }
                PlanStepType.SESSION_RECAP -> {
                    // Handled in completion screen
                }
            }
        }
        return items
    }

    fun submitDailySessionAnswer(selected: String) {
        val session = _dailySessionState.value
        val currentItem = session.items.getOrNull(session.currentItemIndex) ?: return
        val active = uiState.value.activeProfile ?: return

        _dailySessionState.value = session.copy(selectedAnswer = selected)

        val isCorrect = selected.trim().equals(currentItem.answer.trim(), ignoreCase = true)
        val feedback = if (isCorrect) "correct" else "try"
        _dailySessionState.value = _dailySessionState.value.copy(feedbackState = feedback)

        // Record immutable attempt & update spaced repetition review
        viewModelScope.launch {
            val responseDuration = System.currentTimeMillis() - session.startTimeEpochMs
            val idempotencyKey = "session_${active.id}_${currentItem.id}_${System.currentTimeMillis() / 1000}"

            repository.recordAttemptAndReview(
                profileId = active.id,
                unitId = currentItem.unitId,
                lessonId = if (currentItem.stepType == PlanStepType.NEXT_LESSON) "curriculum" else null,
                exerciseId = currentItem.id,
                activityType = currentItem.kind,
                submittedAnswer = selected,
                isCorrect = isCorrect,
                hintUsed = session.hintShown,
                responseDurationMs = responseDuration,
                idempotencyKey = idempotencyKey
            )
        }
    }

    fun showDailySessionHint() {
        val session = _dailySessionState.value
        val currentItem = session.items.getOrNull(session.currentItemIndex) ?: return
        _dailySessionState.value = session.copy(hintShown = true)
        speakFrench(currentItem.audioText, isSlow = true)
    }

    fun nextDailySessionItem() {
        val session = _dailySessionState.value
        val nextIndex = session.currentItemIndex + 1
        val isCorrect = session.feedbackState == "correct"
        val currentItem = session.items.getOrNull(session.currentItemIndex)

        val updatedReviewed = if (currentItem != null && !session.reviewedWords.contains(currentItem.french)) {
            session.reviewedWords + currentItem.french
        } else {
            session.reviewedWords
        }

        val updatedCorrect = if (isCorrect) session.correctCount + 1 else session.correctCount
        val updatedPetals = session.petalsEarned + if (isCorrect) 15 else 5

        if (nextIndex >= session.items.size) {
            // Session Completed
            val active = uiState.value.activeProfile
            if (active != null) {
                viewModelScope.launch {
                    val plan = uiState.value.dailyPlan
                    if (plan != null) {
                        repository.markDailyPlanCompleted(plan.id, active.id, plan.dateString)
                    }
                }
            }

            _dailySessionState.value = session.copy(
                isCompleted = true,
                currentItemIndex = nextIndex,
                correctCount = updatedCorrect,
                reviewedWords = updatedReviewed,
                petalsEarned = updatedPetals
            )
        } else {
            _dailySessionState.value = session.copy(
                currentItemIndex = nextIndex,
                selectedAnswer = "",
                feedbackState = null,
                hintShown = false,
                correctCount = updatedCorrect,
                reviewedWords = updatedReviewed,
                petalsEarned = updatedPetals
            )
        }
    }

    fun exitDailySession() {
        _dailySessionState.value = DailySessionUiState(isActive = false)
        _currentDestination.value = AppNavDestination.HOME
    }

    fun toggleReviewItemSuspended(unitId: String, isSuspended: Boolean) {
        val active = uiState.value.activeProfile ?: return
        viewModelScope.launch {
            repository.setReviewItemSuspended(active.id, unitId, isSuspended)
        }
    }

    fun resetReviewItem(unitId: String) {
        val active = uiState.value.activeProfile ?: return
        viewModelScope.launch {
            repository.resetReviewItem(active.id, unitId)
        }
    }

    // ==========================================
    // STANDARD LESSON FLOW
    // ==========================================

    fun startLesson(lessonId: String) {
        if (lessonId == "review") {
            startReviewSession()
            return
        }
        val active = uiState.value.activeProfile
        val savedIndex = if (active?.currentLessonId == lessonId) active.currentActivityIndex else 0
        _activeLessonId.value = lessonId
        _currentExerciseIndex.value = savedIndex
        _selectedAnswer.value = ""
        _feedbackState.value = null
        _currentDestination.value = AppNavDestination.LESSON

        if (active != null) {
            viewModelScope.launch {
                repository.saveLessonProgress(active.id, lessonId, savedIndex)
            }
        }
    }

    fun selectLessonOption(option: String) {
        _selectedAnswer.value = option
        val lesson = uiState.value.activeLesson ?: return
        val currentEx = lesson.exercises.getOrNull(_currentExerciseIndex.value) ?: return

        val isCorrect = option.trim().equals(currentEx.answer.trim(), ignoreCase = true)
        _feedbackState.value = if (isCorrect) "correct" else "try"

        val active = uiState.value.activeProfile
        if (active != null) {
            viewModelScope.launch {
                val key = "lesson_${active.id}_${currentEx.id}_${System.currentTimeMillis() / 1000}"
                repository.recordAttemptAndReview(
                    profileId = active.id,
                    unitId = currentEx.id,
                    lessonId = lesson.id,
                    exerciseId = currentEx.id,
                    activityType = currentEx.kind,
                    submittedAnswer = option,
                    isCorrect = isCorrect,
                    hintUsed = false,
                    responseDurationMs = 3000L,
                    idempotencyKey = key
                )
            }
        }
    }

    fun setOrderedSentence(sentence: String) {
        _selectedAnswer.value = sentence
        val lesson = uiState.value.activeLesson ?: return
        val currentEx = lesson.exercises.getOrNull(_currentExerciseIndex.value) ?: return

        val isCorrect = sentence.trim().equals(currentEx.answer.trim(), ignoreCase = true)
        _feedbackState.value = if (isCorrect) "correct" else null
    }

    fun nextExercise() {
        val lesson = uiState.value.activeLesson ?: return
        val currentIndex = _currentExerciseIndex.value
        val active = uiState.value.activeProfile

        if (currentIndex >= lesson.exercises.size - 1) {
            // Finish lesson
            if (active != null) {
                viewModelScope.launch {
                    repository.completeLesson(active.id, lesson.id, lesson.minutes)
                }
            }
            _currentDestination.value = AppNavDestination.HOME
        } else {
            val nextIndex = currentIndex + 1
            _currentExerciseIndex.value = nextIndex
            _selectedAnswer.value = ""
            _feedbackState.value = null
            if (active != null) {
                viewModelScope.launch {
                    repository.saveLessonProgress(active.id, lesson.id, nextIndex)
                }
            }
        }
    }

    fun openPronunciationStudio(expected: String) {
        _speakExpectedText.value = expected
        _currentDestination.value = AppNavDestination.SPEAK
    }

    fun finishPronunciationStudio() {
        val active = uiState.value.activeProfile
        if (active != null) {
            viewModelScope.launch {
                repository.recordSpeakingAttempt(active.id)
            }
        }
        _currentDestination.value = AppNavDestination.LESSON
    }

    fun speakFrench(text: String, isSlow: Boolean = false) {
        if (uiState.value.familySettings.soundEnabled) {
            ttsService.speak(text, isSlow)
        }
    }

    fun setWordSearch(query: String) {
        _wordSearchQuery.value = query
    }

    fun setWordTopic(topic: String) {
        _wordSelectedTopic.value = topic
    }

    fun unlockParentGate(pin: String): Boolean {
        val correctPin = uiState.value.familySettings.parentPin
        val isCorrect = pin == correctPin
        _parentUnlocked.value = isCorrect
        return isCorrect
    }

    fun lockParentGate() {
        _parentUnlocked.value = false
    }

    fun updateFamilySettings(settings: FamilySettings) {
        viewModelScope.launch {
            repository.saveFamilySettings(settings)
        }
    }

    fun updateProfileGoal(profileId: String, newGoal: Int) {
        viewModelScope.launch {
            val profile = uiState.value.profiles.find { it.id == profileId } ?: return@launch
            repository.updateProfile(profile.copy(dailyGoal = newGoal))
            repository.getOrGenerateDailyPlan(profileId)
        }
    }

    fun toggleOfflineDownload() {
        val current = uiState.value.familySettings
        updateFamilySettings(current.copy(downloadedOffline = !current.downloadedOffline))
    }

    fun exportProgressJson(): String {
        val data = mapOf(
            "familySettings" to uiState.value.familySettings,
            "profiles" to uiState.value.profiles,
            "reviewUnits" to uiState.value.reviewUnits,
            "objectiveMasteries" to uiState.value.objectiveMasteries
        )
        return try {
            val format = Json { prettyPrint = true }
            format.encodeToString(data)
        } catch (e: Exception) {
            "{}"
        }
    }

    fun addPetals(count: Int) {
        viewModelScope.launch {
            val profile = uiState.value.activeProfile ?: return@launch
            val updated = profile.copy(xp = profile.xp + count)
            repository.updateProfile(updated)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsService.shutdown()
        recorderService.reset()
    }
}
