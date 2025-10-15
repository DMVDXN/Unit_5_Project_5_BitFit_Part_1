package com.example.unit5project5bitfitpart1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WellnessEntry::class], version = 1, exportSchema = false)
abstract class WellnessDatabase : RoomDatabase() {
    abstract fun wellnessDao(): WellnessDao

    companion object {
        @Volatile private var INSTANCE: WellnessDatabase? = null

        fun get(context: Context): WellnessDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    WellnessDatabase::class.java,
                    "wellness.db"
                ).build().also { INSTANCE = it }
            }
    }
}