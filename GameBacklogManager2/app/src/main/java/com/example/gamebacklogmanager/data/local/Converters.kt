package com.example.gamebacklogmanager.data.local

import androidx.room.TypeConverter
import com.example.gamebacklogmanager.data.remote.model.GameStatus
/**
 * Converts GameStatus enum to/from a String for Room storage.
 */
class Converters {
    /** Converts enum value to String. */
    @TypeConverter
    fun fromGameStatus(status: GameStatus): String {
        return status.name
    }
    /** Converts stored String back to GameStatus enum. */
    @TypeConverter
    fun toGameStatus(status: String): GameStatus {
        return GameStatus.valueOf(status)
    }
}