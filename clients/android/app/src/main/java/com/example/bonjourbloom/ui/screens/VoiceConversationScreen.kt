package com.example.bonjourbloom.ui.screens

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.bonjourbloom.gemini.GeminiLiveClient
import com.example.bonjourbloom.gemini.model.VoiceExchange
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CoralDark
import com.example.bonjourbloom.ui.theme.CoralLight
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
import com.example.bonjourbloom.ui.theme.SoftGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

enum class MiloVoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

@Composable
fun VoiceConversationScreen(
    recorder: AudioRecorderService,
    onSpeakFrench: (String, Boolean) -> Unit,
    onAddPetals: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val topics = listOf(
        "👋 Greetings & Names",
        "🥐 At the Bakery",
        "🎨 Colors & Nature",
        "🐾 Animal Friends",
        "💬 Free Chat"
    )
    var selectedTopic by remember { mutableStateOf(topics[0]) }

    var conversationHistory by remember {
        mutableStateOf(
            listOf(
                VoiceExchange(
                    id = "intro-milo",
                    speaker = "milo",
                    frenchText = "Bonjour ! Je m'appelle Milo le renard. Parle-moi en français ou en anglais !",
                    englishTranslation = "Hello! My name is Milo the fox. Speak to me in French or in English!",
                    phonetics = "bon-zhoor! zhuh mah-pel mee-lo luh ruh-nar.",
                    suggestedReplies = listOf("Bonjour Milo !", "Comment vas-tu ?", "Je veux parler français !"),
                    tip = "🦊 Tap the microphone and say 'Bonjour !' to start our voice journey!"
                )
            )
        )
    }

    var miloState by remember { mutableStateOf(MiloVoiceState.IDLE) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var liveAmplitude by remember { mutableFloatStateOf(0f) }
    var textInputText by remember { mutableStateOf("") }
    var showTextInput by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
    }

    // Loop for recording amplitude and time
    LaunchedEffect(miloState) {
        if (miloState == MiloVoiceState.LISTENING) {
            recordingSeconds = 0
            while (miloState == MiloVoiceState.LISTENING) {
                delay(100)
                val amp = recorder.getMaxAmplitude()
                liveAmplitude = (amp / 32767f).coerceIn(0.1f, 1f)
            }
        } else {
            liveAmplitude = 0f
        }
    }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(conversationHistory.size) {
        if (conversationHistory.isNotEmpty()) {
            listState.animateScrollToItem(conversationHistory.size - 1)
        }
    }

    fun submitVoiceTurn() {
        miloState = MiloVoiceState.THINKING
        scope.launch {
            val audioFile = recorder.getCurrentFile()
            val userExchange = VoiceExchange(
                id = UUID.randomUUID().toString(),
                speaker = "user",
                frenchText = "🎤 (Spoken voice message)",
                englishTranslation = "Voice input",
                timestamp = System.currentTimeMillis()
            )
            conversationHistory = conversationHistory + userExchange

            val reply = GeminiLiveClient.converseWithMilo(
                audioFile = audioFile,
                textInput = null,
                history = conversationHistory,
                topic = selectedTopic,
                ageBand = "7–10"
            )

            conversationHistory = conversationHistory + reply
            miloState = MiloVoiceState.SPEAKING
            onAddPetals(5)

            // Speak response via Milo's voice
            onSpeakFrench(reply.frenchText, false)
            delay(3500)
            if (miloState == MiloVoiceState.SPEAKING) {
                miloState = MiloVoiceState.IDLE
            }
        }
    }

    fun submitTextTurn(text: String) {
        if (text.isBlank()) return
        miloState = MiloVoiceState.THINKING
        textInputText = ""
        showTextInput = false

        scope.launch {
            val userExchange = VoiceExchange(
                id = UUID.randomUUID().toString(),
                speaker = "user",
                frenchText = text,
                englishTranslation = "",
                timestamp = System.currentTimeMillis()
            )
            conversationHistory = conversationHistory + userExchange

            val reply = GeminiLiveClient.converseWithMilo(
                audioFile = null,
                textInput = text,
                history = conversationHistory,
                topic = selectedTopic,
                ageBand = "7–10"
            )

            conversationHistory = conversationHistory + reply
            miloState = MiloVoiceState.SPEAKING
            onAddPetals(5)

            onSpeakFrench(reply.frenchText, false)
            delay(3500)
            if (miloState == MiloVoiceState.SPEAKING) {
                miloState = MiloVoiceState.IDLE
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        // Top Bar
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
                    modifier = Modifier.testTag("voice_chat_back_button")
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
                            text = "Milo’s Live Voice Room",
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
                                text = "GEMINI LIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MintDark
                                )
                            )
                        }
                    }
                    Text(
                        text = when (miloState) {
                            MiloVoiceState.LISTENING -> "Listening to your voice..."
                            MiloVoiceState.THINKING -> "Milo is thinking..."
                            MiloVoiceState.SPEAKING -> "Milo is speaking 🦊"
                            MiloVoiceState.IDLE -> "Tap mic to talk in French"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (miloState == MiloVoiceState.LISTENING) CoralAccent else MutedText,
                            fontWeight = if (miloState == MiloVoiceState.LISTENING) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }

            IconButton(
                onClick = {
                    conversationHistory = listOf(
                        VoiceExchange(
                            id = UUID.randomUUID().toString(),
                            speaker = "milo",
                            frenchText = "Coucou ! Nouvelle conversation prête. De quoi veux-tu parler ?",
                            englishTranslation = "Hi there! Fresh conversation ready. What would you like to talk about?",
                            phonetics = "koo-koo! noo-vel kohn-ver-sah-syon preh-t.",
                            suggestedReplies = listOf("Bonjour Milo !", "J'aime les animaux !", "Un croissant s'il te plaît")
                        )
                    )
                },
                modifier = Modifier.testTag("reset_voice_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Clear conversation",
                    tint = MutedText
                )
            }
        }

        // Topic Selector Carousel
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(topics) { topic ->
                val isSelected = selectedTopic == topic
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedTopic = topic
                        submitTextTurn("Let's talk about $topic !")
                    },
                    label = { Text(topic, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CoralAccent,
                        selectedLabelColor = Color.White,
                        containerColor = CreamSurface,
                        labelColor = NavyPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) CoralAccent else LineBorder
                    ),
                    modifier = Modifier.testTag("topic_chip_${topic.take(5)}")
                )
            }
        }

        // Mascot Animated Centerpiece & Waveform Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "miloPulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = if (miloState == MiloVoiceState.LISTENING || miloState == MiloVoiceState.SPEAKING) 1.12f else 1.02f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                NavyPrimary,
                                NavyDark
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Milo Mascot Image with dynamic glow
                Box(contentAlignment = Alignment.Center) {
                    if (miloState != MiloVoiceState.IDLE) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(if (miloState == MiloVoiceState.LISTENING) CoralAccent.copy(alpha = 0.35f) else GoldDark.copy(alpha = 0.35f))
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_milo_fox),
                        contentDescription = "Milo the fox avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(2.dp, CoralAccent, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (miloState) {
                            MiloVoiceState.LISTENING -> "Milo is listening to you..."
                            MiloVoiceState.THINKING -> "Thinking in French..."
                            MiloVoiceState.SPEAKING -> "Milo is speaking!"
                            MiloVoiceState.IDLE -> "Milo’s French Voice Partner"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Audio Amplitude Equalizer Visualizer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(8) { idx ->
                            val heightRatio = if (miloState == MiloVoiceState.LISTENING) {
                                ((liveAmplitude * (idx + 1) * 0.4f).coerceIn(0.2f, 1f))
                            } else if (miloState == MiloVoiceState.SPEAKING) {
                                (0.3f + 0.6f * (idx % 3 == 0).compareTo(false))
                            } else {
                                0.2f
                            }
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height((6 + heightRatio * 18).dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (miloState == MiloVoiceState.LISTENING) CoralAccent
                                        else if (miloState == MiloVoiceState.SPEAKING) GoldDark
                                        else Color.White.copy(alpha = 0.4f)
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (miloState == MiloVoiceState.LISTENING) "Live mic active" else "Ready to converse",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFD7E3EB))
                        )
                    }
                }
            }
        }

        // Conversation List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(conversationHistory) { exchange ->
                VoiceMessageBubble(
                    exchange = exchange,
                    onSpeak = { text, slow -> onSpeakFrench(text, slow) },
                    onSuggestedReplyClick = { reply ->
                        submitTextTurn(reply)
                    }
                )
            }

            if (miloState == MiloVoiceState.THINKING) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = CoralAccent,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Milo is crafting French response...",
                            style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                        )
                    }
                }
            }
        }

        // Quick Suggestion Chips (from latest Milo exchange)
        val latestReplies = conversationHistory.lastOrNull { it.speaker == "milo" }?.suggestedReplies ?: emptyList()
        if (latestReplies.isNotEmpty() && miloState == MiloVoiceState.IDLE) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(latestReplies) { reply ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MintSuccess,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MintDark.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .clickable { submitTextTurn(reply) }
                            .testTag("quick_reply_${reply.take(6)}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💬 ", fontSize = 12.sp)
                            Text(
                                text = reply,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MintDark
                                )
                            )
                        }
                    }
                }
            }
        }

        // Text input fallback tray if toggled
        AnimatedVisibility(visible = showTextInput) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CreamSurface)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInputText,
                    onValueChange = { textInputText = it },
                    placeholder = { Text("Type French or English phrase...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("voice_chat_text_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CreamBackground,
                        unfocusedContainerColor = CreamBackground,
                        focusedBorderColor = CoralAccent,
                        unfocusedBorderColor = LineBorder
                    ),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { submitTextTurn(textInputText) },
                    modifier = Modifier
                        .background(CoralAccent, CircleShape)
                        .testTag("voice_chat_send_text_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom Voice Control Dock
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CreamSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Keyboard icon toggle
                IconButton(
                    onClick = { showTextInput = !showTextInput },
                    modifier = Modifier.testTag("toggle_keyboard_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Toggle text prompt",
                        tint = if (showTextInput) CoralAccent else MutedText
                    )
                }

                // Central Big Mic Button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                if (miloState == MiloVoiceState.LISTENING) CoralAccent else NavyPrimary
                            )
                            .clickable {
                                if (!hasMicPermission) {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    if (miloState == MiloVoiceState.LISTENING) {
                                        recorder.stopRecording()
                                        submitVoiceTurn()
                                    } else {
                                        val started = recorder.startRecording()
                                        if (started) {
                                            miloState = MiloVoiceState.LISTENING
                                        }
                                    }
                                }
                            }
                            .testTag("gemini_live_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (miloState == MiloVoiceState.LISTENING) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (miloState == MiloVoiceState.LISTENING) "Tap to send" else "Hold or Tap to speak",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (miloState == MiloVoiceState.LISTENING) CoralAccent else NavyPrimary
                        )
                    )
                }

                // Petals summary pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CreamBackground)
                        .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🌸 +5 XP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CoralDark
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceMessageBubble(
    exchange: VoiceExchange,
    onSpeak: (String, Boolean) -> Unit,
    onSuggestedReplyClick: (String) -> Unit
) {
    val isMilo = exchange.speaker == "milo"
    var showEnglish by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMilo) Arrangement.Start else Arrangement.End
    ) {
        if (isMilo) {
            Image(
                painter = painterResource(id = R.drawable.ic_milo_fox),
                contentDescription = "Milo",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, CoralAccent, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(if (isMilo) 0.88f else 0.8f),
            horizontalAlignment = if (isMilo) Alignment.Start else Alignment.End
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isMilo) 4.dp else 18.dp,
                    bottomEnd = if (isMilo) 18.dp else 4.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isMilo) CreamSurface else CoralAccent
                ),
                border = if (isMilo) androidx.compose.foundation.BorderStroke(1.dp, LineBorder) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // French text
                    Text(
                        text = exchange.frenchText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isMilo) NavyPrimary else Color.White,
                            fontFamily = if (isMilo) FontFamily.Serif else FontFamily.Default,
                            fontSize = 16.sp
                        )
                    )

                    // Phonetics guide
                    if (exchange.phonetics.isNotBlank() && isMilo) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "[ ${exchange.phonetics} ]",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GoldDark,
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }

                    // English translation toggle
                    if (exchange.englishTranslation.isNotBlank() && isMilo) {
                        Spacer(modifier = Modifier.height(6.dp))
                        if (showEnglish) {
                            Text(
                                text = exchange.englishTranslation,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MutedText,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }

                    // Milo actions (Listen slow / normal)
                    if (isMilo) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onSpeak(exchange.frenchText, false) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hear", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onSpeak(exchange.frenchText, true) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CreamBackground),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Hearing, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Slow", fontSize = 12.sp, color = NavyPrimary, fontWeight = FontWeight.Bold)
                            }

                            TextButton(
                                onClick = { showEnglish = !showEnglish },
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = if (showEnglish) "Hide EN" else "Show EN",
                                    fontSize = 11.sp,
                                    color = CoralDark
                                )
                            }
                        }
                    }

                    // Fox Tip Card
                    if (exchange.tip != null && isMilo) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MintSuccess)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = exchange.tip,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = InkText,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
