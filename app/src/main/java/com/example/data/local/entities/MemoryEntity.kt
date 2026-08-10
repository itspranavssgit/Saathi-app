package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "memories",
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
data class MemoryEntity(
    @PrimaryKey val id: String,
    val companionId: String,
    val category: String, // "personal_info", "preferences", "interests", "important_events", "goals", "projects", "relationships", "facts"
    val memoryText: String,
    val importance: String = "HIGH", // "LOW", "MEDIUM", "HIGH"
    val isEnabled: Boolean = true,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
