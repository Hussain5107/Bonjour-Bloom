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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintDark
import com.example.bonjourbloom.ui.theme.MintGreen
import com.example.bonjourbloom.ui.theme.MintSuccess
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary

@Composable
fun OfflineScreen(
    isDownloaded: Boolean,
    isOnline: Boolean,
    onToggleDownload: () -> Unit,
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
            text = "DOWNLOADS & STORAGE",
            style = MaterialTheme.typography.labelSmall.copy(
                color = CoralAccent,
                letterSpacing = 1.5.sp
            )
        )
        Text(
            text = "French, wherever you go",
            style = MaterialTheme.typography.headlineLarge.copy(color = NavyPrimary)
        )
        Text(
            text = "All progress and starter lessons stay available on this device even without an internet connection.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MutedText),
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        // Starter path offline card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("offline_pack_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CreamSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MintSuccess),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = MintDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Pre-A1 Starter Path Pack",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                )
                Text(
                    text = "5 lessons · 40 words and phrases · On-device native French text-to-speech fallback",
                    style = MaterialTheme.typography.bodySmall.copy(color = MutedText),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Button(
                    onClick = onToggleDownload,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("toggle_offline_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDownloaded) MintDark else CoralAccent
                    )
                ) {
                    Icon(
                        imageVector = if (isDownloaded) Icons.Default.Check else Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isDownloaded) "Available offline (Installed)" else "Download for offline use",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Connection status card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("connection_status_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CreamSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) MintGreen else CoralAccent)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isOnline) "Device is Online" else "Device is Offline",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                    Text(
                        text = if (isOnline)
                            "Local Room database stores all progress securely."
                        else
                            "Offline mode active. All learning, TTS and practice work smoothly.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                    )
                }
            }
        }
    }
}
