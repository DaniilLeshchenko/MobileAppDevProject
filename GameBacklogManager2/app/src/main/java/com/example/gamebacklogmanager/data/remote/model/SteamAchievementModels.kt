package com.example.gamebacklogmanager.data.remote.model

/**
 * Response for player achievement progress.
 */
data class PlayerAchievementsResponse(
    val playerstats: PlayerStats?
)

/**
 * Contains player's achievement data for a specific game.
 */
data class PlayerStats(
    val steamID: String,
    val gameName: String,
    val achievements: List<PlayerAchievementStatus>?,
    val success: Boolean
)
/**
 * Status of a single achievement (locked/unlocked).
 */
data class PlayerAchievementStatus(
    val apiname: String,
    val achieved: Int,
    val unlocktime: Long
)
/**
 * Response containing the full achievement schema for a game.
 */
data class GameSchemaResponse(
    val game: GameSchemaData?
)
/**
 * Contains available stats and achievement definitions.
 */

data class GameSchemaData(
    val availableGameStats: AvailableGameStats?
)
/**
 * List of all achievements defined for the game.
 */
data class AvailableGameStats(
    val achievements: List<SchemaAchievement>?
)
/**
 * Definition of a single achievement (name, icons, description, etc.).
 */
data class SchemaAchievement(
    val name: String,
    val displayName: String,
    val defaultvalue: Int,
    val hidden: Int,
    val description: String?,
    val icon: String,
    val icongray: String?
)
/**
 * UI-ready achievement model used in the app.
 */
data class AchievementUiModel(
    val apiName: String,
    val displayName: String,
    val description: String?,
    val iconUrl: String,
    val isUnlocked: Boolean,
    val isHidden: Boolean
)