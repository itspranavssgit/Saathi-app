package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.repository.SaathiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SaathiViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = SaathiRepository(db)

    val companions: StateFlow<List<CompanionEntity>> = repository.allCompanions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<ConversationEntity>> = repository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allMemories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Selection State
    private val _selectedConversationId = MutableStateFlow<String?>(null)
    val selectedConversationId: StateFlow<String?> = _selectedConversationId.asStateFlow()

    private val _selectedCompanionId = MutableStateFlow<String?>(null)
    val selectedCompanionId: StateFlow<String?> = _selectedCompanionId.asStateFlow()

    val activeConversation: StateFlow<ConversationEntity?> = combine(conversations, _selectedConversationId) { convs, id ->
        convs.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeCompanion: StateFlow<CompanionEntity?> = combine(companions, _selectedCompanionId) { comps, id ->
        comps.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeMessages: StateFlow<List<MessageEntity>> = _selectedConversationId.flatMapLatest { id ->
        if (id != null) repository.getMessagesForConversation(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCompanionMemories: StateFlow<List<MemoryEntity>> = _selectedCompanionId.flatMapLatest { id ->
        if (id != null) repository.getMemoriesForCompanion(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    // Metrics StateFlows for Dev Dashboard
    val companionCount = repository.companionCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val conversationCount = repository.conversationCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val totalMessageCount = repository.totalMessageCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val activeMemoryCount = repository.activeMemoryCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val sentProactiveCount = repository.sentProactiveCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun selectConversation(conversationId: String) {
        _selectedConversationId.value = conversationId
        viewModelScope.launch(Dispatchers.IO) {
            val conv = repository.allConversations.firstOrNull()?.find { it.id == conversationId }
            if (conv != null) {
                _selectedCompanionId.value = conv.companionId
                repository.markConversationAsRead(conversationId)
            }
        }
    }

    fun selectCompanion(companionId: String) {
        _selectedCompanionId.value = companionId
        viewModelScope.launch(Dispatchers.IO) {
            val conv = repository.allConversations.firstOrNull()?.find { it.companionId == companionId }
            if (conv != null) {
                _selectedConversationId.value = conv.id
                repository.markConversationAsRead(conv.id)
            }
        }
    }

    fun sendMessage(text: String) {
        val convId = _selectedConversationId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _isTyping.value = true
            try {
                repository.sendMessage(convId, text)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isTyping.value = false
            }
        }
    }

    fun regenerateResponse(message: MessageEntity) {
        val convId = _selectedConversationId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMessage(message.id)
            val msgs = activeMessages.value
            val lastUserMsg = msgs.lastOrNull { it.senderType == "USER" }?.text ?: "Hello"
            _isTyping.value = true
            try {
                repository.sendMessage(convId, lastUserMsg)
            } finally {
                _isTyping.value = false
            }
        }
    }

    fun toggleReaction(message: MessageEntity, emoji: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMessageReaction(message, emoji)
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMessage(messageId)
        }
    }

    fun saveCompanion(companion: CompanionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveCompanion(companion)
            selectCompanion(companion.id)
        }
    }

    fun deleteCompanion(companionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCompanion(companionId)
            if (_selectedCompanionId.value == companionId) {
                _selectedCompanionId.value = null
                _selectedConversationId.value = null
            }
        }
    }

    fun saveMemory(memory: MemoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveMemory(memory)
        }
    }

    fun deleteMemory(memoryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMemory(memoryId)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllMemories()
        }
    }

    fun updateUserProfile(profile: UserProfileEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserProfile(profile)
        }
    }

    fun triggerProactiveMessage(companionId: String, triggerType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.triggerProactiveMessage(companionId, triggerType)
        }
    }
}
