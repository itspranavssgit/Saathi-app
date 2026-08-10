package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companions")
data class CompanionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val companionType: String, // Female Friend, Male Friend, Best Friend, Girlfriend, Boyfriend, Study Partner, Motivational, Gaming, Custom
    val agePersonaRange: String, // e.g. "20-24"
    val avatarIconName: String, // e.g. "aanya", "rahul", "priya", "custom_1"
    val avatarBgColorHex: String, // e.g. "#0D9488"
    val language: String, // English, Hindi, Hinglish, Marathi
    val communicationStyle: String, // Casual, Funny, Caring, Serious, Playful, Sarcastic, Supportive
    val humorLevel: Int = 70, // 0-100
    val empathyLevel: Int = 85,
    val curiosityLevel: Int = 75,
    val playfulnessLevel: Int = 60,
    val seriousnessLevel: Int = 40,
    val energyLevel: Int = 80,
    val supportivenessLevel: Int = 90,
    val interestsCsv: String = "Movies, Music, Books", // comma separated
    val bio: String = "Your friendly AI companion",
    val proactiveEnabled: Boolean = true,
    val maxMessagesPerDay: Int = 3,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "08:00",
    val preferredActiveHours: String = "09:00 - 21:00",
    val relationshipFamiliarity: Int = 50, // 0-100 score that grows over time
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
