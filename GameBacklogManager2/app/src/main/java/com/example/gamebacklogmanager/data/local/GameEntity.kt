package com.example.gamebacklogmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gamebacklogmanager.data.model.GameStatus

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val steamAppId: Int? = null,
    val title: String,
    val imageUrl: String? = null,
    val genres: String = "",
    val status: GameStatus = GameStatus.PURCHASED,
    val progressHours: Float = 0f,
    val description: String = "",
    val addedDate: Long = System.currentTimeMillis(),
    val localBoxImagePath: String? = null,
    
    val isFree: Boolean = false,
    val priceFinal: Int = 0,
    val metacriticScore: Int = 0,
    val releaseDate: String = "",
    val platforms: String = "",
    val tags: String = ""
)