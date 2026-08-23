package com.example.bonjourbloom.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.data.model.FamilySettings
import com.example.bonjourbloom.data.model.Profile
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintDark
import com.example.bonjourbloom.ui.theme.MintSuccess
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary

@Composable
fun ParentScreen(
    settings: FamilySettings,
    profiles: List<Profile>,
    isUnlocked: Boolean,
    onUnlock: (String) -> Boolean,
    onLock: () -> Unit,
    onUpdateSettings: (FamilySettings) -> Unit,
    onUpdateGoal: (profileId: String, newGoal: Int) -> Unit,
    onDeleteProfile: (String) -> Unit,
    onExportJson: () -> String,
    onBackToLearning: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonContent by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    if (!isUnlocked) {
        // Locked Gate
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CreamBackground)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(CreamSurface)
                    .border(1.dp, LineBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = CoralAccent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "GROWN-UPS ONLY",
                style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent)
            )
            Text(
                text = "Parent check",
                style = MaterialTheme.typography.headlineMedium.copy(color = NavyPrimary)
            )
            Text(
                text = "Enter your 4-digit parent code to manage profiles and family settings.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MutedText,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = enteredPin,
                onValueChange = {
                    if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                        enteredPin = it
                        showError = false
                    }
                },
                placeholder = { Text("e.g. 2468") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier
                    .width(200.dp)
                    .testTag("parent_gate_pin_input"),
                shape = RoundedCornerShape(14.dp)
            )

            if (showError) {
                Text(
                    text = "That code doesn’t match.",
                    color = CoralAccent,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val ok = onUnlock(enteredPin)
                    if (!ok) {
                        showError = true
                    }
                },
                enabled = enteredPin.length == 4,
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
                    .testTag("unlock_parent_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoralAccent)
            ) {
                Text(text = "Unlock", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onBackToLearning,
                modifier = Modifier.testTag("back_to_learning_button")
            ) {
                Text(text = "Back to learning", color = MutedText, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        // Unlocked Parent Dashboard
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CreamBackground)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PARENT DASHBOARD",
                        style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent)
                    )
                    Text(
                        text = "Family French Garden",
                        style = MaterialTheme.typography.headlineLarge.copy(color = NavyPrimary)
                    )
                }
                TextButton(onClick = onLock) {
                    Text(text = "Lock", color = CoralAccent, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "Local summaries stored privately on this device.",
                style = MaterialTheme.typography.bodyMedium.copy(color = MutedText),
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
            )

            // Profiles list
            Text(
                text = "LEARNER PROFILES",
                style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                profiles.forEach { p ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_profile_card_${p.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CreamSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = p.avatar, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = p.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = NavyPrimary
                                            )
                                        )
                                        Text(
                                            text = "${p.ageBand.label} (Ages ${p.ageBand.ageRange})",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                                        )
                                    }
                                }

                                if (profiles.size > 1) {
                                    IconButton(onClick = { onDeleteProfile(p.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete profile",
                                            tint = MutedText
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Summary metrics
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CreamBackground)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${p.minutes}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )
                                    )
                                    Text(text = "minutes", style = MaterialTheme.typography.labelSmall.copy(color = MutedText))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${p.completedLessons.size}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )
                                    )
                                    Text(text = "lessons", style = MaterialTheme.typography.labelSmall.copy(color = MutedText))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${p.xp}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )
                                    )
                                    Text(text = "petals", style = MaterialTheme.typography.labelSmall.copy(color = MutedText))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Goal editor
                            Text(
                                text = "Daily Goal:",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(1 to "1 lesson", 2 to "2 lessons", 3 to "3 lessons").forEach { (g, label) ->
                                    val isCurrent = p.dailyGoal == g
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isCurrent) CoralAccent else CreamBackground)
                                            .clickable { onUpdateGoal(p.id, g) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isCurrent) CreamSurface else InkText,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Family Settings Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("family_settings_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Family Preferences",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Lesson Sound",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = InkText
                                )
                            )
                            Text(
                                text = "French pronunciation audio and celebrations",
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                            )
                        }
                        Switch(
                            checked = settings.soundEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(soundEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CoralAccent)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Optional Streak Counter",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = InkText
                                )
                            )
                            Text(
                                text = "Gentle encouragement — never shames a missed day",
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                            )
                        }
                        Switch(
                            checked = settings.streakEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(streakEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CoralAccent)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            exportedJsonContent = onExportJson()
                            showExportDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("export_progress_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Export family progress (JSON)")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy Assurance Note
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MintSuccess),
                border = androidx.compose.foundation.BorderStroke(1.dp, MintDark.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MintDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Privacy by Design",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MintDark
                            )
                        )
                        Text(
                            text = "No ads, public chat, child-facing external links, or voice recordings retained on external servers. All learning data is stored locally on this device.",
                            style = MaterialTheme.typography.bodySmall.copy(color = InkText)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Export Dialog
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text(text = "Family Progress Export") },
                text = {
                    Column {
                        Text(
                            text = "Your family learning data in JSON format:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CreamBackground)
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = exportedJsonContent,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Bonjour Bloom Data", exportedJsonContent))
                            Toast.makeText(context, "Progress copied to clipboard!", Toast.LENGTH_SHORT).show()
                            showExportDialog = false
                        }
                    ) {
                        Text("Copy to Clipboard")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
