package com.example.bonjourbloom.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.data.curriculum.CurriculumData
import com.example.bonjourbloom.data.model.Profile
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CoralLight
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.GoldDark
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintDark
import com.example.bonjourbloom.ui.theme.MintSuccess
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary

@Composable
fun LearnScreen(
    profile: Profile,
    onStartLesson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "YOUR FRENCH JOURNEY",
            style = MaterialTheme.typography.labelSmall.copy(
                color = CoralAccent,
                letterSpacing = 1.5.sp
            )
        )
        Text(
            text = "Pre-A1 Starter Path",
            style = MaterialTheme.typography.headlineLarge.copy(color = NavyPrimary)
        )
        Text(
            text = "Listen → Repeat → Recognize → Recall → Use → Review",
            style = MaterialTheme.typography.bodyMedium.copy(color = MutedText),
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        // Starter Unit summary card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("starter_unit_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CreamSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PRE-A1 · STARTER UNIT",
                        style = MaterialTheme.typography.labelSmall.copy(color = GoldDark)
                    )
                    Text(
                        text = "First steps in French",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                    Text(
                        text = "Greetings, introductions, numbers, colors and family.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CoralLight)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${profile.completedLessons.size}/5 complete",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = CoralAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lesson list
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CurriculumData.lessonsList.forEachIndexed { index, lesson ->
                val isCompleted = profile.completedLessons.contains(lesson.id)
                val badgeColor = try {
                    Color(android.graphics.Color.parseColor(lesson.colorHex))
                } catch (e: Exception) {
                    CoralAccent
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartLesson(lesson.id) }
                        .testTag("learn_lesson_item_${lesson.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Lesson Number badge
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(badgeColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
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
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Status badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCompleted) MintSuccess else CoralLight)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isCompleted) "Done" else "Start",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isCompleted) MintDark else CoralAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Future levels roadmap
        Text(
            text = "FUTURE CURRICULUM PATHS",
            style = MaterialTheme.typography.labelSmall.copy(color = MutedText)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                "A1 · Everyday explorer" to "Structured expansion ready",
                "A2 · Growing confidence" to "Scalable dialogue paths",
                "B1–C1 · Future adventures" to "Fluency & native stories"
            ).forEach { (title, subtitle) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CreamSurface)
                        .border(1.dp, LineBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = MutedText,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = InkText
                            )
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
