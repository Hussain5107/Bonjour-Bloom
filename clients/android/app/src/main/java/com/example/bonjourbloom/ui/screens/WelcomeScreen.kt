package com.example.bonjourbloom.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.R
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintDark
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary

@Composable
fun WelcomeScreen(
    onDone: (pin: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var pin by remember { mutableStateOf("2468") }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Wordmark
        Text(
            text = "• bonjour bloom",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        )
        Text(
            text = "A LITTLE FRENCH, EVERY DAY",
            style = MaterialTheme.typography.labelSmall.copy(
                color = CoralAccent,
                letterSpacing = 2.sp
            ),
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        Text(
            text = "French that grows\nwith your child.",
            style = MaterialTheme.typography.displayMedium.copy(
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )
        )

        Text(
            text = "Short, playful lessons. Kind feedback. A safe space made for curious learners.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MutedText,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
        )

        // Trust badges
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TrustBadge(icon = Icons.Default.Shield, label = "Private by design")
            Spacer(modifier = Modifier.width(12.dp))
            TrustBadge(icon = Icons.Default.Headphones, label = "Audio-first")
            Spacer(modifier = Modifier.width(12.dp))
            TrustBadge(icon = Icons.Default.Sparkles, label = "Ages 5–11+")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("welcome_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CreamSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mascot image
                Image(
                    painter = painterResource(id = R.drawable.ic_milo_fox),
                    contentDescription = "Milo the fox mascot",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(4.dp, CoralAccent, CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (step == 0) {
                    Text(
                        text = "Grown-ups, let’s begin",
                        style = MaterialTheme.typography.headlineSmall.copy(color = NavyPrimary),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You’ll create learner profiles next. All learning progress stays privately on this device.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MutedText),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { step = 1 },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("setup_family_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CoralAccent)
                    ) {
                        Text(
                            text = "Set up our family",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                } else {
                    Text(
                        text = "Choose a parent code",
                        style = MaterialTheme.typography.headlineSmall.copy(color = NavyPrimary),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "4-digit code to protect settings and profile management.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MutedText),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) pin = it },
                        label = { Text("4-digit PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_pin_input"),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Default is 2468. You can change it anytime in Grown-ups area.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { onDone(pin) },
                        enabled = pin.length == 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("continue_welcome_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CoralAccent)
                    ) {
                        Text(
                            text = "Continue to Profile Setup",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TrustBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MintDark,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = InkText
            )
        )
    }
}
