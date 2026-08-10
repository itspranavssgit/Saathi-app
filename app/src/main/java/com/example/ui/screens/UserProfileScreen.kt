package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UserProfileEntity
import com.example.ui.theme.EmeraldTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userProfile: UserProfileEntity?,
    onSaveProfile: (UserProfileEntity) -> Unit,
    onClearAllMemories: () -> Unit,
    onBack: () -> Unit
) {
    if (userProfile == null) return

    var name by remember { mutableStateOf(userProfile.name) }
    var email by remember { mutableStateOf(userProfile.email) }
    var language by remember { mutableStateOf(userProfile.preferredLanguage) }
    var bio by remember { mutableStateOf(userProfile.bio) }
    var enableMemorySystem by remember { mutableStateOf(userProfile.enableMemorySystem) }

    var showClearMemoriesDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("User Profile & Privacy", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Header
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(EmeraldTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = email, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text("Profile Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("About / Bio") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    onSaveProfile(
                        userProfile.copy(
                            name = name,
                            email = email,
                            bio = bio,
                            preferredLanguage = language,
                            enableMemorySystem = enableMemorySystem
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Profile Changes")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Privacy & Memory Controls
            Text("Privacy & Memory Controls", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Persistent Memory System", fontWeight = FontWeight.SemiBold)
                        Text("Allows companions to extract & recall key facts", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = enableMemorySystem,
                        onCheckedChange = {
                            enableMemorySystem = it
                            onSaveProfile(userProfile.copy(enableMemorySystem = it))
                        }
                    )
                }
            }

            OutlinedButton(
                onClick = { showClearMemoriesDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear All Long-Term Memories")
            }
        }
    }

    if (showClearMemoriesDialog) {
        AlertDialog(
            onDismissRequest = { showClearMemoriesDialog = false },
            title = { Text("Clear All Memories?") },
            text = { Text("This will erase all stored facts across all companions.") },
            confirmButton = {
                TextButton(onClick = {
                    onClearAllMemories()
                    showClearMemoriesDialog = false
                }) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearMemoriesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
