package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC LIMIT 50")
    fun getAllHistory(): Flow<List<CalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calculation: CalculationEntity): Long

    @Delete
    suspend fun delete(calculation: CalculationEntity)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAll()
}
