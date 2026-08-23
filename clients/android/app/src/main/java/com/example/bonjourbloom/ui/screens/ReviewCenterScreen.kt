package com.example.bonjourbloom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.data.model.LearningState
import com.example.bonjourbloom.data.model.ReviewableUnit
import com.example.bonjourbloom.ui.theme.BackgroundSoft
import com.example.bonjourbloom.ui.theme.CoralDark
import com.example.bonjourbloom.ui.theme.CoralLight
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintDark
import com.example.bonjourbloom.ui.theme.MintSuccess
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary
import com.example.bonjourbloom.ui.theme.SunGold

@Composable
fun ReviewCenterScreen(
    reviewUnits: List<ReviewableUnit>,
    onStartReview: (List<String>?) -> Unit,
    onToggleSuspend: (String, Boolean) -> Unit,
    onResetItem: (String) -> Unit,
    onSpeakFrench: (String, Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedTopic by remember { mutableStateOf("all") }
    var resetConfirmItem by remember { mutableStateOf<ReviewableUnit?>(null) }

    val now = System.currentTimeMillis()
    val dueUnits = reviewUnits.filter { !it.isSuspended && (it.nextReviewEpochMs <= now || it.learningState == LearningState.RELEARNING) }
    val masteredUnits = reviewUnits.filter { it.consecutiveSuccesses >= 5 && it.currentIntervalDays >= 7f }

    val filteredUnits = reviewUnits.filter { unit ->
        val matchesTopic = selectedTopic == "all" || unit.topic.equals(selectedTopic, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Due Today" -> !unit.isSuspended && (unit.nextReviewEpochMs <= now || unit.learningState == LearningState.RELEARNING)
            "Learning" -> unit.learningState == LearningState.LEARNING || unit.learningState == LearningState.RELEARNING
            "Mastered" -> unit.consecutiveSuccesses >= 5 && unit.currentIntervalDays >= 7f
            "Suspended" -> unit.isSuspended
            else -> true
        }
        matchesTopic && matchesFilter
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSoft),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CreamSurface)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("review_center_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NavyPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Spaced Review & Mastery",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                    Text(
                        text = "Scientifically scheduled retrieval intervals",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Due Review Banner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${dueUnits.size} Words Due",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (dueUnits.isNotEmpty()) CoralDark else MintDark
                                )
                                Text(
                                    text = if (dueUnits.isNotEmpty()) "Ready for memory strengthening" else "All caught up today! 🌟",
                                    fontSize = 12.sp,
                                    color = MutedText
                                )
                            }

                            Button(
                                onClick = { onStartReview(if (dueUnits.isNotEmpty()) dueUnits.map { it.unitId } else null) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CoralDark),
                                modifier = Modifier.testTag("start_review_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Review Now", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BackgroundSoft)
                                    .padding(8.dp)
                            ) {
                                Text("📚 Total: ${reviewUnits.size}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = NavyPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MintSuccess.copy(alpha = 0.5f))
                                    .padding(8.dp)
                            ) {
                                Text("🏆 Mastered: ${masteredUnits.size}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintDark)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filter State Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf("All", "Due Today", "Learning", "Mastered", "Suspended")
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CoralLight,
                                selectedLabelColor = CoralDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Topic Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val topics = listOf(
                        "all" to "All Topics",
                        "greetings" to "Greetings",
                        "introductions" to "Introductions",
                        "numbers" to "Numbers",
                        "colors" to "Colors",
                        "family" to "Family"
                    )
                    items(topics) { (key, label) ->
                        FilterChip(
                            selected = selectedTopic == key,
                            onClick = { selectedTopic = key },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SunGold.copy(alpha = 0.3f),
                                selectedLabelColor = NavyPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (filteredUnits.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌱", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No items found in this filter", color = MutedText, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                items(filteredUnits, key = { it.unitId }) { unit ->
                    ReviewUnitCard(
                        unit = unit,
                        nowEpochMs = now,
                        onSpeak = { text, isSlow -> onSpeakFrench(text, isSlow) },
                        onPractice = { onStartReview(listOf(unit.unitId)) },
                        onToggleSuspend = { onToggleSuspend(unit.unitId, !unit.isSuspended) },
                        onReset = { resetConfirmItem = unit }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (resetConfirmItem != null) {
        val item = resetConfirmItem!!
        AlertDialog(
            onDismissRequest = { resetConfirmItem = null },
            title = { Text("Reset Review Interval?") },
            text = {
                Text("This will reset '${item.frenchText}' to the active learning queue (due immediately) and reset its consecutive memory streak. Existing learning event history is preserved.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetItem(item.unitId)
                        resetConfirmItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralDark)
                ) {
                    Text("Reset to Learning", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { resetConfirmItem = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ReviewUnitCard(
    unit: ReviewableUnit,
    nowEpochMs: Long,
    onSpeak: (String, Boolean) -> Unit,
    onPractice: () -> Unit,
    onToggleSuspend: () -> Unit,
    onReset: () -> Unit
) {
    val isDue = !unit.isSuspended && (unit.nextReviewEpochMs <= nowEpochMs || unit.learningState == LearningState.RELEARNING)
    val isMastered = unit.consecutiveSuccesses >= 5 && unit.currentIntervalDays >= 7f

    val stateBadgeColor = when {
        unit.isSuspended -> Color.LightGray.copy(alpha = 0.4f)
        isMastered -> MintSuccess
        isDue -> CoralLight
        unit.learningState == LearningState.LEARNING -> SunGold.copy(alpha = 0.3f)
        else -> CreamSurface
    }

    val stateBadgeText = when {
        unit.isSuspended -> "Suspended"
        isMastered -> "Mastered 🏆"
        isDue -> "Due Now ⏰"
        unit.learningState == LearningState.LEARNING -> "Learning 🌱"
        else -> "Review in ${unit.currentIntervalDays.toInt()}d"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(unit.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = unit.frenchText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = unit.englishText,
                            fontSize = 13.sp,
                            color = MutedText
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(stateBadgeColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stateBadgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Streak: ${unit.consecutiveSuccesses} | Reviews: ${unit.totalReviewCount}",
                    fontSize = 11.sp,
                    color = MutedText
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { onSpeak(unit.frenchText, false) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Pronounce", tint = CoralDark, modifier = Modifier.size(20.dp))
                    }

                    IconButton(onClick = onPractice, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Practice", tint = MintDark, modifier = Modifier.size(20.dp))
                    }

                    IconButton(onClick = onToggleSuspend, modifier = Modifier.size(36.dp)) {
                        Icon(
                            if (unit.isSuspended) Icons.Default.CheckCircle else Icons.Default.Block,
                            contentDescription = if (unit.isSuspended) "Unsuspend" else "Suspend",
                            tint = if (unit.isSuspended) MintDark else MutedText,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onReset, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = MutedText, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
