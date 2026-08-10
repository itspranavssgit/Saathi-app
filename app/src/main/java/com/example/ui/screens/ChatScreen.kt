package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CompanionEntity
import com.example.data.local.entities.MessageEntity
import com.example.ui.components.*
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.MintAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    companion: CompanionEntity?,
    messages: List<MessageEntity>,
    isTyping: Boolean,
    onSendMessage: (String) -> Unit,
    onReactionToggle: (MessageEntity, String) -> Unit,
    onRegenerateResponse: (MessageEntity) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onBack: () -> Unit,
    onViewProfile: () -> Unit,
    onViewMemories: () -> Unit
) {
    if (companion == null) return

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var selectedMessageForAction by remember { mutableStateOf<MessageEntity?>(null) }
    var showSafetySheet by remember { mutableStateOf(false) }
    var showAttachmentDialog by remember { mutableStateOf(false) }
    var showVoiceInputDialog by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchChatQuery by remember { mutableStateOf("") }
    var showMenuOverflow by remember { mutableStateOf(false) }

    val filteredMessages = if (isSearchActive && searchChatQuery.isNotBlank()) {
        messages.filter { it.text.contains(searchChatQuery, ignoreCase = true) }
    } else messages

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onViewProfile() }
                        ) {
                            CompanionAvatar(
                                name = companion.name,
                                avatarIconName = companion.avatarIconName,
                                bgColorHex = companion.avatarBgColorHex,
                                size = 40.dp,
                                showOnlineBadge = true
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = companion.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldTeal.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "AI",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldTeal,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${companion.companionType} • Active now",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showVoiceInputDialog = true }) {
                            Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = EmeraldTeal)
                        }
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(Icons.Default.Search, contentDescription = "Search Chat")
                        }
                        Box {
                            IconButton(onClick = { showMenuOverflow = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                            }
                            DropdownMenu(
                                expanded = showMenuOverflow,
                                onDismissRequest = { showMenuOverflow = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("View Companion Profile") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                    onClick = {
                                        showMenuOverflow = false
                                        onViewProfile()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Stored Memories") },
                                    leadingIcon = { Icon(Icons.Default.Psychology, contentDescription = null) },
                                    onClick = {
                                        showMenuOverflow = false
                                        onViewMemories()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Safety & AI Rules") },
                                    leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null) },
                                    onClick = {
                                        showMenuOverflow = false
                                        showSafetySheet = true
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchChatQuery,
                        onValueChange = { searchChatQuery = it },
                        placeholder = { Text("Search messages in this chat...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = {
                                searchChatQuery = ""
                                isSearchActive = false
                            }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                DisclaimerBanner(onClickDisclaimer = { showSafetySheet = true })
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("chat_messages_list")
            ) {
                items(filteredMessages) { msg ->
                    MessageBubble(
                        message = msg,
                        companionName = companion.name,
                        onLongClick = { selectedMessageForAction = msg }
                    )
                }

                if (isTyping) {
                    item {
                        TypingIndicator(companionName = companion.name)
                    }
                }
            }

            // Input Bar
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    IconButton(onClick = { showAttachmentDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attachment",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Message ${companion.name}...") },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field")
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val text = inputText.trim()
                                inputText = ""
                                onSendMessage(text)
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = EmeraldTeal,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("send_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // Message Action Bottom Sheet
    selectedMessageForAction?.let { msg ->
        ModalBottomSheet(
            onDismissRequest = { selectedMessageForAction = null },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Message Actions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Emoji Reaction Row
                Text(text = "React with emoji", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val emojis = listOf("❤️", "😂", "👍", "🔥", "🤗", "💡")
                    emojis.forEach { emoji ->
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable {
                                    onReactionToggle(msg, emoji)
                                    selectedMessageForAction = null
                                }
                                .padding(2.dp)
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Copy Text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Saathi Message", msg.text)
                            clipboard.setPrimaryClip(clip)
                            selectedMessageForAction = null
                        }
                        .padding(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = EmeraldTeal)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Copy Text", fontSize = 15.sp)
                }

                // Regenerate if AI message
                if (msg.senderType == "AI") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onRegenerateResponse(msg)
                                selectedMessageForAction = null
                            }
                            .padding(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = EmeraldTeal)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Regenerate Response", fontSize = 15.sp)
                    }
                }

                // Delete Message
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDeleteMessage(msg.id)
                            selectedMessageForAction = null
                        }
                        .padding(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Delete Message", fontSize = 15.sp, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Attachment Dialog Modal
    if (showAttachmentDialog) {
        AlertDialog(
            onDismissRequest = { showAttachmentDialog = false },
            title = { Text("Share Attachment") },
            text = { Text("Attach a note, photo topic, or study topic for ${companion.name} to discuss!") },
            confirmButton = {
                TextButton(onClick = {
                    onSendMessage("[Attachment: Sharing study notes & project topic]")
                    showAttachmentDialog = false
                }) {
                    Text("Send Sample Note", color = EmeraldTeal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAttachmentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Voice Input Dialog Modal
    if (showVoiceInputDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceInputDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = EmeraldTeal)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Voice Input")
                }
            },
            text = { Text("Simulate speaking to ${companion.name}:") },
            confirmButton = {
                Column {
                    Button(
                        onClick = {
                            onSendMessage("Hey ${companion.name}, kaisa hai sab? Kya kar rahe ho?")
                            showVoiceInputDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("\"Hey ${companion.name}, kaisa hai sab?\"")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onSendMessage("My interview is on Friday, feeling a bit nervous!")
                            showVoiceInputDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("\"My interview is on Friday...\"")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showVoiceInputDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Safety Bottom Sheet Modal
    if (showSafetySheet) {
        SafetyBottomSheet(onDismiss = { showSafetySheet = false })
    }
}
