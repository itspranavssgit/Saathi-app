package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.ProactiveMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProactiveMessageDao {
    @Query("SELECT * FROM proactive_messages ORDER BY scheduledTimestamp DESC")
    fun getAllProactiveMessages(): Flow<List<ProactiveMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProactiveMessage(message: ProactiveMessageEntity)

    @Update
    suspend fun updateProactiveMessage(message: ProactiveMessageEntity)

    @Query("SELECT COUNT(*) FROM proactive_messages WHERE status = 'SENT'")
    fun getSentProactiveCount(): Flow<Int>
}
