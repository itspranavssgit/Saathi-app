package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY createdAtTimestamp DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE companionId = :companionId ORDER BY createdAtTimestamp DESC")
    fun getMemoriesForCompanion(companionId: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE companionId = :companionId AND isEnabled = 1")
    suspend fun getActiveMemoriesForCompanion(companionId: String): List<MemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity)

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemoryById(id: String)

    @Query("DELETE FROM memories WHERE companionId = :companionId")
    suspend fun deleteAllMemoriesForCompanion(companionId: String)

    @Query("DELETE FROM memories")
    suspend fun clearAllMemories()

    @Query("SELECT COUNT(*) FROM memories WHERE isEnabled = 1")
    fun getActiveMemoryCount(): Flow<Int>
}
