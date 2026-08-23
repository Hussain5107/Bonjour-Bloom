package com.example.bonjourbloom.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.data.model.ExerciseType
import com.example.bonjourbloom.data.model.Lesson
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CoralDark
import com.example.bonjourbloom.ui.theme.CoralLight
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.GoldDark
import com.example.bonjourbloom.ui.theme.GoldLight
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintDark
import com.example.bonjourbloom.ui.theme.MintSuccess
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary
import com.example.bonjourbloom.ui.theme.SoftGray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LessonScreen(
    lesson: Lesson,
    currentIndex: Int,
    selectedAnswer: String,
    feedbackState: String?,
    onSelectOption: (String) -> Unit,
    onSetSentenceOrder: (String) -> Unit,
    onNextExercise: () -> Unit,
    onOpenPronunciationStudio: (expected: String) -> Unit,
    onSpeakFrench: (text: String, isSlow: Boolean) -> Unit,
    onExitLesson: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentExercise = lesson.exercises.getOrNull(currentIndex) ?: return
    val totalExercises = lesson.exercises.size
    val progressFraction = (currentIndex.toFloat() / totalExercises.toFloat()).coerceIn(0f, 1f)
    val scrollState = rememberScrollState()

    // Sentence builder tokens state
    val tokensInTarget = remember(currentIndex) { mutableStateListOf<String>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Top Header with progress and exit
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onExitLesson,
                modifier = Modifier.testTag("exit_lesson_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit lesson",
                    tint = MutedText
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = CoralAccent,
                trackColor = SoftGray
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "${currentIndex + 1}/$totalExercises",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Exercise Type Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(CoralLight)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when (currentExercise.type) {
                        ExerciseType.LISTEN -> "LISTEN & REPEAT"
                        ExerciseType.PICTURE_CHOICE -> "PICTURE RECOGNITION"
                        ExerciseType.MULTIPLE_CHOICE -> "CHOOSE THE MATCH"
                        ExerciseType.SENTENCE_ORDER -> "BUILD THE PHRASE"
                        ExerciseType.FILL_BLANK -> "COMPLETE THE SENTENCE"
                        ExerciseType.SPEAKING_PROMPT -> "PRONUNCIATION PRACTICE"
                        ExerciseType.DIALOGUE_RESPONSE -> "FRIENDLY DIALOGUE"
                        ExerciseType.SPACED_REVIEW -> "SPACED RECALL"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CoralAccent,
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Prompt Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lesson_prompt_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (currentExercise.emoji.isNotBlank()) {
                        Text(
                            text = currentExercise.emoji,
                            fontSize = 44.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Text(
                        text = currentExercise.prompt,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 26.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 32.sp
                        )
                    )

                    if (currentExercise.englishHint.isNotBlank()) {
                        Text(
                            text = currentExercise.englishHint,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MutedText,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                val textToSpeak = currentExercise.audioText.ifBlank { currentExercise.prompt }
                                onSpeakFrench(textToSpeak, false)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
                            modifier = Modifier.testTag("listen_normal_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Listen", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val textToSpeak = currentExercise.audioText.ifBlank { currentExercise.prompt }
                                onSpeakFrench(textToSpeak, true)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("listen_slow_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hearing,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Slow", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Exercise Interactions
            when (currentExercise.type) {
                ExerciseType.SPEAKING_PROMPT -> {
                    // Speaking mode card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CreamSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Say it out loud with Milo!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            )
                            Text(
                                text = "Practice pronunciation in Milo’s studio with live mic playback.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MutedText,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val text = currentExercise.audioText.ifBlank { currentExercise.prompt }
                                    onOpenPronunciationStudio(text)
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
                                modifier = Modifier.testTag("open_speaking_studio_button")
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Open Milo's Studio", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                ExerciseType.SENTENCE_ORDER -> {
                    // Word token builder
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Sentence display box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CreamSurface)
                                .border(1.dp, LineBorder, RoundedCornerShape(14.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (tokensInTarget.isEmpty()) {
                                Text(
                                    text = "Tap words below to arrange the phrase…",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MutedText),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            } else {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    tokensInTarget.forEach { token ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(CoralLight)
                                                .border(1.dp, CoralAccent, RoundedCornerShape(8.dp))
                                                .clickable {
                                                    tokensInTarget.remove(token)
                                                    onSetSentenceOrder(tokensInTarget.joinToString(" "))
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = token,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = NavyPrimary
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Word Token Bank
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            currentExercise.options.forEach { token ->
                                val isUsed = tokensInTarget.contains(token)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isUsed) SoftGray else CreamSurface)
                                        .border(1.dp, if (isUsed) LineBorder else CoralAccent, RoundedCornerShape(10.dp))
                                        .clickable(enabled = !isUsed) {
                                            tokensInTarget.add(token)
                                            val fullSentence = tokensInTarget.joinToString(" ")
                                            onSetSentenceOrder(fullSentence)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                        .testTag("token_$token")
                                ) {
                                    Text(
                                        text = token,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUsed) MutedText else NavyPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Standard Choice Options
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        currentExercise.options.forEach { option ->
                            val isSelected = selectedAnswer == option
                            val isCorrectAnswer = option.trim().equals(currentExercise.answer.trim(), ignoreCase = true)
                            val showSuccessHighlight = isSelected && feedbackState == "correct"
                            val showTryHighlight = isSelected && feedbackState == "try"

                            val cardBg = when {
                                showSuccessHighlight -> MintSuccess
                                showTryHighlight -> CoralLight
                                isSelected -> CoralLight
                                else -> CreamSurface
                            }

                            val cardBorderColor = when {
                                showSuccessHighlight -> MintDark
                                showTryHighlight -> CoralAccent
                                isSelected -> CoralAccent
                                else -> LineBorder
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectOption(option) }
                                    .testTag("exercise_option_$option"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor)
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
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )
                                    )
                                    if (showSuccessHighlight) {
                                        Text(text = "🌸 Correct!", color = MintDark, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Feedback Card
            AnimatedVisibility(
                visible = feedbackState != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (feedbackState == "correct") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("feedback_correct"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MintSuccess),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MintDark.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🌸 Très bien ! Beautiful work.",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MintDark
                                )
                            )
                            Text(
                                text = "You’re growing your French roots with every attempt!",
                                style = MaterialTheme.typography.bodySmall.copy(color = InkText)
                            )
                        }
                    }
                } else if (feedbackState == "try") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("feedback_try"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🌱 Not quite yet — listen and try once more!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldDark
                                )
                            )
                            Text(
                                text = "Every little mistake helps your brain remember.",
                                style = MaterialTheme.typography.bodySmall.copy(color = InkText)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom Action Button
        val canProceed = feedbackState == "correct" ||
                currentExercise.type == ExerciseType.SPEAKING_PROMPT ||
                currentExercise.type == ExerciseType.LISTEN

        Button(
            onClick = onNextExercise,
            enabled = canProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("next_exercise_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CoralAccent)
        ) {
            Text(
                text = if (currentIndex >= totalExercises - 1) "Finish Lesson 🌸" else "Next activity",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}
