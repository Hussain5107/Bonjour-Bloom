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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.data.model.AgeBand
import com.example.bonjourbloom.data.model.TeacherVoice
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CoralLight
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary

@Composable
fun ProfileSetupScreen(
    onSaveProfile: (name: String, ageBand: AgeBand, avatar: String, dailyGoal: Int, teacherVoice: TeacherVoice) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var ageBand by remember { mutableStateOf(AgeBand.EARLY) }
    var selectedAvatar by remember { mutableStateOf("🦊") }
    var dailyGoal by remember { mutableIntStateOf(1) }
    var teacherVoice by remember { mutableStateOf(TeacherVoice.FEMALE) }

    val avatars = listOf("🦊", "🐼", "🐯", "🐙", "🦋", "🦁")
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Text(
            text = "bonjour bloom",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        )
        Text(
            text = "CREATE A LEARNER PROFILE",
            style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent),
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "Who’s learning today?",
            style = MaterialTheme.typography.headlineLarge.copy(color = NavyPrimary)
        )
        Text(
            text = if (ageBand == AgeBand.ADULT)
                "Build practical French with clear explanations and active recall."
            else
                "Create a separate, age-adapted learning garden for every child.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MutedText),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_setup_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CreamSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Name Input
                Text(
                    text = "First name or nickname",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 18) name = it },
                    placeholder = { Text("e.g. Amélie or Leo") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("learner_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Age Band selector
                Text(
                    text = "Choose learning mode",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AgeBand.entries.forEach { mode ->
                        val isSelected = ageBand == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) CoralLight else CreamBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) CoralAccent else LineBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { ageBand = mode }
                                .padding(12.dp)
                                .testTag("age_mode_${mode.name.lowercase()}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = mode.label,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = if (isSelected) CoralAccent else NavyPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Ages ${mode.ageRange}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                                    )
                                }
                                Text(
                                    text = mode.note,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Avatar picker
                Text(
                    text = "Pick an avatar",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(avatars) { av ->
                        val isSelected = selectedAvatar == av
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) CoralLight else CreamBackground)
                                .border(
                                    2.dp,
                                    if (isSelected) CoralAccent else LineBorder,
                                    CircleShape
                                )
                                .clickable { selectedAvatar = av }
                                .testTag("avatar_choice_$av"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = av, fontSize = 26.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Daily goal
                Text(
                    text = "Daily goal",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val goals = listOf(
                        1 to "1 lesson · gentle",
                        2 to "2 lessons · steady",
                        3 to "3 lessons · keen"
                    )
                    goals.forEach { (count, label) ->
                        val isSelected = dailyGoal == count
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CoralLight else CreamBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) CoralAccent else LineBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { dailyGoal = count }
                                .padding(vertical = 10.dp, horizontal = 6.dp)
                                .testTag("daily_goal_$count"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CoralAccent else InkText
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Teacher Voice preference
                Text(
                    text = "Teacher French Voice",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TeacherVoice.entries.forEach { voice ->
                        val isSelected = teacherVoice == voice
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CoralLight else CreamBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) CoralAccent else LineBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { teacherVoice = voice }
                                .padding(vertical = 10.dp, horizontal = 8.dp)
                                .testTag("voice_${voice.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (voice == TeacherVoice.FEMALE) "Female voice" else "Male voice",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CoralAccent else InkText
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit button
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onSaveProfile(name.trim(), ageBand, selectedAvatar, dailyGoal, teacherVoice)
                        }
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_learning_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralAccent)
                ) {
                    Text(
                        text = "Start learning",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Sparkles, contentDescription = null)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
