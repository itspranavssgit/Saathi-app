package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CompanionAvatar(
    name: String,
    avatarIconName: String,
    bgColorHex: String,
    size: Dp = 52.dp,
    showOnlineBadge: Boolean = true,
    showBorder: Boolean = false,
    useInitialText: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (defaultBg, defaultText) = when (name.firstOrNull()?.uppercaseChar()) {
        'A' -> AvatarPeachBg to AvatarPeachText
        'R' -> AvatarBlueBg to AvatarBlueText
        'P' -> AvatarPurpleBg to AvatarPurpleText
        else -> UtilityTealLightContainer to UtilityTealOnContainer
    }

    val containerBg = try {
        if (bgColorHex.isNotEmpty() && bgColorHex != "#006A6A" && bgColorHex != "#0D9488") {
            Color(android.graphics.Color.parseColor(bgColorHex))
        } else defaultBg
    } catch (e: Exception) {
        defaultBg
    }

    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (showBorder) Modifier.border(2.dp, UtilityTealPrimary, shape)
                    else Modifier
                )
                .clip(shape)
                .background(containerBg),
            contentAlignment = Alignment.Center
        ) {
            val firstInitial = name.firstOrNull()?.uppercase() ?: "S"

            if (useInitialText) {
                Text(
                    text = firstInitial,
                    fontSize = (size.value * 0.42f).sp,
                    fontWeight = FontWeight.Bold,
                    color = defaultText
                )
            } else {
                val icon = when (avatarIconName.lowercase()) {
                    "aanya" -> Icons.Default.Face3
                    "rahul" -> Icons.Default.Face6
                    "priya" -> Icons.Default.Face2
                    "study" -> Icons.Default.School
                    "gaming" -> Icons.Default.SportsEsports
                    "motivational" -> Icons.Default.Bolt
                    else -> null
                }

                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = defaultText,
                        modifier = Modifier.size(size * 0.52f)
                    )
                } else {
                    Text(
                        text = firstInitial,
                        fontSize = (size.value * 0.42f).sp,
                        fontWeight = FontWeight.Bold,
                        color = defaultText
                    )
                }
            }
        }

        if (showOnlineBadge) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(OnlineBadgeColor)
            )
        }
    }
}

