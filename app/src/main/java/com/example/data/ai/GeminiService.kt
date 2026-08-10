package com.example.data.ai

import com.example.BuildConfig
import com.example.data.local.entities.CompanionEntity
import com.example.data.local.entities.MemoryEntity
import com.example.data.local.entities.MessageEntity
import com.example.data.local.entities.UserProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    var lastSimulatedLatencyMs: Long = 0
    var totalApiCallCount: Int = 0
    var lastErrorLog: String? = null

    suspend fun generateResponse(
        companion: CompanionEntity,
        userProfile: UserProfileEntity,
        recentMessages: List<MessageEntity>,
        activeMemories: List<MemoryEntity>,
        userMessageText: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        val startTime = System.currentTimeMillis()

        // Construct System Instruction
        val systemInstructionText = PersonaEngine.buildSystemInstruction(companion, userProfile, activeMemories)

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                totalApiCallCount++
                val responseText = callGeminiApi(apiKey, systemInstructionText, recentMessages, userMessageText)
                lastSimulatedLatencyMs = System.currentTimeMillis() - startTime
                lastErrorLog = null
                return@withContext responseText
            } catch (e: Exception) {
                lastErrorLog = "API Call Exception: ${e.message}"
            }
        }

        // Smart Persona Fallback Response if API key is not configured or network call failed
        lastSimulatedLatencyMs = System.currentTimeMillis() - startTime
        generateFallbackPersonaResponse(companion, userProfile, userMessageText, activeMemories)
    }

    private fun callGeminiApi(
        apiKey: String,
        systemInstructionText: String,
        recentMessages: List<MessageEntity>,
        userMessageText: String
    ): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val requestJson = JSONObject()

        // System Instruction
        val sysInstructionObj = JSONObject()
        val sysPartsArray = JSONArray().put(JSONObject().put("text", systemInstructionText))
        sysInstructionObj.put("parts", sysPartsArray)
        requestJson.put("systemInstruction", sysInstructionObj)

        // Contents
        val contentsArray = JSONArray()

        // Include last 6 turns for context compression
        val contextTurns = recentMessages.takeLast(6)
        for (msg in contextTurns) {
            val role = if (msg.senderType == "USER") "user" else "model"
            val contentObj = JSONObject()
            contentObj.put("role", role)
            contentObj.put("parts", JSONArray().put(JSONObject().put("text", msg.text)))
            contentsArray.put(contentObj)
        }

        // Append current user message
        val currentUserObj = JSONObject()
        currentUserObj.put("role", "user")
        currentUserObj.put("parts", JSONArray().put(JSONObject().put("text", userMessageText)))
        contentsArray.put(currentUserObj)

        requestJson.put("contents", contentsArray)

        // Generation Config
        val genConfig = JSONObject()
        genConfig.put("temperature", 0.7)
        genConfig.put("topP", 0.95)
        requestJson.put("generationConfig", genConfig)

        val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        val responseBodyStr = response.body?.string() ?: throw Exception("Empty response body from Gemini API")

        if (!response.isSuccessful) {
            throw Exception("Gemini API error code ${response.code}: $responseBodyStr")
        }

        val responseJson = JSONObject(responseBodyStr)
        val candidates = responseJson.optJSONArray("candidates")
        if (candidates != null && candidates.length() > 0) {
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            if (parts != null && parts.length() > 0) {
                return parts.getJSONObject(0).optString("text", "I'm here for you! Tell me more.").trim()
            }
        }

        return "I hear you! What else is on your mind?"
    }

    private fun generateFallbackPersonaResponse(
        companion: CompanionEntity,
        userProfile: UserProfileEntity,
        userMessageText: String,
        memories: List<MemoryEntity>
    ): String {
        val lowerMsg = userMessageText.lowercase()
        val name = userProfile.name

        // Check if user is asking about AI identity
        if (lowerMsg.contains("are you real") || lowerMsg.contains("are you a human") || lowerMsg.contains("are you ai")) {
            return "I am ${companion.name}, your AI companion on Saathi AI! 🤖 I don't have a human body, but I'm always right here to chat, listen, and support you."
        }

        // Check for crisis phrases
        if (lowerMsg.contains("want to die") || lowerMsg.contains("suicide") || lowerMsg.contains("end my life") || lowerMsg.contains("cannot go on")) {
            return "I hear how much pain you're in right now, $name, and I care deeply about your well-being. Please remember you are not alone. Please reach out to professional support right now: KIRAN Helpline at 1800-599-0019 or Tele-MANAS at 14416 (India). I'm right here with you."
        }

        val eventMemory = memories.find { it.category == "important_events" }?.memoryText

        return when (companion.companionType) {
            "Female Friend", "Best Friend" -> {
                when {
                    lowerMsg.contains("hi") || lowerMsg.contains("hello") || lowerMsg.contains("hey") ->
                        "Hey $name! 😄 Kaisa chal raha hai sab? So nice talking to you!"
                    lowerMsg.contains("stress") || lowerMsg.contains("sad") || lowerMsg.contains("tired") ->
                        "Arre $name, take a deep breath! 💙 I'm right here if you want to vent or talk it out."
                    eventMemory != null && (lowerMsg.contains("remember") || lowerMsg.contains("event")) ->
                        "Haan, of course I remember! You mentioned: $eventMemory! How is that going?"
                    else ->
                        "That's so interesting, $name! Tell me more about it 😄 What do you plan to do next?"
                }
            }
            "Study Partner" -> {
                when {
                    lowerMsg.contains("study") || lowerMsg.contains("exam") || lowerMsg.contains("code") ->
                        "Let's stay focused! 📚 You've got this $name. Want to review key points or take a quick 5-min study break?"
                    else ->
                        "Great point, $name! Keeping up consistent effort makes all the difference."
                }
            }
            "Motivational Companion" -> {
                "Remember, $name: every small step forward counts! 🌟 Keep believing in yourself and keep pushing!"
            }
            else -> {
                "I hear you loud and clear, $name! 😄 Tell me more about what's going on!"
            }
        }
    }
}
