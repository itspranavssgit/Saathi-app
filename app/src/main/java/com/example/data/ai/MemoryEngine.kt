package com.example.data.ai

import com.example.data.local.entities.MemoryEntity
import java.util.UUID

object MemoryEngine {

    fun extractMemoryFromMessage(
        companionId: String,
        userMessage: String
    ): MemoryEntity? {
        val text = userMessage.lowercase().trim()

        // Important events pattern
        if (text.contains("interview") || text.contains("exam") || text.contains("test") || text.contains("presentation") || text.contains("meeting")) {
            val eventName = userMessage.take(120)
            return MemoryEntity(
                id = UUID.randomUUID().toString(),
                companionId = companionId,
                category = "important_events",
                memoryText = "User mentioned: \"$eventName\"",
                importance = "HIGH"
            )
        }

        // Preferences & Interests
        if (text.startsWith("i love ") || text.startsWith("i like ") || text.contains("my favorite") || text.contains("is my fav")) {
            return MemoryEntity(
                id = UUID.randomUUID().toString(),
                companionId = companionId,
                category = "interests",
                memoryText = "User preference: \"${userMessage.take(100)}\"",
                importance = "MEDIUM"
            )
        }

        // Personal info / Location / Work
        if (text.contains("my name is") || text.contains("i live in") || text.contains("i work at") || text.contains("i study at") || text.contains("my age is")) {
            return MemoryEntity(
                id = UUID.randomUUID().toString(),
                companionId = companionId,
                category = "personal_info",
                memoryText = "User detail: \"${userMessage.take(100)}\"",
                importance = "HIGH"
            )
        }

        // Goals / Projects
        if (text.contains("i am building") || text.contains("i am learning") || text.contains("my goal is") || text.contains("working on")) {
            return MemoryEntity(
                id = UUID.randomUUID().toString(),
                companionId = companionId,
                category = "goals",
                memoryText = "User goal/project: \"${userMessage.take(100)}\"",
                importance = "MEDIUM"
            )
        }

        return null
    }
}
