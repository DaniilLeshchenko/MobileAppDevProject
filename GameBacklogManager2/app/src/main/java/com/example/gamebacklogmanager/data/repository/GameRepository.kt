package com.example.gamebacklogmanager.data.repository

import com.example.gamebacklogmanager.data.local.GameDao
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.data.remote.SteamStoreService
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val gameDao: GameDao,
    private val steamStoreService: SteamStoreService? = null
) {

    fun getAllGames(): Flow<List<GameEntity>> = gameDao.getAllGames()

    suspend fun getGameById(id: Int): GameEntity? = gameDao.getGameById(id)
    
    suspend fun isGameExists(steamAppId: Int): Boolean = gameDao.getGameBySteamAppId(steamAppId) != null

    suspend fun insertGame(game: GameEntity) = gameDao.insertGame(game)

    suspend fun updateGame(game: GameEntity) = gameDao.updateGame(game)

    suspend fun deleteGame(game: GameEntity) = gameDao.deleteGame(game)

    suspend fun fetchAndSaveGameDetails(game: GameEntity): GameEntity {
        if (game.steamAppId == null || steamStoreService == null) return game
        
        try {
            val responseMap = steamStoreService.getAppDetails(game.steamAppId)
            val detailsResponse = responseMap[game.steamAppId.toString()]
            
            if (detailsResponse?.success == true && detailsResponse.data != null) {
                val data = detailsResponse.data
                
                val platformList = mutableListOf<String>()
                if (data.platforms?.windows == true) platformList.add("windows")
                if (data.platforms?.mac == true) platformList.add("mac")
                if (data.platforms?.linux == true) platformList.add("linux")
                
                val updatedGame = game.copy(
                    description = data.shortDescription ?: game.description,
                    imageUrl = data.headerImage ?: game.imageUrl,
                    genres = data.genres?.joinToString(", ") { it.description } ?: game.genres,
                    isFree = data.isFree == true,
                    priceFinal = data.priceOverview?.final ?: 0,
                    metacriticScore = data.metacritic?.score ?: 0,
                    releaseDate = data.releaseDate?.date ?: "",
                    platforms = platformList.joinToString(","),
                    tags = data.categories?.joinToString(", ") { it.description } ?: ""
                )
                updateGame(updatedGame)
                return updatedGame
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return game
    }
}