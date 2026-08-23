package com.example.bonjourbloom.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.bonjourbloom.R
import com.example.bonjourbloom.audio.AudioRecorderService
import com.example.bonjourbloom.audio.RecordingState
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CoralLight
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintDark
import com.example.bonjourbloom.ui.theme.MintSuccess
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary
import kotlinx.coroutines.delay

@Composable
fun PronunciationScreen(
    phrase: String,
    recorder: AudioRecorderService,
    onSpeakFrench: (String, Boolean) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted) {
            recorder.startRecording()
        }
    }

    var recordSeconds by remember { mutableIntStateOf(0) }
    var recordingState by remember { mutableStateOf(recorder.state) }
    val scrollState = rememberScrollState()

    // Timer loop during recording
    LaunchedEffect(recordingState) {
        if (recordingState == RecordingState.RECORDING) {
            recordSeconds = 0
            while (recordSeconds < 12 && recorder.state == RecordingState.RECORDING) {
                delay(1000)
                recordSeconds++
            }
            if (recorder.state == RecordingState.RECORDING) {
                recorder.stopRecording()
                recordingState = recorder.state
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top exit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onFinish,
                modifier = Modifier.testTag("close_pronunciation_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MutedText
                )
            }
            Text(
                text = "MILO’S PRONUNCIATION STUDIO",
                style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent)
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mascot
            Image(
                painter = painterResource(id = R.drawable.ic_milo_fox),
                contentDescription = "Milo the fox",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(3.dp, CoralAccent, CircleShape)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Listen, then try saying:",
                style = MaterialTheme.typography.bodyMedium.copy(color = MutedText)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Expected French Phrase
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expected_phrase_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = phrase.ifBlank { "Bonjour !" },
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = NavyPrimary,
                            textAlign = TextAlign.Center,
                            fontSize = 32.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onSpeakFrench(phrase.ifBlank { "Bonjour !" }, false) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
                            modifier = Modifier.testTag("hear_model_normal")
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hear Milo", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onSpeakFrench(phrase.ifBlank { "Bonjour !" }, true) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("hear_model_slow")
                        ) {
                            Icon(Icons.Default.Hearing, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Slowly", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Big Microphone Record Control
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        if (recordingState == RecordingState.RECORDING) CoralAccent else CreamSurface
                    )
                    .border(
                        3.dp,
                        if (recordingState == RecordingState.RECORDING) CoralAccent else CoralLight,
                        CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (!hasMicPermission) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            if (recordingState == RecordingState.RECORDING) {
                                recorder.stopRecording()
                                recordingState = recorder.state
                            } else {
                                val started = recorder.startRecording()
                                if (started) {
                                    recordingState = RecordingState.RECORDING
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .size(90.dp)
                        .testTag("mic_record_button")
                ) {
                    Icon(
                        imageVector = if (recordingState == RecordingState.RECORDING) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (recordingState == RecordingState.RECORDING) "Stop recording" else "Start recording",
                        tint = if (recordingState == RecordingState.RECORDING) Color.White else CoralAccent,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = when (recordingState) {
                    RecordingState.RECORDING -> "Recording... (${recordSeconds}s / 12s)"
                    RecordingState.RECORDED -> "Recorded! Tap Play to hear your voice."
                    RecordingState.PLAYING -> "Playing your recording..."
                    else -> "Tap the microphone to speak"
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (recordingState == RecordingState.RECORDING) CoralAccent else NavyPrimary
                )
            )

            // Playback controls if recorded
            if (recordingState == RecordingState.RECORDED || recordingState == RecordingState.PLAYING) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            recordingState = RecordingState.PLAYING
                            recorder.playRecording {
                                recordingState = RecordingState.RECORDED
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MintDark),
                        modifier = Modifier.testTag("play_recording_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Play my voice", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Encouragement Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MintSuccess),
                border = androidx.compose.foundation.BorderStroke(1.dp, MintDark.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🦊 Milo says: Brave voice!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MintDark
                        )
                    )
                    Text(
                        text = "Speaking out loud connects language to memory. Keep exploring and having fun!",
                        style = MaterialTheme.typography.bodySmall.copy(color = InkText)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Return button
        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("finish_speaking_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CoralAccent)
        ) {
            Text(
                text = "I’m ready! Back to lesson",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
