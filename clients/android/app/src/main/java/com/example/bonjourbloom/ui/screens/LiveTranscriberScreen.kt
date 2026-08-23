package com.example.bonjourbloom.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.bonjourbloom.R
import com.example.bonjourbloom.audio.AudioRecorderService
import com.example.bonjourbloom.audio.RecordingState
import com.example.bonjourbloom.data.curriculum.AudioFeedPreset
import com.example.bonjourbloom.data.curriculum.SampleAudioFeeds
import com.example.bonjourbloom.gemini.GeminiLiveClient
import com.example.bonjourbloom.gemini.model.AudioTranscriptionResult
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CoralDark
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.GoldDark
import com.example.bonjourbloom.ui.theme.InkText
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintDark
import com.example.bonjourbloom.ui.theme.MintSuccess
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyDark
import com.example.bonjourbloom.ui.theme.NavyPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LiveTranscriberScreen(
    recorder: AudioRecorderService,
    onSpeakFrench: (String, Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0 = Live Mic Feed, 1 = Preset Audio Feeds
    var selectedPreset by remember { mutableStateOf<AudioFeedPreset?>(SampleAudioFeeds.presets.first()) }

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
    }

    var isLiveTranscribing by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var liveAmplitude by remember { mutableFloatStateOf(0f) }
    var isProcessingGemini by remember { mutableStateOf(false) }

    var transcriptionResult by remember {
        mutableStateOf(
            GeminiLiveClient.generateFallbackTranscription("Bakery dialogue")
        )
    }

    // Dynamic amplitude tracking loop
    LaunchedEffect(isLiveTranscribing) {
        if (isLiveTranscribing) {
            elapsedSeconds = 0
            while (isLiveTranscribing) {
                delay(100)
                elapsedSeconds++
                val amp = recorder.getMaxAmplitude()
                liveAmplitude = (amp / 32767f).coerceIn(0.1f, 1f)
            }
        } else {
            liveAmplitude = 0f
        }
    }

    fun startLiveMicCapture() {
        if (!hasMicPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        val started = recorder.startRecording()
        if (started) {
            isLiveTranscribing = true
        }
    }

    fun stopAndTranscribeMicCapture() {
        isLiveTranscribing = false
        recorder.stopRecording()
        isProcessingGemini = true

        scope.launch {
            val audioFile = recorder.getCurrentFile()
            val audioBytes = if (audioFile != null && audioFile.exists()) audioFile.readBytes() else null

            val result = GeminiLiveClient.transcribeAudioFeed(
                audioBytes = audioBytes,
                sampleContext = "Live microphone French speech practice"
            )
            transcriptionResult = result
            isProcessingGemini = false
        }
    }

    fun transcribePresetFeed(preset: AudioFeedPreset) {
        selectedPreset = preset
        isLiveTranscribing = true
        isProcessingGemini = true

        // Play the speech feed so user hears what is being transcribed!
        onSpeakFrench(preset.sampleTextToSpeak, false)

        scope.launch {
            delay(1200) // Brief delay to simulate live streaming chunk arrival
            val result = GeminiLiveClient.generateFallbackTranscription(preset.title)
            transcriptionResult = result
            isProcessingGemini = false
            isLiveTranscribing = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CreamSurface)
                .border(1.dp, LineBorder)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("transcriber_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NavyPrimary
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Live Audio Transcriber",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MintSuccess)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "REAL-TIME",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MintDark
                                )
                            )
                        }
                    }
                    Text(
                        text = "Instant French audio feed transcription & translation",
                        style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                    )
                }
            }
        }

        // Tabs: Live Microphone vs. Sample Audio Feeds
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = CreamSurface,
            contentColor = CoralAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = CoralAccent
                )
            }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Live Mic Feed", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                },
                modifier = Modifier.testTag("tab_live_mic")
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sample Feeds", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                },
                modifier = Modifier.testTag("tab_sample_feeds")
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preset selector if Tab 1
            if (selectedTabIndex == 1) {
                Text(
                    text = "CHOOSE AUDIO FEED",
                    style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent, letterSpacing = 1.2.sp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SampleAudioFeeds.presets) { preset ->
                        val isSelected = selectedPreset?.id == preset.id
                        Card(
                            modifier = Modifier
                                .width(200.dp)
                                .clickable { transcribePresetFeed(preset) }
                                .testTag("preset_feed_${preset.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) CoralAccent else CreamSurface
                            ),
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, LineBorder) else null
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(preset.icon, fontSize = 22.sp)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) Color.White.copy(alpha = 0.2f) else CreamBackground)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            preset.durationText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MutedText
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = preset.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else NavyPrimary
                                    )
                                )
                                Text(
                                    text = preset.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) Color(0xFFFFECEC) else MutedText,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            // Audio Feed Status & Waveform Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                            val infiniteTransition = rememberInfiniteTransition(label = "recDot")
                            val dotScale by infiniteTransition.animateFloat(
                                initialValue = 0.8f,
                                targetValue = 1.3f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(600, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "dot"
                            )
                            Icon(
                                imageVector = Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                tint = if (isLiveTranscribing) CoralAccent else MintDark,
                                modifier = Modifier
                                    .size(16.dp)
                                    .scale(if (isLiveTranscribing) dotScale else 1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isLiveTranscribing) "TRANSCRIBING LIVE FEED..." else "FEED STREAM READY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLiveTranscribing) CoralAccent else Color(0xFFD7E3EB)
                                )
                            )
                        }

                        Text(
                            text = if (isLiveTranscribing) "${elapsedSeconds / 10}s" else transcriptionResult.detectedLanguage,
                            style = MaterialTheme.typography.labelSmall.copy(color = GoldDark)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Equalizer wave bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(NavyDark, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(24) { i ->
                            val dynamicHeight = if (isLiveTranscribing) {
                                (8 + ((i % 5 + 1) * (liveAmplitude * 28f))).coerceIn(6f, 36f)
                            } else {
                                (6 + (i % 4) * 4).toFloat()
                            }
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(dynamicHeight.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (isLiveTranscribing) CoralAccent else Color.White.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Controls row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedTabIndex == 0) {
                            Button(
                                onClick = {
                                    if (isLiveTranscribing) {
                                        stopAndTranscribeMicCapture()
                                    } else {
                                        startLiveMicCapture()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLiveTranscribing) CoralDark else CoralAccent
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("toggle_mic_transcribe_button")
                            ) {
                                Icon(
                                    imageVector = if (isLiveTranscribing) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isLiveTranscribing) "Stop & Transcribe" else "Listen Live Feed",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    selectedPreset?.let { transcribePresetFeed(it) }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("play_transcribe_preset_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Play & Transcribe Feed", fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    onSpeakFrench(transcriptionResult.fullText, false)
                                },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                    .testTag("hear_full_transcription_button")
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Hear full", tint = Color.White)
                            }

                            IconButton(
                                onClick = {
                                    onSpeakFrench(transcriptionResult.fullText, true)
                                },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                    .testTag("hear_slow_transcription_button")
                            ) {
                                Icon(Icons.Default.Hearing, contentDescription = "Hear slowly", tint = Color.White)
                            }
                        }
                    }
                }
            }

            if (isProcessingGemini) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        CircularProgressIndicator(
                            color = CoralAccent,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Transcribing live audio feed with Gemini...",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            )
                            Text(
                                text = "Analyzing phonetic stress, language confidence & English alignment",
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                            )
                        }
                    }
                }
            }

            // Real-Time French Transcription Main Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("french_transcription_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CreamSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FRENCH TRANSCRIPTION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CoralAccent,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Row {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(transcriptionResult.fullText))
                                    Toast.makeText(context, "Copied French transcript", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = MutedText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = transcriptionResult.fullText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp
                        )
                    )

                    if (transcriptionResult.phonetics.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "/ ${transcriptionResult.phonetics} /",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GoldDark,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(LineBorder)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "ENGLISH TRANSLATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MintDark,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = transcriptionResult.englishTranslation,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = InkText,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Detected Keywords Chips
            if (transcriptionResult.detectedKeywords.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DETECTED VOCABULARY IN AUDIO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NavyPrimary,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(transcriptionResult.detectedKeywords) { word ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MintSuccess,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MintDark.copy(alpha = 0.3f)),
                                    modifier = Modifier.clickable {
                                        onSpeakFrench(word.french, false)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(word.icon, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = word.french,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = NavyPrimary
                                                )
                                            )
                                            Text(
                                                text = word.english,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    color = MintDark
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Timestamped Segments Breakdown
            if (transcriptionResult.segments.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "LIVE AUDIO SEGMENTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NavyPrimary,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            transcriptionResult.segments.forEach { seg ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CreamBackground, RoundedCornerShape(10.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NavyPrimary)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = seg.timestamp,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = seg.frenchText,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = NavyPrimary
                                            )
                                        )
                                        Text(
                                            text = seg.englishText,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MutedText
                                            )
                                        )
                                    }
                                    IconButton(
                                        onClick = { onSpeakFrench(seg.frenchText, false) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Play segment",
                                            tint = CoralAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tutor Accent & Pronunciation Intelligence
            if (transcriptionResult.pronunciationTip.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MintSuccess),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MintDark.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_milo_fox),
                            contentDescription = "Milo Fox",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, CoralAccent, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🦊 Milo’s Pronunciation Insight",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MintDark
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = transcriptionResult.pronunciationTip,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = InkText,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
