package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CompanionEntity
import com.example.data.local.entities.UserProfileEntity
import com.example.ui.theme.EmeraldTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProactiveSettingsScreen(
    userProfile: UserProfileEntity?,
    companions: List<CompanionEntity>,
    onUpdateProfile: (UserProfileEntity) -> Unit,
    onTriggerProactiveMessage: (String, String) -> Unit,
    onBack: () -> Unit
) {
    if (userProfile == null) return

    var globalEnabled by remember { mutableStateOf(userProfile.enableGlobalProactive) }
    var quietStart by remember { mutableStateOf(userProfile.globalQuietHoursStart) }
    var quietEnd by remember { mutableStateOf(userProfile.globalQuietHoursEnd) }
    var maxDaily by remember { mutableStateOf(userProfile.maxDailyProactiveMessages) }

    var selectedCompanionForTest by remember { mutableStateOf(companions.firstOrNull()?.id ?: "") }
    var testTriggerType by remember { mutableStateOf("MORNING_GREETING") }
    var lastTriggeredMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Proactive Messaging Settings", fontWeight = FontWeight.Bold) },
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
            Text(
                text = "Controlled Proactive AI Messages",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "With your explicit permission, companions can initiate friendly morning greetings or follow up after important events.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Global Master Toggle
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
                        Text("Enable Proactive Messages", fontWeight = FontWeight.SemiBold)
                        Text("Global permission toggle", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = globalEnabled,
                        onCheckedChange = {
                            globalEnabled = it
                            onUpdateProfile(userProfile.copy(enableGlobalProactive = it))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldTeal)
                    )
                }
            }

            if (globalEnabled) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Daily Message Limits", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("Maximum proactive messages per day across all companions: $maxDaily", fontSize = 13.sp)

                        Slider(
                            value = maxDaily.toFloat(),
                            onValueChange = {
                                maxDaily = it.toInt()
                                onUpdateProfile(userProfile.copy(maxDailyProactiveMessages = maxDaily))
                            },
                            valueRange = 1f..5f,
                            steps = 3,
                            colors = SliderDefaults.colors(thumbColor = EmeraldTeal, activeTrackColor = EmeraldTeal)
                        )

                        Divider()

                        Text("Quiet Hours (No notifications sent)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = quietStart,
                                onValueChange = {
                                    quietStart = it
                                    onUpdateProfile(userProfile.copy(globalQuietHoursStart = quietStart))
                                },
                                label = { Text("Start Time") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = quietEnd,
                                onValueChange = {
                                    quietEnd = it
                                    onUpdateProfile(userProfile.copy(globalQuietHoursEnd = quietEnd))
                                },
                                label = { Text("End Time") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Test Proactive Trigger Section
            Text("Proactive Message Trigger Tester", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select Trigger Scenario:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = testTriggerType == "MORNING_GREETING",
                            onClick = { testTriggerType = "MORNING_GREETING" },
                            label = { Text("Morning") }
                        )
                        FilterChip(
                            selected = testTriggerType == "EVENT_CHECKIN",
                            onClick = { testTriggerType = "EVENT_CHECKIN" },
                            label = { Text("Event Check-in") }
                        )
                        FilterChip(
                            selected = testTriggerType == "INACTIVITY_CHECKIN",
                            onClick = { testTriggerType = "INACTIVITY_CHECKIN" },
                            label = { Text("Inactivity") }
                        )
                    }

                    Button(
                        onClick = {
                            if (selectedCompanionForTest.isNotBlank()) {
                                onTriggerProactiveMessage(selectedCompanionForTest, testTriggerType)
                                lastTriggeredMessage = "Successfully triggered $testTriggerType message!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trigger_proactive_test_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Trigger Test Proactive Message Now")
                    }

                    if (lastTriggeredMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldTeal.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = lastTriggeredMessage!!,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = EmeraldTeal,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
