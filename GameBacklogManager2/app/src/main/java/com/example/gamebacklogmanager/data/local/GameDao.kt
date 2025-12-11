package com.example.gamebacklogmanager.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
/**
 * DAO interface defining all database operations for GameEntity.
 * Room generates the implementation automatically based on these annotations.
 */
@Dao
interface GameDao {
    /**
     * Returns all saved games sorted by the date they were added (newest first).
     * Flow is used so the UI automatically updates when the data changes.
     */
    @Query("SELECT * FROM games ORDER BY addedDate DESC")
    fun getAllGames(): Flow<List<GameEntity>>
    /**
     * Fetches a single game by its local database ID.
     * Returns null if the game does not exist.
     */
    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameById(id: Int): GameEntity?
    /**
     * Retrieves a game based on its Steam App ID.
     * Used to prevent duplicates when importing games from the Steam API.
     */
    @Query("SELECT * FROM games WHERE steamAppId = :steamAppId LIMIT 1")
    suspend fun getGameBySteamAppId(steamAppId: Int): GameEntity?
    /**
     * Inserts a new game into the database.
     * REPLACE ensures existing entries with the same ID are overwritten.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)
    /**
     * Updates an existing game entry (e.g., status, rating, notes).
     */
    @Update
    suspend fun updateGame(game: GameEntity)
    /**
     * Removes a game from the database.
     */
    @Delete
    suspend fun deleteGame(game: GameEntity)
}