package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.SaathiAITheme
import com.example.ui.viewmodel.SaathiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaathiAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: SaathiViewModel = viewModel()
                    val navController = rememberNavController()

                    val companions by viewModel.companions.collectAsState()
                    val conversations by viewModel.conversations.collectAsState()
                    val userProfile by viewModel.userProfile.collectAsState()
                    val allMemories by viewModel.allMemories.collectAsState()

                    val activeCompanion by viewModel.activeCompanion.collectAsState()
                    val activeMessages by viewModel.activeMessages.collectAsState()
                    val activeCompanionMemories by viewModel.activeCompanionMemories.collectAsState()
                    val isTyping by viewModel.isTyping.collectAsState()

                    val companionCount by viewModel.companionCount.collectAsState()
                    val conversationCount by viewModel.conversationCount.collectAsState()
                    val totalMessageCount by viewModel.totalMessageCount.collectAsState()
                    val activeMemoryCount by viewModel.activeMemoryCount.collectAsState()
                    val sentProactiveCount by viewModel.sentProactiveCount.collectAsState()

                    var editingCompanionId by remember { mutableStateOf<String?>(null) }

                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("onboarding") {
                            OnboardingScreen(
                                currentProfile = userProfile,
                                onSaveProfile = { viewModel.updateUserProfile(it) },
                                onCompleteOnboarding = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                conversations = conversations,
                                companions = companions,
                                onSelectConversation = { convId ->
                                    viewModel.selectConversation(convId)
                                    navController.navigate("chat")
                                },
                                onCreateCompanion = {
                                    editingCompanionId = null
                                    navController.navigate("create_companion")
                                },
                                onNavigateToMemories = { navController.navigate("memories") },
                                onNavigateToProactiveSettings = { navController.navigate("proactive_settings") },
                                onNavigateToProfile = { navController.navigate("user_profile") },
                                onNavigateToDashboard = { navController.navigate("dashboard") },
                                onViewCompanionProfile = { compId ->
                                    viewModel.selectCompanion(compId)
                                    navController.navigate("companion_profile/$compId")
                                }
                            )
                        }

                        composable("chat") {
                            ChatScreen(
                                companion = activeCompanion,
                                messages = activeMessages,
                                isTyping = isTyping,
                                onSendMessage = { text -> viewModel.sendMessage(text) },
                                onReactionToggle = { msg, emoji -> viewModel.toggleReaction(msg, emoji) },
                                onRegenerateResponse = { msg -> viewModel.regenerateResponse(msg) },
                                onDeleteMessage = { msgId -> viewModel.deleteMessage(msgId) },
                                onBack = { navController.popBackStack() },
                                onViewProfile = {
                                    activeCompanion?.let { comp ->
                                        navController.navigate("companion_profile/${comp.id}")
                                    }
                                },
                                onViewMemories = { navController.navigate("memories") }
                            )
                        }

                        composable("create_companion") {
                            val companionToEdit = companions.find { it.id == editingCompanionId }
                            CreateCompanionScreen(
                                initialCompanion = companionToEdit,
                                onSaveCompanion = { comp ->
                                    viewModel.saveCompanion(comp)
                                    navController.navigate("chat") {
                                        popUpTo("create_companion") { inclusive = true }
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("companion_profile/{companionId}") { backStackEntry ->
                            val compId = backStackEntry.arguments?.getString("companionId") ?: ""
                            val companion = companions.find { it.id == compId }
                            val companionMemories = allMemories.filter { it.companionId == compId }

                            CompanionProfileScreen(
                                companion = companion,
                                memories = companionMemories,
                                onEditCompanion = {
                                    editingCompanionId = compId
                                    navController.navigate("create_companion")
                                },
                                onTriggerTestProactive = {
                                    viewModel.triggerProactiveMessage(compId, "EVENT_CHECKIN")
                                },
                                onDeleteCompanion = {
                                    viewModel.deleteCompanion(compId)
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("memories") {
                            MemoryManagerScreen(
                                memories = allMemories,
                                companions = companions,
                                onSaveMemory = { mem -> viewModel.saveMemory(mem) },
                                onDeleteMemory = { memId -> viewModel.deleteMemory(memId) },
                                onClearAllMemories = { viewModel.clearAllMemories() },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("proactive_settings") {
                            ProactiveSettingsScreen(
                                userProfile = userProfile,
                                companions = companions,
                                onUpdateProfile = { prof -> viewModel.updateUserProfile(prof) },
                                onTriggerProactiveMessage = { compId, triggerType ->
                                    viewModel.triggerProactiveMessage(compId, triggerType)
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("user_profile") {
                            UserProfileScreen(
                                userProfile = userProfile,
                                onSaveProfile = { prof -> viewModel.updateUserProfile(prof) },
                                onClearAllMemories = { viewModel.clearAllMemories() },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("dashboard") {
                            DeveloperDashboardScreen(
                                companionCount = companionCount,
                                conversationCount = conversationCount,
                                totalMessageCount = totalMessageCount,
                                activeMemoryCount = activeMemoryCount,
                                sentProactiveCount = sentProactiveCount,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
