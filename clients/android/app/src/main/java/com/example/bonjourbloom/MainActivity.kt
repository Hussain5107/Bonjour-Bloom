package com.example.bonjourbloom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.bonjourbloom.ui.components.AppBottomNav
import com.example.bonjourbloom.ui.components.AppTopBar
import com.example.bonjourbloom.ui.components.ProfileSwitcherDialog
import com.example.bonjourbloom.ui.screens.DailySessionScreen
import com.example.bonjourbloom.ui.screens.HomeScreen
import com.example.bonjourbloom.ui.screens.LearnScreen
import com.example.bonjourbloom.ui.screens.LessonScreen
import com.example.bonjourbloom.ui.screens.LiveTranscriberScreen
import com.example.bonjourbloom.ui.screens.OfflineScreen
import com.example.bonjourbloom.ui.screens.ParentScreen
import com.example.bonjourbloom.ui.screens.ProfileSetupScreen
import com.example.bonjourbloom.ui.screens.PronunciationScreen
import com.example.bonjourbloom.ui.screens.ReviewCenterScreen
import com.example.bonjourbloom.ui.screens.RewardsScreen
import com.example.bonjourbloom.ui.screens.VoiceConversationScreen
import com.example.bonjourbloom.ui.screens.WelcomeScreen
import com.example.bonjourbloom.ui.screens.WordsScreen
import com.example.bonjourbloom.ui.theme.BonjourBloomTheme
import com.example.bonjourbloom.ui.viewmodel.AppNavDestination
import com.example.bonjourbloom.ui.viewmodel.BloomViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BloomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BonjourBloomTheme {
                val uiState by viewModel.uiState.collectAsState()

                BonjourBloomMainScreen(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
        }
    }
}

