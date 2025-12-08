package com.example.gamebacklogmanager.data.repository

import com.example.gamebacklogmanager.data.remote.SteamApiService
import com.example.gamebacklogmanager.data.remote.SteamStoreService
import com.example.gamebacklogmanager.data.remote.model.AchievementUiModel
import com.example.gamebacklogmanager.data.remote.model.SteamGameDetails
import com.example.gamebacklogmanager.data.remote.model.SteamGameItem
import com.example.gamebacklogmanager.data.remote.model.SteamPlayer
import com.example.gamebacklogmanager.data.remote.model.StoreItem
import com.example.gamebacklogmanager.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class SteamRepository(
    private val steamApiService: SteamApiService,
    private val steamStoreService: SteamStoreService
) {

    suspend fun getUserOwnedGames(steamId: String): List<SteamGameItem> {
        return withContext(Dispatchers.IO) {
            try {
                val response = steamApiService.getOwnedGames(
                    key = Constants.STEAM_API_KEY,
                    steamId = steamId
                )
                response.response.games ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getPlayerSummary(steamId: String): SteamPlayer? {
        return withContext(Dispatchers.IO) {
            try {
                val response = steamApiService.getPlayerSummaries(
                    key = Constants.STEAM_API_KEY,
                    steamIds = steamId
                )
                response.response.players.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getGameDetails(appId: Int): SteamGameDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val responseMap = steamStoreService.getAppDetails(appId)
                val gameDetailsResponse = responseMap[appId.toString()]
                if (gameDetailsResponse?.success == true) {
                    gameDetailsResponse.data
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    suspend fun searchStore(term: String): List<StoreItem> {
        return withContext(Dispatchers.IO) {
            try {
                val response = steamStoreService.searchStore(term)
                response.items ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getAchievements(steamId: String, appId: Int): List<AchievementUiModel> {
        return withContext(Dispatchers.IO) {
            try {
                // Run schema and player stats requests in parallel
                val schemaDeferred = async { 
                    try {
                        steamApiService.getGameSchema(Constants.STEAM_API_KEY, appId) 
                    } catch (e: Exception) { null }
                }
                val playerStatsDeferred = async { 
                    try {
                        steamApiService.getPlayerAchievements(Constants.STEAM_API_KEY, steamId, appId) 
                    } catch (e: Exception) { null }
                }

                val schemaResponse = schemaDeferred.await()
                val playerStatsResponse = playerStatsDeferred.await()

                val schemaAchievements = schemaResponse?.game?.availableGameStats?.achievements ?: emptyList()
                val playerAchievements = playerStatsResponse?.playerstats?.achievements ?: emptyList()
                
                // Map api name to achieved status (1 or 0)
                val achievedMap = playerAchievements.associate { it.apiname to (it.achieved == 1) }

                schemaAchievements.map { schema ->
                    val isUnlocked = achievedMap[schema.name] == true
                    AchievementUiModel(
                        apiName = schema.name,
                        displayName = schema.displayName,
                        description = schema.description,
                        // Use unlocked icon if unlocked, otherwise gray. Fallback to icon if gray is missing.
                        iconUrl = if (isUnlocked) schema.icon else (schema.icongray ?: schema.icon),
                        isUnlocked = isUnlocked,
                        isHidden = schema.hidden == 1
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}