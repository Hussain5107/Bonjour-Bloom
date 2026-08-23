package com.example.bonjourbloom.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.R
import com.example.bonjourbloom.data.curriculum.CurriculumData
import com.example.bonjourbloom.data.model.DailyLearningPlan
import com.example.bonjourbloom.data.model.Lesson
import com.example.bonjourbloom.data.model.ObjectiveMastery
import com.example.bonjourbloom.data.model.ObjectiveStatus
import com.example.bonjourbloom.data.model.PlanStepType
import com.example.bonjourbloom.data.model.Profile
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CoralDark
import com.example.bonjourbloom.ui.theme.CoralLight
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.GoldDark
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintDark
import com.example.bonjourbloom.ui.theme.MintSuccess
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyDark
import com.example.bonjourbloom.ui.theme.NavyPrimary
import com.example.bonjourbloom.ui.theme.PetalOrange
import com.example.bonjourbloom.ui.theme.SoftGray
import com.example.bonjourbloom.ui.theme.SunGold

@Composable
fun HomeScreen(
    profile: Profile,
    dailyPlan: DailyLearningPlan? = null,
    dueReviewsCount: Int = 0,
    objectiveMasteries: List<ObjectiveMastery> = emptyList(),
    onStartDailyPlan: () -> Unit = {},
    onStartReviewSession: () -> Unit = {},
    onOpenReviewCenter: () -> Unit = {},
    onStartLesson: (String) -> Unit,
    onSeeAllLessons: () -> Unit,
    onOpenVoiceChat: () -> Unit = {},
    onOpenTranscriber: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val nextLesson: Lesson = CurriculumData.lessonsList.find { !profile.completedLessons.contains(it.id) }
        ?: CurriculumData.lessonsList.first()

    val goalFraction = if (profile.dailyGoal > 0) {
        (profile.completedLessons.size.toFloat() / profile.dailyGoal).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(targetValue = goalFraction, label = "goalProgress")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Welcome Greeting
        Text(
            text = "BONJOUR, ${profile.name.uppercase()}!",
            style = MaterialTheme.typography.labelSmall.copy(
                color = CoralAccent,
                letterSpacing = 1.5.sp
            )
        )
        Text(
            text = "Ready for a little French?",
            style = MaterialTheme.typography.headlineLarge.copy(color = NavyPrimary)
        )
        Text(
            text = "Every word is a new leaf in your garden.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MutedText),
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        // =========================================================
        // SECTION 3: DETERMINISTIC TODAY'S LEARNING PLAN HERO CARD
        // =========================================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("daily_plan_hero_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CreamSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, LineBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌱", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Today's Learning Path",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            )
                            val targetMins = dailyPlan?.targetMinutes ?: (profile.dailyGoal * 5)
                            Text(
                                text = "${targetMins} min target · Age-adapted plan",
                                fontSize = 12.sp,
                                color = MutedText
                            )
                        }
                    }

                    if (dailyPlan?.isCompleted == true) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MintSuccess)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("DONE 🌟", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintDark)
                        }
                    } else if (dueReviewsCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CoralLight)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("$dueReviewsCount due ⏰", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CoralDark)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Explainable Steps Overview
                if (dailyPlan != null && dailyPlan.steps.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundSoftCard)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        dailyPlan.steps.take(3).forEach { step ->
                            val icon = when (step.stepType) {
                                PlanStepType.DUE_REVIEW -> "🧠"
                                PlanStepType.RESUME_LESSON, PlanStepType.NEXT_LESSON -> "📖"
                                PlanStepType.NEW_VOCAB -> "🌱"
                                PlanStepType.SESSION_RECAP -> "🌸"
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(icon, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = step.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = NavyPrimary
                                    )
                                }
                                Text(
                                    text = "~${step.estimatedMinutes}m",
                                    fontSize = 11.sp,
                                    color = MutedText
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onStartDailyPlan,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("start_daily_session_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CoralDark)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (dailyPlan?.isCompleted == true) "Practice Again" else "Start Session",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenReviewCenter,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("open_review_center_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Review (${dueReviewsCount})", fontSize = 12.sp, color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Continue Lesson Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("continue_lesson_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavyPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Banner with Garden Illustration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(NavyDark)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_french_garden),
                        contentDescription = "French garden with Milo the fox",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Speech bubble badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CreamSurface)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "On y va ? 🦊",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        )
                    }
                }

                // Lesson details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "EXPLORE LESSON · UNIT 1",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldDark,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Text(
                        text = nextLesson.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Text(
                        text = nextLesson.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD7E3EB)),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = null,
                                tint = Color(0xFFFFCF98),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${nextLesson.exercises.size} activities",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFFFFCF98),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${nextLesson.minutes} min",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = { onStartLesson(nextLesson.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("start_continue_lesson_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CoralAccent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Lesson",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Gemini Live Voice & Audio Transcriber Quick Access Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Voice Room with Milo Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenVoiceChat() }
                    .testTag("home_voice_room_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎙️", fontSize = 24.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MintSuccess)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "GEMINI",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MintDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Voice Room",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                    Text(
                        text = "Talk with Milo in French",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MutedText,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Live Audio Transcriber Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenTranscriber() }
                    .testTag("home_transcriber_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎧", fontSize = 24.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CoralLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "REAL-TIME",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CoralDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Transcriber",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                    Text(
                        text = "Live audio feed text",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MutedText,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // =========================================================
        // SECTION 3: OBJECTIVE MASTERY SNAPSHOT (CEFR Pre-A1)
        // =========================================================
        if (objectiveMasteries.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CEFR PRE-A1",
                        style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent)
                    )
                    Text(
                        text = "Learning Objectives Mastery",
                        style = MaterialTheme.typography.titleLarge.copy(color = NavyPrimary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                objectiveMasteries.forEach { obj ->
                    val statusColor = when (obj.status) {
                        ObjectiveStatus.MASTERED -> MintDark
                        ObjectiveStatus.FAMILIAR -> MintDark
                        ObjectiveStatus.PRACTISING -> CoralDark
                        ObjectiveStatus.INTRODUCED -> PetalOrange
                        ObjectiveStatus.NEEDS_REVIEW -> CoralDark
                        ObjectiveStatus.NOT_STARTED -> MutedText
                    }

                    val statusBg = when (obj.status) {
                        ObjectiveStatus.MASTERED -> MintSuccess
                        ObjectiveStatus.FAMILIAR -> MintSuccess.copy(alpha = 0.5f)
                        ObjectiveStatus.PRACTISING -> CoralLight
                        ObjectiveStatus.INTRODUCED -> SunGold.copy(alpha = 0.3f)
                        ObjectiveStatus.NEEDS_REVIEW -> CoralLight
                        ObjectiveStatus.NOT_STARTED -> SoftGray
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("objective_card_${obj.objectiveId}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CreamSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = obj.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(statusBg)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = obj.status.name.replace("_", " "),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                }
                            }

                            Text(
                                text = obj.description,
                                fontSize = 12.sp,
                                color = MutedText,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            LinearProgressIndicator(
                                progress = { obj.masteryScore },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = statusColor,
                                trackColor = SoftGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section header for lessons path
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CURRICULUM",
                    style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent)
                )
                Text(
                    text = "First steps in French",
                    style = MaterialTheme.typography.titleLarge.copy(color = NavyPrimary)
                )
            }
            TextButton(
                onClick = onSeeAllLessons,
                modifier = Modifier.testTag("see_all_lessons_button")
            ) {
                Text(
                    text = "See all",
                    fontWeight = FontWeight.Bold,
                    color = CoralDark
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = CoralDark,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Path cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CurriculumData.lessonsList.take(3).forEachIndexed { index, lesson ->
                val isCompleted = profile.completedLessons.contains(lesson.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartLesson(lesson.id) }
                        .testTag("path_card_${lesson.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val badgeColor = try {
                            Color(android.graphics.Color.parseColor(lesson.colorHex))
                        } catch (e: Exception) {
                            CoralAccent
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(badgeColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            )
                            Text(
                                text = "${lesson.subtitle} · ${lesson.minutes} min",
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { if (isCompleted) 1f else 0f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MintDark,
                                trackColor = SoftGray
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

val BackgroundSoftCard = Color(0xFFF3EDE2)
