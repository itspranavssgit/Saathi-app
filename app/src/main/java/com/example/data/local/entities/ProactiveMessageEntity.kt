package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "proactive_messages",
    foreignKeys = [
        ForeignKey(
            entity = CompanionEntity::class,
            parentColumns = ["id"],
            childColumns = ["companionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("companionId")]
)
data class ProactiveMessageEntity(
    @PrimaryKey val id: String,
    val companionId: String,
    val triggerType: String, // "MORNING_GREETING", "EVENT_CHECKIN", "INACTIVITY_CHECKIN", "TEST_TRIGGER"
    val messageText: String,
    val scheduledTimestamp: Long,
    val status: String = "PENDING", // "PENDING", "SENT", "DISCARDED"
    val sentTimestamp: Long? = null
)
