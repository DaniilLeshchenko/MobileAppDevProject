package com.example.gamebacklogmanager.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.data.model.GameStatus
import com.example.gamebacklogmanager.data.remote.model.AchievementUiModel
import com.example.gamebacklogmanager.data.repository.GameRepository
import com.example.gamebacklogmanager.data.repository.SteamRepository
import com.example.gamebacklogmanager.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val steamRepository: SteamRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val gameId: Int = checkNotNull(savedStateHandle["gameId"])

    private val _uiState = MutableStateFlow<GameEntity?>(null)
    val uiState: StateFlow<GameEntity?> = _uiState.asStateFlow()

    private val _achievements = MutableStateFlow<List<AchievementUiModel>>(emptyList())
    val achievements: StateFlow<List<AchievementUiModel>> = _achievements.asStateFlow()

    private val _isLoadingAchievements = MutableStateFlow(false)
    val isLoadingAchievements: StateFlow<Boolean> = _isLoadingAchievements.asStateFlow()

    init {
        viewModelScope.launch {
            val game = gameRepository.getGameById(gameId)
            _uiState.value = game
            
            // If game exists but has no description (or generic imported one) and has a Steam ID, try to fetch details
            if (game != null && (game.description.isBlank() || game.description == "Imported from Steam") && game.steamAppId != null) {
                val updatedGame = gameRepository.fetchAndSaveGameDetails(game)
                _uiState.value = updatedGame
            }

            // Load achievements if Steam ID is present
            if (game?.steamAppId != null) {
                loadAchievements(game.steamAppId)
            }
        }
    }
    
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
    
    fun updateStatus(newStatus: GameStatus) {
        viewModelScope.launch {
            _uiState.value?.let { currentGame ->
                val updatedGame = currentGame.copy(status = newStatus)
                gameRepository.updateGame(updatedGame)
                _uiState.value = updatedGame
            }
        }
    }
    
    fun deleteGame() {
        viewModelScope.launch {
            _uiState.value?.let { gameRepository.deleteGame(it) }
        }
    }
}