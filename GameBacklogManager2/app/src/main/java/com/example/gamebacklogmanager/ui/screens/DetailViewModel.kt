package com.example.gamebacklogmanager.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.data.remote.model.GameStatus
import com.example.gamebacklogmanager.data.remote.model.AchievementUiModel
import com.example.gamebacklogmanager.data.repository.GameRepository
import com.example.gamebacklogmanager.data.repository.SteamRepository
import com.example.gamebacklogmanager.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Detail screen.
 * Loads a game by ID, fetches Steam details if needed,
 * and loads achievements for the logged-in Steam user.
 */
class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val steamRepository: SteamRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val gameId: Int = checkNotNull(savedStateHandle["gameId"])

    private val _uiState = MutableStateFlow<GameEntity?>(null)
    val uiState: StateFlow<GameEntity?> = _uiState.asStateFlow()

    /** List of Steam achievements. */
    private val _achievements = MutableStateFlow<List<AchievementUiModel>>(emptyList())
    val achievements: StateFlow<List<AchievementUiModel>> = _achievements.asStateFlow()

    /** Loading state for achievements. */
    private val _isLoadingAchievements = MutableStateFlow(false)
    val isLoadingAchievements: StateFlow<Boolean> = _isLoadingAchievements.asStateFlow()

    init {
        // Load game fetch missing details load achievements
        viewModelScope.launch {
            val game = gameRepository.getGameById(gameId)
            _uiState.value = game

            // Auto-update description/image/tags from Steam if not filled
            if (game != null && (game.description.isBlank() || game.description == "Imported from Steam") && game.steamAppId != null) {
                val updatedGame = gameRepository.fetchAndSaveGameDetails(game)
                _uiState.value = updatedGame
            }

            // Load achievements for this game if user has Steam ID
            if (game?.steamAppId != null) {
                loadAchievements(game.steamAppId)
            }
        }
    }

    /**
     * Loads achievements using Steam Web API.
     */
    private fun loadAchievements(appId: Int) {
        val steamId = userPreferencesRepository.steamId
        if (steamId.isNotBlank()) {
            viewModelScope.launch {
                _isLoadingAchievements.value = true
                val list = steamRepository.getAchievements(steamId, appId)
                _achievements.value = list
                _isLoadingAchievements.value = false
            }
        }
    }

    /**
     * Updates the status (Now Playing / Completed / Abandoned).
     */
    fun updateStatus(newStatus: GameStatus) {
        viewModelScope.launch {
            _uiState.value?.let { currentGame ->
                val updatedGame = currentGame.copy(status = newStatus)
                gameRepository.updateGame(updatedGame)
                _uiState.value = updatedGame
            }
        }
    }

    /**
     * Deletes the game from the database.
     */
    fun deleteGame() {
        viewModelScope.launch {
            _uiState.value?.let { gameRepository.deleteGame(it) }
        }
    }
}