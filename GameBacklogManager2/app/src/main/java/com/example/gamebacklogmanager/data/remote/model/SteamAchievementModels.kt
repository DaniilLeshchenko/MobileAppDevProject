package com.example.gamebacklogmanager.data.remote.model

import com.google.gson.annotations.SerializedName

// --- Player Achievements Status ---
data class PlayerAchievementsResponse(
    val playerstats: PlayerStats?
)

data class PlayerStats(
    val steamID: String,
    val gameName: String,
    val achievements: List<PlayerAchievementStatus>?,
    val success: Boolean
)

data class PlayerAchievementStatus(
    val apiname: String,
    val achieved: Int, // 1 if achieved, 0 if not
    val unlocktime: Long
)

// --- Game Schema (Icons, Titles) ---
data class GameSchemaResponse(
    val game: GameSchemaData?
)

data class GameSchemaData(
    val availableGameStats: AvailableGameStats?
)

data class AvailableGameStats(
    val achievements: List<SchemaAchievement>?
)

data class SchemaAchievement(
    val name: String, // Matches apiname
    val displayName: String,
    val defaultvalue: Int,
    val hidden: Int, // 0 or 1
    val description: String?,
    val icon: String, // URL for unlocked icon
    val icongray: String? // URL for locked icon
)

// --- Combined UI Model ---
data class AchievementUiModel(
    val apiName: String,
    val displayName: String,
    val description: String?,
    val iconUrl: String,
    val isUnlocked: Boolean,
    val isHidden: Boolean // If hidden and locked, maybe don't show description
)