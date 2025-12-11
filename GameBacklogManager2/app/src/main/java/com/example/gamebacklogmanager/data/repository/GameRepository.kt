package com.example.gamebacklogmanager.data.repository

import com.example.gamebacklogmanager.data.local.GameDao
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.data.remote.SteamStoreService
import kotlinx.coroutines.flow.Flow
/**
 * Repository providing a clean API for accessing local database
 * and optional Steam Store data.
 */
class GameRepository(
    private val gameDao: GameDao,
    private val steamStoreService: SteamStoreService? = null
) {
    /** Returns all games as a Flow for automatic UI updates. */
    fun getAllGames(): Flow<List<GameEntity>> = gameDao.getAllGames()

    /** Retrieves a single game by its ID. */
    suspend fun getGameById(id: Int): GameEntity? = gameDao.getGameById(id)

    /** Checks if a game with the given Steam App ID already exists. */
    suspend fun isGameExists(steamAppId: Int): Boolean = gameDao.getGameBySteamAppId(steamAppId) != null

    /** Inserts a new game into the database. */
    suspend fun insertGame(game: GameEntity) = gameDao.insertGame(game)

    /** Updates an existing game entry. */
    suspend fun updateGame(game: GameEntity) = gameDao.updateGame(game)

    /** Deletes a game from the database. */
    suspend fun deleteGame(game: GameEntity) = gameDao.deleteGame(game)

    /**
     * Fetches additional details from the Steam Store API
     * and updates the local game entry.
     */
    suspend fun fetchAndSaveGameDetails(game: GameEntity): GameEntity {
        if (game.steamAppId == null || steamStoreService == null) return game
        
        try {
            val responseMap = steamStoreService.getAppDetails(game.steamAppId)
            val detailsResponse = responseMap[game.steamAppId.toString()]
            
            if (detailsResponse?.success == true && detailsResponse.data != null) {
                val data = detailsResponse.data

                
                val updatedGame = game.copy(
                    description = data.shortDescription ?: game.description,
                    imageUrl = data.headerImage ?: game.imageUrl,
                    isFree = data.isFree == true,
                    priceFinal = data.priceOverview?.final ?: 0,
                    metacriticScore = data.metacritic?.score ?: 0
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