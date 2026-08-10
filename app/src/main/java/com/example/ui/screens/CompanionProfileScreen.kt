package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CompanionEntity
import com.example.data.local.entities.MemoryEntity
import com.example.ui.components.CompanionAvatar
import com.example.ui.theme.EmeraldTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionProfileScreen(
    companion: CompanionEntity?,
    memories: List<MemoryEntity>,
    onEditCompanion: () -> Unit,
    onTriggerTestProactive: () -> Unit,
    onDeleteCompanion: () -> Unit,
    onBack: () -> Unit
) {
    if (companion == null) return

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text(text = "${companion.name}'s Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onEditCompanion) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Companion", tint = EmeraldTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        CompanionAvatar(
                            name = companion.name,
                            avatarIconName = companion.avatarIconName,
                            bgColorHex = companion.avatarBgColorHex,
                            size = 80.dp,
                            showOnlineBadge = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = companion.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = companion.companionType, fontSize = 14.sp, color = EmeraldTeal, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(onClick = {}, label = { Text("Lang: ${companion.language}") })
                            AssistChip(onClick = {}, label = { Text("Style: ${companion.communicationStyle}") })
                        }
                    }
                }
            }

            // Relationship Familiarity Score
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldTeal.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Relationship Familiarity", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${companion.relationshipFamiliarity}%", fontWeight = FontWeight.Bold, color = EmeraldTeal)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { companion.relationshipFamiliarity / 100f },
                            color = EmeraldTeal,
                            trackColor = EmeraldTeal.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Personality Traits Summary
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Personality Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        TraitBar("Humor", companion.humorLevel)
                        TraitBar("Empathy", companion.empathyLevel)
                        TraitBar("Curiosity", companion.curiosityLevel)
                        TraitBar("Playfulness", companion.playfulnessLevel)
                        TraitBar("Supportiveness", companion.supportivenessLevel)
                    }
                }
            }

            // Test Proactive Button
            item {
                Button(
                    onClick = onTriggerTestProactive,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger Sample Proactive Message")
                }
            }

            // Stored Memories Section
            item {
                Text("Stored Memories (${memories.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            if (memories.isEmpty()) {
                item {
                    Text("No long-term memories recorded yet for this companion.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(memories) { mem ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = EmeraldTeal)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(mem.category.replace("_", " ").uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldTeal)
                                Text(mem.memoryText, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Delete Companion Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Companion")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete ${companion.name}?") },
            text = { Text("Are you sure you want to delete this companion? All conversation history and memories will be permanently removed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteCompanion()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TraitBar(label: String, value: Int) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(label, fontSize = 13.sp)
            Text("$value%", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { value / 100f },
            color = EmeraldTeal,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )
    }
}
