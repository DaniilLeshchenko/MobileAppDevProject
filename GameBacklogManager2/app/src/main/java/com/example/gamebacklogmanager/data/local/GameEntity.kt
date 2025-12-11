package com.example.gamebacklogmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gamebacklogmanager.data.remote.model.GameStatus
/**
 * Local Room entity representing a game in the backlog.
 */
@Entity(tableName = "games")
data class GameEntity(
    /** Auto-generated database ID. */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    /** Steam App ID used for imported Steam games). */
    val steamAppId: Int? = null,
    val title: String,
    /** Cover image URL (Steam). */
    val imageUrl: String? = null,
    val status: GameStatus = GameStatus.PURCHASED,
    val progressHours: Float = 0f,
    val description: String = "",
    /** Timestamp when the entry was added. */
    val addedDate: Long = System.currentTimeMillis(),
    /** Local file path for a custom cover image. */
    val localBoxImagePath: String? = null,

    /** Extra metadata imported from Steam API. */
    val isFree: Boolean = false,
    val priceFinal: Int = 0,
    val metacriticScore: Int = 0
)