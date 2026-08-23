package com.example.bonjourbloom.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.R
import com.example.bonjourbloom.data.model.Profile
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MintGreen
import com.example.bonjourbloom.ui.theme.NavyPrimary

@Composable
fun AppTopBar(
    profile: Profile?,
    isOnline: Boolean,
    onBrandClick: () -> Unit,
    onStatusClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CreamBackground)
            .border(width = 1.dp, color = LineBorder)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand logo
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onBrandClick() }
                .padding(4.dp)
                .testTag("brand_logo_button"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_milo_fox),
                contentDescription = "Milo the fox mascot",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, CoralAccent, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buildAnnotatedString {
                    append("bonjour ")
                    withStyle(style = SpanStyle(color = CoralAccent, fontWeight = FontWeight.Bold)) {
                        append("bloom")
                    }
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp,
                    color = NavyPrimary
                )
            )
        }

        // Action chips
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Online status pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(CreamSurface)
                    .border(1.dp, LineBorder, RoundedCornerShape(99.dp))
                    .clickable { onStatusClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("status_pill"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.CloudOff,
                    contentDescription = if (isOnline) "Online" else "Offline",
                    tint = if (isOnline) MintGreen else Color.Gray,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isOnline) "Online" else "Offline",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOnline) MintGreen else Color.Gray
                )
            }

            // Profile chip
            if (profile != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(CreamSurface)
                        .border(1.dp, LineBorder, RoundedCornerShape(99.dp))
                        .clickable { onProfileClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("profile_chip"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = profile.avatar,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Switch profile",
                        tint = NavyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