@Composable
fun BonjourBloomMainScreen(
    viewModel: BloomViewModel,
    uiState: com.example.bonjourbloom.ui.viewmodel.BloomUiState
) {
    val currentDest = uiState.currentDestination
    val hideNavBars = currentDest == AppNavDestination.WELCOME ||
            currentDest == AppNavDestination.PROFILE_SETUP ||
            currentDest == AppNavDestination.LESSON ||
            currentDest == AppNavDestination.SPEAK ||
            currentDest == AppNavDestination.DAILY_SESSION

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!hideNavBars) {
                AppTopBar(
                    profile = uiState.activeProfile,
                    isOnline = uiState.isOnline,
                    onBrandClick = { viewModel.navigateTo(AppNavDestination.HOME) },
                    onStatusClick = { viewModel.navigateTo(AppNavDestination.OFFLINE) },
                    onProfileClick = { viewModel.setShowProfileSwitcher(true) }
                )
            }
        },
        bottomBar = {
            if (!hideNavBars) {
                AppBottomNav(
                    currentDestination = currentDest,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDest) {
                AppNavDestination.WELCOME -> {
                    WelcomeScreen(
                        onDone = { pin ->
                            viewModel.completeGuardianSetup(pin)
                        }
                    )
                }

                AppNavDestination.PROFILE_SETUP -> {
                    ProfileSetupScreen(
                        onSaveProfile = { name, ageBand, avatar, dailyGoal, teacherVoice ->
                            viewModel.createProfile(name, ageBand, avatar, dailyGoal, teacherVoice)
                        }
                    )
                }

                AppNavDestination.HOME -> {
                    val profile = uiState.activeProfile
                    if (profile != null) {
                        HomeScreen(
                            profile = profile,
                            dailyPlan = uiState.dailyPlan,
                            dueReviewsCount = uiState.dueReviewsCount,
                            objectiveMasteries = uiState.objectiveMasteries,
                            onStartDailyPlan = {
                                viewModel.startDailyLearningSession()
                            },
                            onStartReviewSession = {
                                viewModel.startReviewSession()
                            },
                            onOpenReviewCenter = {
                                viewModel.navigateTo(AppNavDestination.REVIEW_CENTER)
                            },
                            onStartLesson = { lessonId ->
                                viewModel.startLesson(lessonId)
                            },
                            onSeeAllLessons = {
                                viewModel.navigateTo(AppNavDestination.LEARN)
                            },
                            onOpenVoiceChat = {
                                viewModel.navigateTo(AppNavDestination.VOICE_CHAT)
                            },
                            onOpenTranscriber = {
                                viewModel.navigateTo(AppNavDestination.TRANSCRIBER)
                            }
                        )
                    }
                }

                AppNavDestination.DAILY_SESSION -> {
                    DailySessionScreen(
                        sessionState = uiState.sessionState,
                        onSelectAnswer = { answer ->
                            viewModel.submitDailySessionAnswer(answer)
                        },
                        onShowHint = {
                            viewModel.showDailySessionHint()
                        },
                        onNextItem = {
                            viewModel.nextDailySessionItem()
                        },
                        onExit = {
                            viewModel.exitDailySession()
                        },
                        onSpeakFrench = { text, isSlow ->
                            viewModel.speakFrench(text, isSlow)
                        }
                    )
                }

                AppNavDestination.REVIEW_CENTER -> {
                    ReviewCenterScreen(
                        reviewUnits = uiState.reviewUnits,
                        onStartReview = { unitIds ->
                            viewModel.startReviewSession(unitIds)
                        },
                        onToggleSuspend = { unitId, isSuspended ->
                            viewModel.toggleReviewItemSuspended(unitId, isSuspended)
                        },
                        onResetItem = { unitId ->
                            viewModel.resetReviewItem(unitId)
                        },
                        onSpeakFrench = { text, isSlow ->
                            viewModel.speakFrench(text, isSlow)
                        },
                        onBack = {
                            viewModel.navigateTo(AppNavDestination.HOME)
                        }
                    )
                }

                AppNavDestination.VOICE_CHAT -> {
                    VoiceConversationScreen(
                        recorder = viewModel.recorderService,
                        onSpeakFrench = { text, isSlow ->
                            viewModel.speakFrench(text, isSlow)
                        },
                        onAddPetals = { petals ->
                            viewModel.addPetals(petals)
                        },
                        onBack = {
                            viewModel.navigateTo(AppNavDestination.HOME)
                        }
                    )
                }

                AppNavDestination.TRANSCRIBER -> {
                    LiveTranscriberScreen(
                        recorder = viewModel.recorderService,
                        onSpeakFrench = { text, isSlow ->
                            viewModel.speakFrench(text, isSlow)
                        },
                        onBack = {
                            viewModel.navigateTo(AppNavDestination.HOME)
                        }
                    )
                }

                AppNavDestination.LEARN -> {
                    val profile = uiState.activeProfile
                    if (profile != null) {
                        LearnScreen(
                            profile = profile,
                            onStartLesson = { lessonId ->
                                viewModel.startLesson(lessonId)
                            }
                        )
                    }
                }

                AppNavDestination.WORDS -> {
                    WordsScreen(
                        searchQuery = uiState.wordSearchQuery,
                        selectedTopic = uiState.wordSelectedTopic,
                        onSearchChange = { viewModel.setWordSearch(it) },
                        onTopicChange = { viewModel.setWordTopic(it) },
                        onSpeakWord = { word, isSlow ->
                            viewModel.speakFrench(word, isSlow)
                        }
                    )
                }

                AppNavDestination.REWARDS -> {
                    val profile = uiState.activeProfile
                    if (profile != null) {
                        RewardsScreen(profile = profile)
                    }
                }

                AppNavDestination.OFFLINE -> {
                    OfflineScreen(
                        isDownloaded = uiState.familySettings.downloadedOffline,
                        isOnline = uiState.isOnline,
                        onToggleDownload = { viewModel.toggleOfflineDownload() }
                    )
                }

                AppNavDestination.PARENT -> {
                    ParentScreen(
                        settings = uiState.familySettings,
                        profiles = uiState.profiles,
                        isUnlocked = uiState.parentUnlocked,
                        onUnlock = { pin -> viewModel.unlockParentGate(pin) },
                        onLock = { viewModel.lockParentGate() },
                        onUpdateSettings = { viewModel.updateFamilySettings(it) },
                        onUpdateGoal = { pId, g -> viewModel.updateProfileGoal(pId, g) },
                        onDeleteProfile = { pId -> viewModel.deleteProfile(pId) },
                        onExportJson = { viewModel.exportProgressJson() },
                        onBackToLearning = { viewModel.navigateTo(AppNavDestination.HOME) }
                    )
                }

                AppNavDestination.LESSON -> {
                    val lesson = uiState.activeLesson
                    if (lesson != null) {
                        LessonScreen(
                            lesson = lesson,
                            currentIndex = uiState.currentExerciseIndex,
                            selectedAnswer = uiState.selectedAnswer,
                            feedbackState = uiState.feedbackState,
                            onSelectOption = { viewModel.selectLessonOption(it) },
                            onSetSentenceOrder = { viewModel.setOrderedSentence(it) },
                            onNextExercise = { viewModel.nextExercise() },
                            onOpenPronunciationStudio = { phrase ->
                                viewModel.openPronunciationStudio(phrase)
                            },
                            onSpeakFrench = { text, isSlow ->
                                viewModel.speakFrench(text, isSlow)
                            },
                            onExitLesson = {
                                viewModel.navigateTo(AppNavDestination.HOME)
                            }
                        )
                    }
                }

                AppNavDestination.SPEAK -> {
                    PronunciationScreen(
                        phrase = uiState.speakExpectedText,
                        recorder = viewModel.recorderService,
                        onSpeakFrench = { text, isSlow ->
                            viewModel.speakFrench(text, isSlow)
                        },
                        onFinish = {
                            viewModel.finishPronunciationStudio()
                        }
                    )
                }
            }

            // Profile Switcher Dialog modal
            if (uiState.showProfileSwitcherDialog) {
                ProfileSwitcherDialog(
                    profiles = uiState.profiles,
                    activeProfileId = uiState.familySettings.activeProfileId,
                    onSelectProfile = { pId ->
                        viewModel.selectActiveProfile(pId)
                    },
                    onAddLearner = {
                        viewModel.setShowProfileSwitcher(false)
                        viewModel.navigateTo(AppNavDestination.PROFILE_SETUP)
                    },
                    onOpenParent = {
                        viewModel.setShowProfileSwitcher(false)
                        viewModel.navigateTo(AppNavDestination.PARENT)
                    },
                    onDismiss = {
                        viewModel.setShowProfileSwitcher(false)
                    }
                )
            }
        }
    }
}
