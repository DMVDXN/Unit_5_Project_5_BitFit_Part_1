package com.example.unit5project5bitfitpart1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wellness_entries")
data class WellnessEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountCups: Int,
    val note: String,
    val createdAt: Long
)