package com.example.bonjourbloom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bonjourbloom.data.model.Profile
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CoralLight
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary

@Composable
fun ProfileSwitcherDialog(
    profiles: List<Profile>,
    activeProfileId: String?,
    onSelectProfile: (String) -> Unit,
    onAddLearner: () -> Unit,
    onOpenParent: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("profile_switcher_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CreamSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WHO’S LEARNING?",
                            style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent)
                        )
                        Text(
                            text = "Switch learner profile",
                            style = MaterialTheme.typography.titleLarge.copy(color = NavyPrimary)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MutedText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile items
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    profiles.forEach { profile ->
                        val isSelected = profile.id == activeProfileId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) CoralLight else CreamBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) CoralAccent else LineBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { onSelectProfile(profile.id) }
                                .padding(12.dp)
                                .testTag("profile_item_${profile.id}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = profile.avatar,
                                fontSize = 28.sp,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(CreamSurface)
                                    .border(1.dp, LineBorder, CircleShape)
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = NavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "${profile.ageBand.label} · ${profile.completedLessons.size} lessons · ${profile.xp} petals",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = CoralAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = LineBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Add another learner button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onAddLearner() }
                        .padding(vertical = 10.dp, horizontal = 12.dp)
                        .testTag("add_learner_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = CoralAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Add another learner",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = CoralAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Grown-ups area button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onOpenParent() }
                        .padding(vertical = 10.dp, horizontal = 12.dp)
                        .testTag("grown_ups_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Grown-ups area",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = NavyPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
