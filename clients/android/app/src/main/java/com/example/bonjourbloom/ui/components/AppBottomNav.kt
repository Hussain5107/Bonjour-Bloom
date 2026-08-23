package com.example.bonjourbloom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CoralLight
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary
import com.example.bonjourbloom.ui.viewmodel.AppNavDestination

data class NavItemData(
    val destination: AppNavDestination,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun AppBottomNav(
    currentDestination: AppNavDestination,
    onNavigate: (AppNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItemData(AppNavDestination.HOME, "Home", Icons.Default.Home, "nav_home"),
        NavItemData(AppNavDestination.LEARN, "Learn", Icons.Default.AutoStories, "nav_learn"),
        NavItemData(AppNavDestination.VOICE_CHAT, "Voice", Icons.Default.Mic, "nav_voice"),
        NavItemData(AppNavDestination.TRANSCRIBER, "Transcribe", Icons.Default.GraphicEq, "nav_transcribe"),
        NavItemData(AppNavDestination.WORDS, "Words", Icons.Default.MenuBook, "nav_words"),
        NavItemData(AppNavDestination.PARENT, "Parents", Icons.Default.Shield, "nav_parent")
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, LineBorder)
            .background(CreamBackground),
        containerColor = CreamBackground,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination == item.destination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.destination) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CoralAccent,
                    selectedTextColor = CoralAccent,
                    indicatorColor = CoralLight,
                    unselectedIconColor = MutedText,
                    unselectedTextColor = MutedText
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
