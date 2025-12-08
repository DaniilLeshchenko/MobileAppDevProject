package com.example.gamebacklogmanager.data.remote

import com.example.gamebacklogmanager.data.remote.model.GameSchemaResponse
import com.example.gamebacklogmanager.data.remote.model.PlayerAchievementsResponse
import com.example.gamebacklogmanager.data.remote.model.SteamGameDetailsResponse
import com.example.gamebacklogmanager.data.remote.model.SteamOwnedGamesResponse
import com.example.gamebacklogmanager.data.remote.model.SteamPlayerSummaryResponse
import com.example.gamebacklogmanager.data.remote.model.StoreSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApiService {
    @GET("IPlayerService/GetOwnedGames/v0001/")
    suspend fun getOwnedGames(
        @Query("key") key: String,
        @Query("steamid") steamId: String,
        @Query("include_appinfo") includeAppInfo: Boolean = true,
        @Query("format") format: String = "json"
    ): SteamOwnedGamesResponse

    @GET("ISteamUser/GetPlayerSummaries/v0002/")
    suspend fun getPlayerSummaries(
        @Query("key") key: String,
        @Query("steamids") steamIds: String,
        @Query("format") format: String = "json"
    ): SteamPlayerSummaryResponse
    
    @GET("ISteamUserStats/GetPlayerAchievements/v0001/")
    suspend fun getPlayerAchievements(
        @Query("key") key: String,
        @Query("steamid") steamId: String,
        @Query("appid") appId: Int
    ): PlayerAchievementsResponse
    
    @GET("ISteamUserStats/GetSchemaForGame/v2/")
    suspend fun getGameSchema(
        @Query("key") key: String,
        @Query("appid") appId: Int
    ): GameSchemaResponse
}

interface SteamStoreService {
    @GET("api/appdetails")
    suspend fun getAppDetails(
        @Query("appids") appId: Int
    ): Map<String, SteamGameDetailsResponse>

    // Use api/storesearch endpoint for JSON response
    @GET("api/storesearch")
    suspend fun searchStore(
        @Query("term") term: String,
        @Query("l") language: String = "english",
        @Query("cc") countryCode: String = "US"
    ): StoreSearchResponse
}