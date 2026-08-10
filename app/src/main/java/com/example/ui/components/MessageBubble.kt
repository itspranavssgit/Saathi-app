package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.MessageEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: MessageEntity,
    companionName: String,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.senderType == "USER"
    val darkTheme = isSystemInDarkTheme()

    val timeFormatted = try {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    } catch (e: Exception) {
        ""
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 12.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            val bubbleShape = if (isUser) {
                RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
            } else {
                RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
            }

            val bubbleBg = if (isUser) {
                if (darkTheme) UserBubbleDark else UserBubbleLight
            } else {
                if (darkTheme) AiBubbleDark else AiBubbleLight
            }

            val textColor = if (isUser) {
                if (darkTheme) UserBubbleTextDark else UserBubbleTextLight
            } else {
                if (darkTheme) AiBubbleTextDark else AiBubbleTextLight
            }

            Surface(
                shape = bubbleShape,
                color = bubbleBg,
                tonalElevation = 2.dp,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .testTag("message_bubble_${message.id}")
                    .clip(bubbleShape)
                    .combinedClickable(
                        onClick = { },
                        onLongClick = onLongClick
                    )
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "AI Tag",
                                tint = EmeraldTeal,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$companionName • AI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldTeal
                            )
                        }
                    }

                    Text(
                        text = message.text,
                        fontSize = 15.sp,
                        color = textColor,
                        lineHeight = 21.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            text = timeFormatted,
                            fontSize = 10.sp,
                            color = textColor.copy(alpha = 0.65f)
                        )

                        if (isUser) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Read",
                                tint = SoftCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Emoji Reaction Badge
            if (!message.reactionEmoji.isNullOrEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .offset(y = (-8).dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = message.reactionEmoji,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
