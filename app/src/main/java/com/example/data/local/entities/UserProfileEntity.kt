package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "primary_user",
    val name: String = "Friend",
    val email: String = "user@saathiai.app",
    val preferredLanguage: String = "Hinglish",
    val bio: String = "Conversationalist & tech enthusiast",
    val avatarIconName: String = "avatar_user_1",
    val enableGlobalProactive: Boolean = true,
    val globalQuietHoursStart: String = "22:00",
    val globalQuietHoursEnd: String = "08:00",
    val maxDailyProactiveMessages: Int = 3,
    val enableMemorySystem: Boolean = true
)
