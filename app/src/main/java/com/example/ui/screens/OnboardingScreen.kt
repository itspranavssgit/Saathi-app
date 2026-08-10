package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
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
import com.example.ui.theme.MintAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    currentProfile: UserProfileEntity?,
    onSaveProfile: (UserProfileEntity) -> Unit,
    onCompleteOnboarding: () -> Unit
) {
    var name by remember { mutableStateOf(currentProfile?.name ?: "Aarav") }
    var email by remember { mutableStateOf(currentProfile?.email ?: "aarav@saathiai.app") }
    var selectedLanguage by remember { mutableStateOf(currentProfile?.preferredLanguage ?: "Hinglish") }
    var selectedAvatar by remember { mutableStateOf(currentProfile?.avatarIconName ?: "avatar_user_1") }

    val languages = listOf("Hinglish", "English", "Hindi", "Marathi")
    val avatars = listOf("avatar_user_1", "avatar_user_2", "avatar_user_3", "avatar_user_4")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome to Saathi AI", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Set up your profile",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your companions will use this to personalize conversations for you.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar selection
            Text(text = "Choose Avatar", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(avatars) { av ->
                    val isSelected = selectedAvatar == av
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) EmeraldTeal else MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = MintAccent,
                                shape = CircleShape
                            )
                            .clickable { selectedAvatar = av },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_name_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Preferred Language", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                languages.forEach { lang ->
                    FilterChip(
                        selected = selectedLanguage == lang,
                        onClick = { selectedLanguage = lang },
                        label = { Text(lang) },
                        leadingIcon = if (selectedLanguage == lang) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Safety notice card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = EmeraldTeal.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = EmeraldTeal,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Saathi AI companions are AI entities designed to listen and chat. They do not replace real human relationships.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val updated = UserProfileEntity(
                        id = "primary_user",
                        name = name.ifBlank { "Aarav" },
                        email = email.ifBlank { "aarav@saathiai.app" },
                        preferredLanguage = selectedLanguage,
                        avatarIconName = selectedAvatar
                    )
                    onSaveProfile(updated)
                    onCompleteOnboarding()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_chatting_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Start Chatting", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}
