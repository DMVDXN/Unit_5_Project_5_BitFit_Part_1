package com.example.unit5project5bitfitpart1.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WellnessDao {
    @Insert
    suspend fun insert(entry: WellnessEntry)

    @Query("SELECT * FROM wellness_entries ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<WellnessEntry>>
}