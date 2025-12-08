package com.example.gamebacklogmanager.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.data.model.GameStatus
import com.example.gamebacklogmanager.data.remote.model.SteamPlayer
import com.example.gamebacklogmanager.data.repository.GameRepository
import com.example.gamebacklogmanager.data.repository.SteamRepository
import com.example.gamebacklogmanager.data.repository.UserPreferencesRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val steamRepository: SteamRepository,
    private val gameRepository: GameRepository
) : ViewModel() {

    var steamId by mutableStateOf(userPreferencesRepository.steamId)
        private set

    var steamProfile by mutableStateOf<SteamPlayer?>(null)
        private set
    
    var isLoading by mutableStateOf(false)
        private set
        
    var errorMessage by mutableStateOf<String?>(null)
        private set
        
    var syncStatus by mutableStateOf<String?>(null)
        private set

    init {
        if (steamId.isNotBlank()) {
            fetchSteamProfile()
        }
    }

    fun updateSteamId(newId: String) {
        steamId = newId
    }

    fun saveSteamId() {
        userPreferencesRepository.steamId = steamId
        fetchSteamProfile()
    }

    private fun fetchSteamProfile() {
        if (steamId.isBlank()) return
        
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val profile = steamRepository.getPlayerSummary(steamId)
            if (profile != null) {
                steamProfile = profile
            } else {
                errorMessage = "Could not find Steam profile"
            }
            isLoading = false
        }
    }
    
    fun syncLibrary() {
        if (steamId.isBlank()) return
        
        viewModelScope.launch {
            isLoading = true
            syncStatus = "Syncing..."
            try {
                val games = steamRepository.getUserOwnedGames(steamId)
                var addedCount = 0
                games.forEach { steamGame ->
                    if (!gameRepository.isGameExists(steamGame.appid)) {
                        // Create basic entity
                        val game = GameEntity(
                            title = steamGame.name,
                            steamAppId = steamGame.appid,
                            // URL for header image, does not require API call to Store
                            imageUrl = "https://steamcdn-a.akamaihd.net/steam/apps/${steamGame.appid}/header.jpg",
                            progressHours = steamGame.playtimeForever / 60f,
                            status = GameStatus.PURCHASED, 
                            description = "Imported from Steam"
                        )
                        gameRepository.insertGame(game)
                        addedCount++
                    }
                }
                syncStatus = "Synced! Added $addedCount new games."
            } catch (e: Exception) {
                errorMessage = "Sync failed: ${e.message}"
                syncStatus = null
            } finally {
                isLoading = false
            }
        }
    }
}