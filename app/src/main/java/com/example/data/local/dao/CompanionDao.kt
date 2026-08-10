package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.CompanionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanionDao {
    @Query("SELECT * FROM companions ORDER BY createdAtTimestamp DESC")
    fun getAllCompanions(): Flow<List<CompanionEntity>>

    @Query("SELECT * FROM companions WHERE id = :id")
    suspend fun getCompanionById(id: String): CompanionEntity?

    @Query("SELECT * FROM companions WHERE id = :id")
    fun observeCompanionById(id: String): Flow<CompanionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanion(companion: CompanionEntity)

    @Update
    suspend fun updateCompanion(companion: CompanionEntity)

    @Query("DELETE FROM companions WHERE id = :id")
    suspend fun deleteCompanionById(id: String)

    @Query("SELECT COUNT(*) FROM companions")
    fun getCompanionCount(): Flow<Int>
}
