package com.example.data.ai

import com.example.data.local.entities.CompanionEntity
import com.example.data.local.entities.MemoryEntity
import com.example.data.local.entities.UserProfileEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ProactiveEngine {

    fun generateProactiveMessage(
        companion: CompanionEntity,
        userProfile: UserProfileEntity,
        memories: List<MemoryEntity>,
        triggerType: String
    ): String {
        val eventMemory = memories.find { it.category == "important_events" }?.memoryText
        val name = userProfile.name

        return when (triggerType) {
            "MORNING_GREETING" -> {
                when (companion.language.lowercase()) {
                    "hinglish" -> listOf(
                        "Good morning $name! 😄 Aaj ka din kaisa hone wala hai? Koi naye plans?",
                        "Hey $name, morning! ☀️ Chai/Coffee ho gayi? How are you feeling today?",
                        "Subah ho gayi $name! Have a fantastic day ahead! What's on your schedule today?"
                    ).random()
                    "hindi" -> "शुभ प्रभात $name! ☀️ आपका आज का दिन कैसा रहे?"
                    "marathi" -> "शुभ सकाळ $name! ☀️ आज काय विशेष?"
                    else -> "Good morning $name! ☀️ Wishing you a wonderful day ahead! What are your plans for today?"
                }
            }
            "EVENT_CHECKIN" -> {
                if (eventMemory != null) {
                    when (companion.language.lowercase()) {
                        "hinglish" -> "Hey $name! Mujhe yaad aaya aapne bataya tha... $eventMemory. Kaisa raha sab?"
                        else -> "Hey $name! I was just thinking about what you mentioned earlier: $eventMemory. How did it go?"
                    }
                } else {
                    when (companion.language.lowercase()) {
                        "hinglish" -> "Hey $name! Sab theek-thaak chal raha hai na? Just checking in on you! 😊"
                        else -> "Hey $name! Just checking in to see how your day is going! 😊"
                    }
                }
            }
            "INACTIVITY_CHECKIN" -> {
                when (companion.language.lowercase()) {
                    "hinglish" -> listOf(
                        "Hey $name! Kaafi time ho gaya baat kiye 😄 Hope everything is going great with you!",
                        "Arre $name! Sab badhiya na? Thought I'd drop a quick hi to see how you are doing! ✨"
                    ).random()
                    else -> "Hey $name! It's been a little while! Hope everything is going well with you. 😊"
                }
            }
            else -> {
                when (companion.language.lowercase()) {
                    "hinglish" -> "Hey $name! Bas aisi hi yaad aayi aapki. Kya kar rahe ho abhi? 😄"
                    else -> "Hey $name! Just wanted to say hi and see what you're up to today! 😄"
                }
            }
        }
    }

    fun isQuietHour(quietStart: String, quietEnd: String): Boolean {
        try {
            val now = Calendar.getInstance()
            val currentHourMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

            val startParts = quietStart.split(":").map { it.toInt() }
            val endParts = quietEnd.split(":").map { it.toInt() }

            val startMin = startParts[0] * 60 + startParts[1]
            val endMin = endParts[0] * 60 + endParts[1]

            return if (startMin > endMin) {
                // Overnight quiet hours, e.g., 22:00 to 08:00
                currentHourMin >= startMin || currentHourMin <= endMin
            } else {
                currentHourMin in startMin..endMin
            }
        } catch (e: Exception) {
            return false
        }
    }
}
