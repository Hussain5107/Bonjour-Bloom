package com.example.bonjourbloom.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.data.model.PlanStepType
import com.example.bonjourbloom.ui.theme.BackgroundSoft
import com.example.bonjourbloom.ui.theme.CoralDark
import com.example.bonjourbloom.ui.theme.CoralLight
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintDark
import com.example.bonjourbloom.ui.theme.MintSuccess
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary
import com.example.bonjourbloom.ui.theme.PetalOrange
import com.example.bonjourbloom.ui.theme.SunGold
import com.example.bonjourbloom.ui.viewmodel.DailySessionUiState

@Composable
fun DailySessionScreen(
    sessionState: DailySessionUiState,
    onSelectAnswer: (String) -> Unit,
    onShowHint: () -> Unit,
    onNextItem: () -> Unit,
    onExit: () -> Unit,
    onSpeakFrench: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (sessionState.isCompleted) {
        DailySessionSummaryView(
            sessionState = sessionState,
            onExit = onExit,
            onSpeakFrench = onSpeakFrench,
            modifier = modifier
        )
        return
    }

    val currentItem = sessionState.items.getOrNull(sessionState.currentItemIndex)

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSoft),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CreamSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onExit,
                        modifier = Modifier.testTag("session_exit_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Exit session", tint = NavyPrimary)
                    }

                    Text(
                        text = sessionState.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        ),
                        modifier = Modifier.testTag("session_title")
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🌸", fontSize = 16.sp)
                        Text(
                            text = "${sessionState.petalsEarned}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PetalOrange
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                val progress = if (sessionState.totalItems > 0) {
                    (sessionState.currentItemIndex.toFloat() / sessionState.totalItems.toFloat())
                } else 0f
                val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = CoralLight,
                        trackColor = LineBorder
                    )
                    Text(
                        text = "${sessionState.currentItemIndex + 1}/${sessionState.totalItems}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedText
                    )
                }
            }
        },
        bottomBar = {
            // Interactive feedback banner
            if (sessionState.feedbackState != null) {
                val isCorrect = sessionState.feedbackState == "correct"
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("session_feedback_bar"),
                    color = if (isCorrect) MintSuccess else CoralLight,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(if (isCorrect) "🌟" else "💡", fontSize = 24.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isCorrect) "Magnifique ! Great recall" else "Not quite, keep practicing!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isCorrect) MintDark else CoralDark
                                )
                                if (currentItem != null && currentItem.explanation.isNotEmpty()) {
                                    Text(
                                        text = currentItem.explanation,
                                        fontSize = 13.sp,
                                        color = if (isCorrect) MintDark else CoralDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onNextItem,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("session_continue_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCorrect) MintDark else CoralDark
                            )
                        ) {
                            Text("Continue", fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (currentItem != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Step Category Pill
                val stepBadgeColor = when (currentItem.stepType) {
                    PlanStepType.DUE_REVIEW -> CoralLight
                    PlanStepType.NEXT_LESSON -> MintSuccess
                    PlanStepType.NEW_VOCAB -> SunGold.copy(alpha = 0.3f)
                    else -> CreamSurface
                }
                val stepBadgeText = when (currentItem.stepType) {
                    PlanStepType.DUE_REVIEW -> "🧠 Spaced Review"
                    PlanStepType.NEXT_LESSON -> "📖 Daily Lesson Step"
                    PlanStepType.NEW_VOCAB -> "🌱 New Vocabulary"
                    else -> "✦ Daily Practice"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(stepBadgeColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stepBadgeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card with Emoji and Prompt
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentItem.emoji,
                            fontSize = 48.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = currentItem.prompt,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.testTag("session_prompt_text")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Audio button row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onSpeakFrench(currentItem.audioText, false) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CoralLight),
                                modifier = Modifier.testTag("session_audio_button")
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = CoralDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Listen", color = CoralDark, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { onSpeakFrench(currentItem.audioText, true) },
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                            ) {
                                Text("Slow 🐢", fontSize = 12.sp, color = NavyPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Answer Options List
                Text(
                    text = "Select the correct translation:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(10.dp))

                currentItem.options.forEach { option ->
                    val isSelected = sessionState.selectedAnswer == option
                    val borderColor = when {
                        isSelected && sessionState.feedbackState == "correct" -> MintDark
                        isSelected && sessionState.feedbackState == "try" -> CoralDark
                        isSelected -> PetalOrange
                        else -> LineBorder
                    }

                    val bgColor = when {
                        isSelected && sessionState.feedbackState == "correct" -> MintSuccess
                        isSelected && sessionState.feedbackState == "try" -> CoralLight
                        isSelected -> CreamSurface
                        else -> Color.White
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable(enabled = sessionState.feedbackState == null) {
                                onSelectAnswer(option)
                            }
                            .testTag("session_option_${option.replace(" ", "_")}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = NavyPrimary
                            )

                            if (isSelected) {
                                Text(
                                    text = if (sessionState.feedbackState == "correct") "✓" else if (sessionState.feedbackState == "try") "✗" else "●",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sessionState.feedbackState == "correct") MintDark else CoralDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hint Button / Drawer
                AnimatedVisibility(visible = !sessionState.hintShown && sessionState.feedbackState == null) {
                    OutlinedButton(
                        onClick = onShowHint,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("session_hint_button")
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = SunGold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Show Hint 💡", color = NavyPrimary)
                    }
                }

                if (sessionState.hintShown && currentItem.hint.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SunGold.copy(alpha = 0.15f))
                            .border(1.dp, SunGold, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💡 ", fontSize = 16.sp)
                            Text(
                                text = "Hint: ${currentItem.hint}",
                                fontSize = 13.sp,
                                color = NavyPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailySessionSummaryView(
    sessionState: DailySessionUiState,
    onExit: () -> Unit,
    onSpeakFrench: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSoft),
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CreamSurface,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = onExit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp)
                        .testTag("session_finish_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralDark)
                ) {
                    Text("Return to Garden 🌸", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Celebration Badge
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MintSuccess),
                contentAlignment = Alignment.Center
            ) {
                Text("🏆", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bravo ! Session Finished",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.testTag("summary_headline")
            )

            Text(
                text = "You completed your personalized daily French path today!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MutedText,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🌸", fontSize = 24.sp)
                        Text(
                            text = "+${sessionState.petalsEarned}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = PetalOrange
                        )
                        Text("Petals XP", fontSize = 12.sp, color = MutedText)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎯", fontSize = 24.sp)
                        Text(
                            text = "${sessionState.correctCount}/${sessionState.totalItems}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MintDark
                        )
                        Text("Accuracy", fontSize = 12.sp, color = MutedText)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reviewed Words List
            if (sessionState.reviewedWords.isNotEmpty()) {
                Text(
                    text = "Words & Phrases Practiced Today:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                sessionState.reviewedWords.forEach { word ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CreamSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = word,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            IconButton(onClick = { onSpeakFrench(word, false) }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Pronounce", tint = CoralDark)
                            }
                        }
                    }
                }
            }
        }
    }
}
