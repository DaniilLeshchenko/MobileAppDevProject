package com.example.gamebacklogmanager.data.remote.model

import com.google.gson.annotations.SerializedName

data class SteamOwnedGamesResponse(
    val response: OwnedGamesData
)

data class OwnedGamesData(
    @SerializedName("game_count") val gameCount: Int,
    val games: List<SteamGameItem>?
)

data class SteamGameItem(
    val appid: Int,
    val name: String,
    @SerializedName("playtime_forever") val playtimeForever: Int,
    @SerializedName("img_icon_url") val imgIconUrl: String?,
    @SerializedName("has_community_visible_stats") val hasCommunityVisibleStats: Boolean?
)