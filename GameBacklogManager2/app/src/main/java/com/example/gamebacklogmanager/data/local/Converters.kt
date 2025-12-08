package com.example.gamebacklogmanager.data.local

import androidx.room.TypeConverter
import com.example.gamebacklogmanager.data.model.GameStatus

class Converters {
    @TypeConverter
    fun fromGameStatus(status: GameStatus): String {
        return status.name
    }

    @TypeConverter
    fun toGameStatus(status: String): GameStatus {
        return GameStatus.valueOf(status)
    }
}