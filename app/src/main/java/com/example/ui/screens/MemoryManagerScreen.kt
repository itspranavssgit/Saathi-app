package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CompanionEntity
import com.example.data.local.entities.MemoryEntity
import com.example.ui.theme.EmeraldTeal
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryManagerScreen(
    memories: List<MemoryEntity>,
    companions: List<CompanionEntity>,
    onSaveMemory: (MemoryEntity) -> Unit,
    onDeleteMemory: (String) -> Unit,
    onClearAllMemories: () -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var editingMemory by remember { mutableStateOf<MemoryEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "important_events", "personal_info", "interests", "goals", "preferences")

    val companionsMap = companions.associateBy { it.id }

    val filteredMemories = memories.filter { mem ->
        val matchesSearch = mem.memoryText.contains(searchQuery, ignoreCase = true) ||
                mem.category.contains(searchQuery, ignoreCase = true)
        val matchesCat = if (selectedCategory == "All") true else mem.category == selectedCategory
        matchesSearch && matchesCat
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Memory Management", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showClearAllDialog = true }) {
                        Icon(Icons.Default.DeleteForever, contentDescription = "Clear All", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldTeal,
                contentColor = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.testTag("add_memory_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Memory")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search stored memories...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("memory_search_bar")
            )

            // Category Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.replace("_", " ").replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            if (filteredMemories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = EmeraldTeal.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No memories found", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Companions automatically store key facts during conversation.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredMemories) { mem ->
                        val companion = companionsMap[mem.companionId]
                        MemoryCard(
                            memory = mem,
                            companionName = companion?.name ?: "All Companions",
                            onToggleEnabled = {
                                onSaveMemory(mem.copy(isEnabled = !mem.isEnabled))
                            },
                            onEdit = { editingMemory = mem },
                            onDelete = { onDeleteMemory(mem.id) }
                        )
                    }
                }
            }
        }
    }

    // Edit Memory Dialog
    editingMemory?.let { mem ->
        var editText by remember { mutableStateOf(mem.memoryText) }
        AlertDialog(
            onDismissRequest = { editingMemory = null },
            title = { Text("Edit Memory") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    label = { Text("Memory Fact") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onSaveMemory(mem.copy(memoryText = editText))
                    editingMemory = null
                }) {
                    Text("Save", color = EmeraldTeal)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMemory = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Memory Dialog
    if (showAddDialog) {
        var newText by remember { mutableStateOf("") }
        var newCat by remember { mutableStateOf("important_events") }
        var selectedCompId by remember { mutableStateOf(companions.firstOrNull()?.id ?: "") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Memory") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newText,
                        onValueChange = { newText = it },
                        label = { Text("Memory Detail") },
                        placeholder = { Text("e.g. User has an interview on Friday") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("important_events", "personal_info", "interests").forEach { c ->
                            FilterChip(
                                selected = newCat == c,
                                onClick = { newCat = c },
                                label = { Text(c.replace("_", " ")) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newText.isNotBlank() && selectedCompId.isNotBlank()) {
                        val newMem = MemoryEntity(
                            id = UUID.randomUUID().toString(),
                            companionId = selectedCompId,
                            category = newCat,
                            memoryText = newText,
                            importance = "HIGH"
                        )
                        onSaveMemory(newMem)
                    }
                    showAddDialog = false
                }) {
                    Text("Add", color = EmeraldTeal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear All Dialog
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("Clear All Memories?") },
            text = { Text("Are you sure you want to permanently delete all stored companion memories? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    onClearAllMemories()
                    showClearAllDialog = false
                }) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MemoryCard(
    memory: MemoryEntity,
    companionName: String,
    onToggleEnabled: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = EmeraldTeal, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = memory.category.replace("_", " ").uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldTeal
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• $companionName",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = memory.isEnabled,
                    onCheckedChange = { onToggleEnabled() },
                    modifier = Modifier.scale(0.8f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = memory.memoryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (memory.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EmeraldTeal, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
