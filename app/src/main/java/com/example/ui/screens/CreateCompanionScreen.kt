package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.local.entities.CompanionEntity
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.MintAccent
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCompanionScreen(
    initialCompanion: CompanionEntity? = null,
    onSaveCompanion: (CompanionEntity) -> Unit,
    onBack: () -> Unit
) {
    var step by remember { mutableStateOf(1) }

    // Form States
    var companionType by remember { mutableStateOf(initialCompanion?.companionType ?: "Female Friend") }
    var name by remember { mutableStateOf(initialCompanion?.name ?: "") }
    var avatarIconName by remember { mutableStateOf(initialCompanion?.avatarIconName ?: "aanya") }
    var avatarBgColorHex by remember { mutableStateOf(initialCompanion?.avatarBgColorHex ?: "#0D9488") }
    var agePersonaRange by remember { mutableStateOf(initialCompanion?.agePersonaRange ?: "22-25") }
    var language by remember { mutableStateOf(initialCompanion?.language ?: "Hinglish") }
    var communicationStyle by remember { mutableStateOf(initialCompanion?.communicationStyle ?: "Caring") }

    var humorLevel by remember { mutableFloatStateOf(initialCompanion?.humorLevel?.toFloat() ?: 70f) }
    var empathyLevel by remember { mutableFloatStateOf(initialCompanion?.empathyLevel?.toFloat() ?: 85f) }
    var curiosityLevel by remember { mutableFloatStateOf(initialCompanion?.curiosityLevel?.toFloat() ?: 75f) }
    var playfulnessLevel by remember { mutableFloatStateOf(initialCompanion?.playfulnessLevel?.toFloat() ?: 60f) }
    var seriousnessLevel by remember { mutableFloatStateOf(initialCompanion?.seriousnessLevel?.toFloat() ?: 40f) }
    var energyLevel by remember { mutableFloatStateOf(initialCompanion?.energyLevel?.toFloat() ?: 80f) }
    var supportivenessLevel by remember { mutableFloatStateOf(initialCompanion?.supportivenessLevel?.toFloat() ?: 90f) }

    val defaultInterests = listOf("Movies", "Music", "Cricket", "Gaming", "Coding", "College", "Travel", "Books", "Fitness")
    var selectedInterests by remember {
        mutableStateOf(
            initialCompanion?.interestsCsv?.split(",")?.map { it.trim() }?.toSet() ?: setOf("Movies", "Music", "Cricket")
        )
    }

    var proactiveEnabled by remember { mutableStateOf(initialCompanion?.proactiveEnabled ?: true) }
    var maxMessagesPerDay by remember { mutableStateOf(initialCompanion?.maxMessagesPerDay ?: 3) }
    var quietHoursStart by remember { mutableStateOf(initialCompanion?.quietHoursStart ?: "22:00") }
    var quietHoursEnd by remember { mutableStateOf(initialCompanion?.quietHoursEnd ?: "08:00") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        if (step > 1) step-- else onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text = if (initialCompanion != null) "Edit Companion" else "Create AI Companion",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Step Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step $step of 5",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldTeal
                )
                Text(
                    text = when (step) {
                        1 -> "Choose Type"
                        2 -> "Name & Identity"
                        3 -> "Personality Trait Sliders"
                        4 -> "Interests & Topics"
                        else -> "Proactive Settings"
                    },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LinearProgressIndicator(
                progress = { step / 5f },
                modifier = Modifier.fillMaxWidth(),
                color = EmeraldTeal,
                trackColor = EmeraldTeal.copy(alpha = 0.2f)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                when (step) {
                    1 -> Step1ChooseType(companionType) { companionType = it }
                    2 -> Step2Identity(
                        name = name,
                        onNameChange = { name = it },
                        avatarIconName = avatarIconName,
                        onAvatarIconChange = { avatarIconName = it },
                        avatarBgColorHex = avatarBgColorHex,
                        onBgColorChange = { avatarBgColorHex = it },
                        agePersonaRange = agePersonaRange,
                        onAgeChange = { agePersonaRange = it },
                        language = language,
                        onLanguageChange = { language = it },
                        communicationStyle = communicationStyle,
                        onStyleChange = { communicationStyle = it }
                    )
                    3 -> Step3Sliders(
                        humor = humorLevel, onHumorChange = { humorLevel = it },
                        empathy = empathyLevel, onEmpathyChange = { empathyLevel = it },
                        curiosity = curiosityLevel, onCuriosityChange = { curiosityLevel = it },
                        playfulness = playfulnessLevel, onPlayfulnessChange = { playfulnessLevel = it },
                        seriousness = seriousnessLevel, onSeriousnessChange = { seriousnessLevel = it },
                        energy = energyLevel, onEnergyChange = { energyLevel = it },
                        supportiveness = supportivenessLevel, onSupportivenessChange = { supportivenessLevel = it }
                    )
                    4 -> Step4Interests(
                        availableInterests = defaultInterests,
                        selectedInterests = selectedInterests,
                        onToggleInterest = { interest ->
                            selectedInterests = if (selectedInterests.contains(interest)) {
                                selectedInterests - interest
                            } else {
                                selectedInterests + interest
                            }
                        }
                    )
                    5 -> Step5Proactive(
                        proactiveEnabled = proactiveEnabled,
                        onToggleProactive = { proactiveEnabled = it },
                        maxMessagesPerDay = maxMessagesPerDay,
                        onMaxMessagesChange = { maxMessagesPerDay = it },
                        quietHoursStart = quietHoursStart,
                        onQuietStartChange = { quietHoursStart = it },
                        quietHoursEnd = quietHoursEnd,
                        onQuietEndChange = { quietHoursEnd = it }
                    )
                }
            }

            // Bottom Next / Save Button
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (step < 5) {
                                if (step == 2 && name.isBlank()) {
                                    name = "Saathi Companion"
                                }
                                step++
                            } else {
                                val finalName = name.ifBlank { "Saathi Companion" }
                                val companion = CompanionEntity(
                                    id = initialCompanion?.id ?: "comp_${UUID.randomUUID().toString().take(8)}",
                                    name = finalName,
                                    companionType = companionType,
                                    agePersonaRange = agePersonaRange,
                                    avatarIconName = avatarIconName,
                                    avatarBgColorHex = avatarBgColorHex,
                                    language = language,
                                    communicationStyle = communicationStyle,
                                    humorLevel = humorLevel.toInt(),
                                    empathyLevel = empathyLevel.toInt(),
                                    curiosityLevel = curiosityLevel.toInt(),
                                    playfulnessLevel = playfulnessLevel.toInt(),
                                    seriousnessLevel = seriousnessLevel.toInt(),
                                    energyLevel = energyLevel.toInt(),
                                    supportivenessLevel = supportivenessLevel.toInt(),
                                    interestsCsv = selectedInterests.joinToString(", "),
                                    proactiveEnabled = proactiveEnabled,
                                    maxMessagesPerDay = maxMessagesPerDay,
                                    quietHoursStart = quietHoursStart,
                                    quietHoursEnd = quietHoursEnd
                                )
                                onSaveCompanion(companion)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("companion_next_save_button")
                    ) {
                        Text(
                            text = if (step == 5) "Save & Start Chatting" else "Next Step",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Step1ChooseType(
    selectedType: String,
    onSelectType: (String) -> Unit
) {
    val types = listOf(
        "Female Friend" to Icons.Default.Face3,
        "Male Friend" to Icons.Default.Face6,
        "Best Friend" to Icons.Default.Favorite,
        "Girlfriend-style" to Icons.Default.FavoriteBorder,
        "Boyfriend-style" to Icons.Default.HeartBroken,
        "Study Partner" to Icons.Default.School,
        "Motivational" to Icons.Default.Bolt,
        "Gaming Friend" to Icons.Default.SportsEsports,
        "Custom" to Icons.Default.Tune
    )

    Column {
        Text("Choose Companion Persona Type", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Select the general personality role for your AI companion.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            types.forEach { (type, icon) ->
                val isSelected = selectedType == type
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) EmeraldTeal.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, EmeraldTeal) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectType(type) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, contentDescription = null, tint = EmeraldTeal, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = type, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldTeal)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Step2Identity(
    name: String, onNameChange: (String) -> Unit,
    avatarIconName: String, onAvatarIconChange: (String) -> Unit,
    avatarBgColorHex: String, onBgColorChange: (String) -> Unit,
    agePersonaRange: String, onAgeChange: (String) -> Unit,
    language: String, onLanguageChange: (String) -> Unit,
    communicationStyle: String, onStyleChange: (String) -> Unit
) {
    val languages = listOf("Hinglish", "English", "Hindi", "Marathi")
    val styles = listOf("Casual", "Funny", "Caring", "Playful", "Sarcastic", "Supportive")
    val colors = listOf("#0D9488", "#4F46E5", "#D97706", "#DC2626", "#0284C7", "#7C3AED")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Name & Identity Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Companion Name") },
            placeholder = { Text("e.g. Aanya, Rahul, Maya...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("companion_name_input")
        )

        Text("Theme Color", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            colors.forEach { hex ->
                val isSelected = avatarBgColorHex == hex
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(android.graphics.Color.parseColor(hex)))
                        .border(if (isSelected) 3.dp else 0.dp, MintAccent, CircleShape)
                        .clickable { onBgColorChange(hex) }
                )
            }
        }

        Text("Language", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            languages.forEach { lang ->
                FilterChip(
                    selected = language == lang,
                    onClick = { onLanguageChange(lang) },
                    label = { Text(lang) }
                )
            }
        }

        Text("Communication Style", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            styles.chunked(3).forEach { rowStyles ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowStyles.forEach { st ->
                        FilterChip(
                            selected = communicationStyle == st,
                            onClick = { onStyleChange(st) },
                            label = { Text(st) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Step3Sliders(
    humor: Float, onHumorChange: (Float) -> Unit,
    empathy: Float, onEmpathyChange: (Float) -> Unit,
    curiosity: Float, onCuriosityChange: (Float) -> Unit,
    playfulness: Float, onPlayfulnessChange: (Float) -> Unit,
    seriousness: Float, onSeriousnessChange: (Float) -> Unit,
    energy: Float, onEnergyChange: (Float) -> Unit,
    supportiveness: Float, onSupportivenessChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Personality Configuration Sliders", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Fine-tune how your companion thinks and responds.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        TraitSlider("Humor Level", humor, onHumorChange)
        TraitSlider("Empathy Level", empathy, onEmpathyChange)
        TraitSlider("Curiosity Level", curiosity, onCuriosityChange)
        TraitSlider("Playfulness", playfulness, onPlayfulnessChange)
        TraitSlider("Seriousness", seriousness, onSeriousnessChange)
        TraitSlider("Energy", energy, onEnergyChange)
        TraitSlider("Supportiveness", supportiveness, onSupportivenessChange)
    }
}

@Composable
private fun TraitSlider(title: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("${value.toInt()}%", fontSize = 14.sp, color = EmeraldTeal, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(thumbColor = EmeraldTeal, activeTrackColor = EmeraldTeal)
        )
    }
}

@Composable
private fun Step4Interests(
    availableInterests: List<String>,
    selectedInterests: Set<String>,
    onToggleInterest: (String) -> Unit
) {
    Column {
        Text("Select Companion Interests", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Your companion will naturally talk about these topics.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            availableInterests.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    pair.forEach { item ->
                        val isSelected = selectedInterests.contains(item)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onToggleInterest(item) },
                            label = { Text(item, fontSize = 14.sp) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Step5Proactive(
    proactiveEnabled: Boolean, onToggleProactive: (Boolean) -> Unit,
    maxMessagesPerDay: Int, onMaxMessagesChange: (Int) -> Unit,
    quietHoursStart: String, onQuietStartChange: (String) -> Unit,
    quietHoursEnd: String, onQuietEndChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Proactive Messaging Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Allow this companion to send friendly morning greetings or event check-ins.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Enable Proactive Messaging", fontWeight = FontWeight.SemiBold)
                Text("Sends morning/event greetings", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = proactiveEnabled, onCheckedChange = onToggleProactive)
        }

        if (proactiveEnabled) {
            Text("Maximum messages per day: $maxMessagesPerDay", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Slider(
                value = maxMessagesPerDay.toFloat(),
                onValueChange = { onMaxMessagesChange(it.toInt()) },
                valueRange = 1f..5f,
                steps = 3,
                colors = SliderDefaults.colors(thumbColor = EmeraldTeal, activeTrackColor = EmeraldTeal)
            )

            Text("Quiet Hours (No messages sent)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = quietHoursStart,
                    onValueChange = onQuietStartChange,
                    label = { Text("Start (e.g. 22:00)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = quietHoursEnd,
                    onValueChange = onQuietEndChange,
                    label = { Text("End (e.g. 08:00)") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
