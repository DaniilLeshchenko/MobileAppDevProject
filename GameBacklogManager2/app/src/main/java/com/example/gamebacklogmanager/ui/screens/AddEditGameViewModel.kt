package com.example.gamebacklogmanager.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.data.model.GameStatus
import com.example.gamebacklogmanager.data.remote.model.SteamGameItem
import com.example.gamebacklogmanager.data.remote.model.StoreItem
import com.example.gamebacklogmanager.data.repository.GameRepository
import com.example.gamebacklogmanager.data.repository.SteamRepository
import kotlinx.coroutines.launch

class AddEditGameViewModel(
    private val gameRepository: GameRepository,
    private val steamRepository: SteamRepository
) : ViewModel() {

    var gameUiState by mutableStateOf(GameUiState())
        private set

    var gameAppIdQuery by mutableStateOf("")
        private set
        
    var steamGamesList by mutableStateOf<List<SteamGameItem>>(emptyList())
        private set
    
    var isLoadingSteamGames by mutableStateOf(false)
        private set
        
    var searchError by mutableStateOf<String?>(null)
        private set

    fun updateUiState(newGameUiState: GameUiState) {
        gameUiState = newGameUiState
    }
    
    fun updateLocalBoxImage(path: String) {
        gameUiState = gameUiState.copy(localBoxImagePath = path)
    }
    
    fun updateGameAppIdQuery(newQuery: String) {
        gameAppIdQuery = newQuery
    }
    
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

    fun saveGame() {
        if (isValid()) {
            viewModelScope.launch {
                gameRepository.insertGame(gameUiState.toGameEntity())
            }
        }
    }

    private fun isValid(): Boolean {
        return gameUiState.title.isNotBlank()
    }
}

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