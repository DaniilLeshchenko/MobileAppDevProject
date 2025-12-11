package com.example.gamebacklogmanager.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.data.remote.model.GameStatus
import com.example.gamebacklogmanager.data.remote.model.SteamGameItem
import com.example.gamebacklogmanager.data.remote.model.StoreItem
import com.example.gamebacklogmanager.data.repository.GameRepository
import com.example.gamebacklogmanager.data.repository.SteamRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for managing Add/Edit Game UI state and saving games.
 */
class AddEditGameViewModel(
    private val gameRepository: GameRepository,
    private val steamRepository: SteamRepository
) : ViewModel() {

    /** Current state of the game being added or edited. */
    var gameUiState by mutableStateOf(GameUiState())
        private set

    /** Input value for Steam App ID search. */
    var gameAppIdQuery by mutableStateOf("")
        private set

    /** Indicates if Steam API data is loading. */
    var isLoadingSteamGames by mutableStateOf(false)
        private set

    /** Error message shown when search fails. */
    var searchError by mutableStateOf<String?>(null)
        private set

    /** Updates full UI state. */
    fun updateUiState(newGameUiState: GameUiState) {
        gameUiState = newGameUiState
    }

    /** Updates path to locally captured box art image. */
    fun updateLocalBoxImage(path: String) {
        gameUiState = gameUiState.copy(localBoxImagePath = path)
    }

    /** Updates Steam App ID text input. */
    fun updateGameAppIdQuery(newQuery: String) {
        gameAppIdQuery = newQuery
    }

    /**
     * Searches Steam API by app ID and populates game fields if found.
     * Triggered when user enters an ID manually or comes from Store screen.
     */
    fun searchGameByAppId(appIdOverride: String? = null) {
        val appIdStr = appIdOverride ?: gameAppIdQuery
        val appIdInt = appIdStr.toIntOrNull()
        if (appIdInt == null) {
            searchError = "Invalid App ID"
            return
        }
        
        viewModelScope.launch {
            isLoadingSteamGames = true
            searchError = null
            val details = steamRepository.getGameDetails(appIdInt)

            if (details != null) {
                // Fill UI state with Steam metadata
                gameUiState = gameUiState.copy(
                    title = details.name,
                    steamAppId = details.steamAppId.toString(),
                    description = details.shortDescription ?: "",
                    imageUrl = details.headerImage ?: "",
                    progressHours = "0"
                )
            } else {
                if (appIdOverride == null) {
                    searchError = "Game not found via API"
                }
            }
            isLoadingSteamGames = false
        }
    }

    /** Saves game to database if validation passes. */
    fun saveGame() {
        if (isValid()) {
            viewModelScope.launch {
                gameRepository.insertGame(gameUiState.toGameEntity())
            }
        }
    }


    /** Basic validation: title must not be empty. */
    private fun isValid(): Boolean {
        return gameUiState.title.isNotBlank()
    }
}

/**
 * UI model representing editable fields on Add/Edit screen.
 */
data class GameUiState(
    val id: Int = 0,
    val title: String = "",
    val steamAppId: String = "",
    val description: String = "",
    val status: GameStatus = GameStatus.PURCHASED,
    val progressHours: String = "0",
    val imageUrl: String = "",
    val localBoxImagePath: String? = null
)

/**
 * Maps UI state to a Room entity for database storage.
 */
fun GameUiState.toGameEntity(): GameEntity = GameEntity(
    id = id,
    title = title,
    steamAppId = steamAppId.toIntOrNull(),
    description = description,
    status = status,
    progressHours = progressHours.toFloatOrNull() ?: 0f,
    imageUrl = imageUrl,
    localBoxImagePath = localBoxImagePath
)