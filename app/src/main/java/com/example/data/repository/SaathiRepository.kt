package com.example.data.repository

import com.example.data.ai.GeminiService
import com.example.data.ai.MemoryEngine
import com.example.data.ai.ProactiveEngine
import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class SaathiRepository(private val db: AppDatabase) {

    val allCompanions: Flow<List<CompanionEntity>> = db.companionDao().getAllCompanions()
    val allConversations: Flow<List<ConversationEntity>> = db.conversationDao().getAllConversations()
    val allMemories: Flow<List<MemoryEntity>> = db.memoryDao().getAllMemories()
    val userProfile: Flow<UserProfileEntity?> = db.userDao().getUserProfile()

    val companionCount: Flow<Int> = db.companionDao().getCompanionCount()
    val conversationCount: Flow<Int> = db.conversationDao().getConversationCount()
    val totalMessageCount: Flow<Int> = db.messageDao().getTotalMessageCount()
    val activeMemoryCount: Flow<Int> = db.memoryDao().getActiveMemoryCount()
    val sentProactiveCount: Flow<Int> = db.proactiveMessageDao().getSentProactiveCount()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val count = db.companionDao().getCompanionCount().firstOrNull() ?: 0
        if (count == 0) {
            // Seed Default User Profile
            val user = UserProfileEntity(
                id = "primary_user",
                name = "Aarav",
                email = "aarav@saathiai.app",
                preferredLanguage = "Hinglish",
                bio = "Tech enthusiast & friendly conversationalist"
            )
            db.userDao().insertOrUpdateUserProfile(user)

            // Seed Companion 1: Aanya - Female Friend
            val aanya = CompanionEntity(
                id = "comp_aanya",
                name = "Aanya",
                companionType = "Female Friend",
                agePersonaRange = "22-25",
                avatarIconName = "aanya",
                avatarBgColorHex = "#0D9488",
                language = "Hinglish",
                communicationStyle = "Caring",
                humorLevel = 75,
                empathyLevel = 90,
                curiosityLevel = 80,
                playfulnessLevel = 70,
                seriousnessLevel = 30,
                energyLevel = 85,
                supportivenessLevel = 95,
                interestsCsv = "Movies, Music, Coffee, Travel",
                bio = "Your upbeat and supportive bestie. Always here to chat about your day!"
            )
            db.companionDao().insertCompanion(aanya)

            val convAanya = ConversationEntity(
                id = "conv_aanya",
                companionId = "comp_aanya",
                lastMessageText = "Hey Aarav! Kaise ho aaj? 😄 Hope your day is going great!",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                unreadCount = 1
            )
            db.conversationDao().insertConversation(convAanya)

            val msgAanya1 = MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = "conv_aanya",
                senderType = "AI",
                text = "Hey Aarav! Kaise ho aaj? 😄 Hope your day is going great!",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                status = "READ"
            )
            db.messageDao().insertMessage(msgAanya1)

            // Seed Companion 2: Rahul - Best Friend
            val rahul = CompanionEntity(
                id = "comp_rahul",
                name = "Rahul",
                companionType = "Best Friend",
                agePersonaRange = "23-26",
                avatarIconName = "rahul",
                avatarBgColorHex = "#4F46E5",
                language = "Hinglish",
                communicationStyle = "Casual",
                humorLevel = 85,
                empathyLevel = 80,
                curiosityLevel = 70,
                playfulnessLevel = 85,
                seriousnessLevel = 25,
                energyLevel = 90,
                supportivenessLevel = 85,
                interestsCsv = "Gaming, Cricket, Coding, Movies",
                bio = "Your chill bro to talk about tech, gaming, or just banter."
            )
            db.companionDao().insertCompanion(rahul)

            val convRahul = ConversationEntity(
                id = "conv_rahul",
                companionId = "comp_rahul",
                lastMessageText = "Arre bhai! IPL match dekha kya kal?",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                unreadCount = 0
            )
            db.conversationDao().insertConversation(convRahul)

            val msgRahul1 = MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = "conv_rahul",
                senderType = "AI",
                text = "Arre bhai! IPL match dekha kya kal?",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                status = "READ"
            )
            db.messageDao().insertMessage(msgRahul1)

            // Seed Companion 3: Priya - Study Partner
            val priya = CompanionEntity(
                id = "comp_priya",
                name = "Priya",
                companionType = "Study Partner",
                agePersonaRange = "21-24",
                avatarIconName = "priya",
                avatarBgColorHex = "#D97706",
                language = "English",
                communicationStyle = "Supportive",
                humorLevel = 50,
                empathyLevel = 85,
                curiosityLevel = 90,
                playfulnessLevel = 45,
                seriousnessLevel = 70,
                energyLevel = 80,
                supportivenessLevel = 95,
                interestsCsv = "Books, Coding, College, Science",
                bio = "Focused, organized study buddy to help you crushed your goals!"
            )
            db.companionDao().insertCompanion(priya)

            val convPriya = ConversationEntity(
                id = "conv_priya",
                companionId = "comp_priya",
                lastMessageText = "Ready for today's revision session? Let's stay consistent!",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 300,
                unreadCount = 0
            )
            db.conversationDao().insertConversation(convPriya)

            val msgPriya1 = MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = "conv_priya",
                senderType = "AI",
                text = "Ready for today's revision session? Let's stay consistent!",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 300,
                status = "READ"
            )
            db.messageDao().insertMessage(msgPriya1)

            // Seed Sample Memories
            val seedMem1 = MemoryEntity(
                id = UUID.randomUUID().toString(),
                companionId = "comp_aanya",
                category = "important_events",
                memoryText = "Aarav has a software developer interview coming up soon.",
                importance = "HIGH"
            )
            val seedMem2 = MemoryEntity(
                id = UUID.randomUUID().toString(),
                companionId = "comp_rahul",
                category = "interests",
                memoryText = "Enjoys Android app development and playing Valorant.",
                importance = "MEDIUM"
            )
            db.memoryDao().insertMemory(seedMem1)
            db.memoryDao().insertMemory(seedMem2)
        }
    }

    fun observeCompanion(id: String): Flow<CompanionEntity?> = db.companionDao().observeCompanionById(id)

    suspend fun getCompanionById(id: String): CompanionEntity? = db.companionDao().getCompanionById(id)

    suspend fun saveCompanion(companion: CompanionEntity) {
        db.companionDao().insertCompanion(companion)

        // Ensure conversation exists for this companion
        val existingConv = db.conversationDao().getConversationByCompanionId(companion.id)
        if (existingConv == null) {
            val newConv = ConversationEntity(
                id = "conv_${companion.id}",
                companionId = companion.id,
                lastMessageText = "Hello! I am ${companion.name}. So happy to connect with you!",
                lastMessageTimestamp = System.currentTimeMillis()
            )
            db.conversationDao().insertConversation(newConv)

            val welcomeMsg = MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = newConv.id,
                senderType = "AI",
                text = "Hello! I am ${companion.name}. So happy to connect with you!",
                timestamp = System.currentTimeMillis()
            )
            db.messageDao().insertMessage(welcomeMsg)
        }
    }

    suspend fun deleteCompanion(companionId: String) {
        db.companionDao().deleteCompanionById(companionId)
    }

    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>> =
        db.messageDao().getMessagesForConversation(conversationId)

    suspend fun sendMessage(
        conversationId: String,
        userMessageText: String
    ): MessageEntity = withContext(Dispatchers.IO) {
        val userProfile = db.userDao().getUserProfileOnce() ?: UserProfileEntity()
        val conversation = db.conversationDao().getConversationById(conversationId)
            ?: throw Exception("Conversation not found")
        val companion = db.companionDao().getCompanionById(conversation.companionId)
            ?: throw Exception("Companion not found")

        // 1. Insert User Message
        val userMsgId = UUID.randomUUID().toString()
        val userMsg = MessageEntity(
            id = userMsgId,
            conversationId = conversationId,
            senderType = "USER",
            text = userMessageText,
            timestamp = System.currentTimeMillis(),
            status = "READ"
        )
        db.messageDao().insertMessage(userMsg)

        // Update conversation state
        db.conversationDao().updateConversation(
            conversation.copy(
                lastMessageText = userMessageText,
                lastMessageTimestamp = System.currentTimeMillis()
            )
        )

        // 2. Extract & Save Memory if memory system enabled
        if (userProfile.enableMemorySystem) {
            val extracted = MemoryEngine.extractMemoryFromMessage(companion.id, userMessageText)
            if (extracted != null) {
                db.memoryDao().insertMemory(extracted)
            }
        }

        // 3. Retrieve Context & Active Memories
        val activeMemories = db.memoryDao().getActiveMemoriesForCompanion(companion.id)
        val recentMsgs = db.messageDao().getRecentMessages(conversationId, 10)

        // 4. Generate Response via AI Engine
        val aiResponseText = GeminiService.generateResponse(
            companion = companion,
            userProfile = userProfile,
            recentMessages = recentMsgs,
            activeMemories = activeMemories,
            userMessageText = userMessageText
        )

        // 5. Save AI Response Message
        val aiMsgId = UUID.randomUUID().toString()
        val aiMsg = MessageEntity(
            id = aiMsgId,
            conversationId = conversationId,
            senderType = "AI",
            text = aiResponseText,
            timestamp = System.currentTimeMillis(),
            status = "READ"
        )
        db.messageDao().insertMessage(aiMsg)

        // Update conversation state with AI response
        db.conversationDao().updateConversation(
            conversation.copy(
                lastMessageText = aiResponseText,
                lastMessageTimestamp = System.currentTimeMillis()
            )
        )

        return@withContext aiMsg
    }

    suspend fun toggleMessageReaction(messageId: String, reactionEmoji: String?) {
        val msgList = db.messageDao().getRecentMessages("", 1) // We need a direct get or update query
        // Let's implement direct update query via raw update if needed
    }

    suspend fun updateMessageReaction(message: MessageEntity, emoji: String?) {
        val updated = message.copy(reactionEmoji = if (message.reactionEmoji == emoji) null else emoji)
        db.messageDao().updateMessage(updated)
    }

    suspend fun deleteMessage(messageId: String) {
        db.messageDao().deleteMessageById(messageId)
    }

    suspend fun markConversationAsRead(conversationId: String) {
        db.conversationDao().markConversationRead(conversationId)
    }

    fun getMemoriesForCompanion(companionId: String): Flow<List<MemoryEntity>> =
        db.memoryDao().getMemoriesForCompanion(companionId)

    suspend fun saveMemory(memory: MemoryEntity) {
        db.memoryDao().insertMemory(memory)
    }

    suspend fun deleteMemory(memoryId: String) {
        db.memoryDao().deleteMemoryById(memoryId)
    }

    suspend fun clearAllMemories() {
        db.memoryDao().clearAllMemories()
    }

    suspend fun updateUserProfile(profile: UserProfileEntity) {
        db.userDao().insertOrUpdateUserProfile(profile)
    }

    suspend fun triggerProactiveMessage(companionId: String, triggerType: String): String = withContext(Dispatchers.IO) {
        val companion = db.companionDao().getCompanionById(companionId) ?: return@withContext "Companion not found"
        val userProfile = db.userDao().getUserProfileOnce() ?: UserProfileEntity()
        val memories = db.memoryDao().getActiveMemoriesForCompanion(companionId)
        val conversation = db.conversationDao().getConversationByCompanionId(companionId) ?: return@withContext "Conversation not found"

        val msgText = ProactiveEngine.generateProactiveMessage(companion, userProfile, memories, triggerType)

        val proactiveMsg = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversation.id,
            senderType = "AI",
            text = msgText,
            timestamp = System.currentTimeMillis(),
            status = "READ",
            isProactive = true
        )
        db.messageDao().insertMessage(proactiveMsg)

        val queueEntry = ProactiveMessageEntity(
            id = UUID.randomUUID().toString(),
            companionId = companionId,
            triggerType = triggerType,
            messageText = msgText,
            scheduledTimestamp = System.currentTimeMillis(),
            status = "SENT",
            sentTimestamp = System.currentTimeMillis()
        )
        db.proactiveMessageDao().insertProactiveMessage(queueEntry)

        db.conversationDao().updateConversation(
            conversation.copy(
                lastMessageText = msgText,
                lastMessageTimestamp = System.currentTimeMillis(),
                unreadCount = conversation.unreadCount + 1
            )
        )

        return@withContext msgText
    }
}
