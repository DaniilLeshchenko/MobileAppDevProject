package com.example.gamebacklogmanager.data.remote.model

import com.google.gson.annotations.SerializedName

data class SteamPlayerSummaryResponse(
    val response: SteamPlayerSummaryData
)

data class SteamPlayerSummaryData(
    val players: List<SteamPlayer>
)

data class SteamPlayer(
    @SerializedName("steamid") val steamId: String,
    @SerializedName("personaname") val personaName: String,
    @SerializedName("avatarfull") val avatarUrl: String,
    @SerializedName("profileurl") val profileUrl: String
)