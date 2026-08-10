package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderType: String, // "USER" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "READ", // "SENDING", "SENT", "DELIVERED", "READ"
    val reactionEmoji: String? = null, // e.g. "❤️", "😂", "👍", "🔥", "🤗", "💡"
    val isRegenerated: Boolean = false,
    val isProactive: Boolean = false,
    val extractedMemoryText: String? = null
)
