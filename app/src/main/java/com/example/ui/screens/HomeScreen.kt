package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CompanionEntity
import com.example.data.local.entities.ConversationEntity
import com.example.ui.components.CompanionAvatar
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    conversations: List<ConversationEntity>,
    companions: List<CompanionEntity>,
    onSelectConversation: (String) -> Unit,
    onCreateCompanion: () -> Unit,
    onNavigateToMemories: () -> Unit,
    onNavigateToProactiveSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onViewCompanionProfile: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Friends", "Study", "Special")

    val companionsMap = companions.associateBy { it.id }

    val filteredConversations = conversations.filter { conv ->
        val companion = companionsMap[conv.companionId] ?: return@filter false
        val matchesSearch = companion.name.contains(searchQuery, ignoreCase = true) ||
                companion.companionType.contains(searchQuery, ignoreCase = true) ||
                conv.lastMessageText.contains(searchQuery, ignoreCase = true)

        val matchesCategory = when (selectedCategory) {
            "Friends" -> companion.companionType.contains("Friend", ignoreCase = true)
            "Study" -> companion.companionType.contains("Study", ignoreCase = true)
            "Special" -> companion.companionType.contains("Girlfriend", ignoreCase = true) ||
                    companion.companionType.contains("Boyfriend", ignoreCase = true) ||
                    companion.companionType.contains("Motivational", ignoreCase = true)
            else -> true
        }

        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(UtilityTealPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "S",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Text(
                                text = "Saathi AI",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { searchQuery = if (searchQuery.isEmpty()) " " else "" }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = UtilityTealAccent
                            )
                        }
                        IconButton(onClick = onNavigateToProfile) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, UtilityTealPrimary, CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "US",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UtilityTealAccent
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Search Bar Expandable
                if (searchQuery.isNotEmpty()) {
                    OutlinedTextField(
                        value = if (searchQuery == " ") "" else searchQuery,
                        onValueChange = { searchQuery = if (it.isEmpty()) " " else it },
                        placeholder = { Text("Search companions or messages...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UtilityTealPrimary,
                            unfocusedBorderColor = OutlineLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("search_bar")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Filter Category Chips (Match HTML pill styling)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) UtilityTealLightContainer else MaterialTheme.colorScheme.surface,
                            border = if (!isSelected) BorderStroke(1.dp, OutlineLight) else null,
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) UtilityTealOnContainer else UtilityTealAccent,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Divider(color = SurfaceBorderLight, thickness = 1.dp)
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFFEFF1F1),
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, SurfaceBorderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chats Tab (Active)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = UtilityTealLightContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.ChatBubble,
                                contentDescription = "Chats",
                                tint = UtilityTealOnContainer,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 2.dp)
                                    .size(20.dp)
                            )
                        }
                        Text(
                            text = "CHATS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = UtilityTealOnContainer
                        )
                    }

                    // Memories Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onNavigateToMemories() }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = "Memories",
                            tint = UtilityTealAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "MEMORIES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = UtilityTealAccent
                        )
                    }

                    // Settings / Proactive Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onNavigateToProactiveSettings() }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = UtilityTealAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SETTINGS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = UtilityTealAccent
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateCompanion,
                containerColor = UtilityTealPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("create_companion_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Companion", modifier = Modifier.size(28.dp))
            }
        }
    ) { innerPadding ->
        if (filteredConversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = null,
                        tint = EmeraldTeal.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No companion chats found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap '+' to create your custom AI companion!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("chat_list")
            ) {
                items(filteredConversations) { conv ->
                    val companion = companionsMap[conv.companionId]
                    if (companion != null) {
                        ConversationItem(
                            conversation = conv,
                            companion = companion,
                            onClick = { onSelectConversation(conv.id) },
                            onViewProfile = { onViewCompanionProfile(companion.id) }
                        )
                        Divider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                            modifier = Modifier.padding(start = 76.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: ConversationEntity,
    companion: CompanionEntity,
    onClick: () -> Unit,
    onViewProfile: () -> Unit
) {
    val timeFormatted = try {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(conversation.lastMessageTimestamp))
    } catch (e: Exception) {
        ""
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(modifier = Modifier.clickable { onViewProfile() }) {
            CompanionAvatar(
                name = companion.name,
                avatarIconName = companion.avatarIconName,
                bgColorHex = companion.avatarBgColorHex,
                size = 52.dp,
                showOnlineBadge = true
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = companion.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = companion.companionType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = EmeraldTeal,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = timeFormatted,
                    fontSize = 12.sp,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (conversation.unreadCount > 0) UtilityTealPrimary else UtilityTealAccent
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = conversation.lastMessageText,
                    fontSize = 13.sp,
                    color = UtilityTealAccent,
                    fontStyle = if (conversation.unreadCount > 0) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (conversation.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(UtilityTealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation.unreadCount.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
