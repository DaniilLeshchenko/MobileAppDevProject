package com.example.gamebacklogmanager.data.remote.model

import com.google.gson.annotations.SerializedName
/**
 * Response containing Steam player summary data.
 */
data class SteamPlayerSummaryResponse(
    val response: SteamPlayerSummaryData
)
/**
 * List wrapper for player profile entries.
 */
data class SteamPlayerSummaryData(
    val players: List<SteamPlayer>
)
/**
 * Basic Steam user profile information.
 */
data class SteamPlayer(
    @SerializedName("steamid") val steamId: String,
    @SerializedName("personaname") val personaName: String,
    @SerializedName("avatarfull") val avatarUrl: String,
    @SerializedName("profileurl") val profileUrl: String
)