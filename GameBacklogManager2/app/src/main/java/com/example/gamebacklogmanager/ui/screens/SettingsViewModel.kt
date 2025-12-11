package com.example.gamebacklogmanager.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.data.remote.model.GameStatus
import com.example.gamebacklogmanager.data.remote.model.SteamPlayer
import com.example.gamebacklogmanager.data.repository.GameRepository
import com.example.gamebacklogmanager.data.repository.SteamRepository
import com.example.gamebacklogmanager.data.repository.UserPreferencesRepository
import kotlinx.coroutines.launch

/**
 * ViewModel managing Settings screen state:
 * - Steam ID persistence
 * - Fetching Steam profile
 * - Syncing Steam library into local database.
 */
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
        // Automatically fetch profile if ID already saved.
        if (steamId.isNotBlank()) {
            fetchSteamProfile()
        }
    }

    /** Updates local Steam ID field. */
    fun updateSteamId(newId: String) {
        steamId = newId
    }

    /** Saves Steam ID to preferences and loads Steam profile. */
    fun saveSteamId() {
        userPreferencesRepository.steamId = steamId
        fetchSteamProfile()
    }

    /** Retrieves Steam profile info from API. */
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

    /** Imports all owned Steam games into local Room database. */
    fun syncLibrary() {
        if (steamId.isBlank()) return
        
        viewModelScope.launch {
            isLoading = true
            syncStatus = "Syncing..."
            try {
                val games = steamRepository.getUserOwnedGames(steamId)
                var addedCount = 0

                // Insert only games not already in the database
                games.forEach { steamGame ->
                    if (!gameRepository.isGameExists(steamGame.appid)) {
                        val game = GameEntity(
                            title = steamGame.name,
                            steamAppId = steamGame.appid,
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