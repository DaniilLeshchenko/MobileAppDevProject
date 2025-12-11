package com.example.gamebacklogmanager.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogmanager.data.remote.model.GameStatus
import com.example.gamebacklogmanager.data.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
/**
 * UI state representing computed statistics from the local game library.
 */
data class StatsUiState(
    val totalGames: Int = 0,
    val completedGames: Int = 0,
    val nowPlayingGames: Int = 0,
    val purchasedGames: Int = 0,
    val abandonedGames: Int = 0,
    val totalHours: Float = 0f
)

/**
 * ViewModel calculates library statistics from Room database in real time.
 */
class StatsViewModel(gameRepository: GameRepository) : ViewModel() {

    /**
     * Observes all games and transforms them into aggregated statistics:
     * counts by status + total playtime.
     */
    val uiState: StateFlow<StatsUiState> = gameRepository.getAllGames()
        .map { games ->
            StatsUiState(
                totalGames = games.size,
                completedGames = games.count { it.status == GameStatus.COMPLETED },
                nowPlayingGames = games.count { it.status == GameStatus.NOW_PLAYING },
                purchasedGames = games.count { it.status == GameStatus.PURCHASED },
                abandonedGames = games.count { it.status == GameStatus.ABANDONED },
                totalHours = games.sumOf { it.progressHours.toDouble() }.toFloat()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatsUiState()
        )
}