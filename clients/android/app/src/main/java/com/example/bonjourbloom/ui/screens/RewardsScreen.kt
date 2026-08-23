package com.example.bonjourbloom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.data.model.Achievement
import com.example.bonjourbloom.data.model.Profile
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.GoldDark
import com.example.bonjourbloom.ui.theme.GoldLight
import com.example.bonjourbloom.ui.theme.GoldWarning
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintDark
import com.example.bonjourbloom.ui.theme.MintSuccess
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary

@Composable
fun RewardsScreen(
    profile: Profile,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val achievements = listOf(
        Achievement(
            id = "first_seed",
            emoji = "🌱",
            title = "First seed",
            description = "Finish your first French lesson",
            isEarned = profile.completedLessons.isNotEmpty()
        ),
        Achievement(
            id = "careful_listener",
            emoji = "🎧",
            title = "Careful listener",
            description = "Complete 3 listening activities",
            isEarned = profile.completedLessons.size >= 2
        ),
        Achievement(
            id = "brave_voice",
            emoji = "🗣️",
            title = "Brave voice",
            description = "Try speaking French in Pronunciation Studio",
            isEarned = profile.speakingAttempts > 0 || profile.achievements.contains("brave_voice")
        ),
        Achievement(
            id = "review_gardener",
            emoji = "🌻",
            title = "Review gardener",
            description = "Complete spaced review sessions",
            isEarned = profile.reviews > 0 || profile.achievements.contains("review_gardener")
        ),
        Achievement(
            id = "starter_bloom",
            emoji = "🏆",
            title = "Starter Unit Bloom",
            description = "Complete all 5 starter lessons",
            isEarned = profile.completedLessons.size >= 5
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "EFFORT BLOOMS",
            style = MaterialTheme.typography.labelSmall.copy(
                color = CoralAccent,
                letterSpacing = 1.5.sp
            )
        )
        Text(
            text = "Your little wins",
            style = MaterialTheme.typography.headlineLarge.copy(color = NavyPrimary)
        )
        Text(
            text = "We celebrate practice, courage and coming back — never perfection.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MutedText),
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        // Summary Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                icon = Icons.Default.Star,
                iconColor = GoldWarning,
                value = "${profile.xp}",
                label = "petals earned",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.AutoStories,
                iconColor = CoralAccent,
                value = "${profile.completedLessons.size}",
                label = "lessons finished",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.Headphones,
                iconColor = MintDark,
                value = "${profile.minutes}",
                label = "min learned",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ACHIEVEMENT BADGES",
            style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Achievements list
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            achievements.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("achievement_${item.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isEarned) CreamSurface else CreamSurface.copy(alpha = 0.6f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (item.isEarned) MintDark.copy(alpha = 0.5f) else LineBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (item.isEarned) GoldLight else LineBorder.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.emoji, fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isEarned) NavyPrimary else MutedText
                                )
                            )
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (item.isEarned) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MintSuccess)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MintDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Earned",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MintDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = "In progress",
                                style = MaterialTheme.typography.labelSmall.copy(color = MutedText)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MutedText,
                    fontSize = 10.sp
                )
            )
        }
    }
}
