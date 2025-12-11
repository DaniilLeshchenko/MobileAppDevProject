package com.example.gamebacklogmanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


/**
 * Main Room database for storing game backlog data.
 * Contains the GameEntity table and provides access to DAOs.
 */
@Database(entities = [GameEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // DAO with CRUD operations for games
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null
        /**
         * Returns a singleton instance of the database.
         * Uses synchronized block to ensure safe initialization.
         */
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "game_database")
                    .fallbackToDestructiveMigration() // Recreate DB on version change
                    .build()
                    .also { Instance = it }
            }
        }
    }
}