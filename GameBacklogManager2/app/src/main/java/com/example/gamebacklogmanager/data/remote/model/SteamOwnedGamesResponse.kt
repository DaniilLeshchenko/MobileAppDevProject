package com.example.gamebacklogmanager.data.remote.model

import com.google.gson.annotations.SerializedName
/**
 * Response for the list of games owned by a Steam user.
 */
data class SteamOwnedGamesResponse(
    val response: OwnedGamesData
)
/**
 * Contains the total game count and list of owned games.
 */
data class OwnedGamesData(
    @SerializedName("game_count") val gameCount: Int,
    val games: List<SteamGameItem>?
)
/**
 * Data for a single owned game returned by the Steam API.
 */
data class SteamGameItem(
    val appid: Int,
    val name: String,
    @SerializedName("playtime_forever") val playtimeForever: Int, // Minutes played
    @SerializedName("img_icon_url") val imgIconUrl: String?, // Small game icon
    @SerializedName("has_community_visible_stats") val hasCommunityVisibleStats: Boolean?
)