package com.example.gamebacklogmanager.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.data.remote.model.GameStatus
import com.example.gamebacklogmanager.data.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
/**
 * UI model grouping games by status for HomeScreen.
 */
data class HomeUiState(
    val nowPlayingGames: List<GameEntity> = emptyList(),
    val purchasedGames: List<GameEntity> = emptyList(),
    val completedGames: List<GameEntity> = emptyList(),
    val abandonedGames: List<GameEntity> = emptyList(),
    val isFiltering: Boolean = false
)

/**
 * Simple filter model for searching games by title.
 */
data class GameFilter(
    val query: String = ""
)

/**
 * ViewModel providing filtered and categorized game lists.
 */
class HomeViewModel(private val gameRepository: GameRepository) : ViewModel() {

    // Current filter state for Compose UI
    var filter by mutableStateOf(GameFilter())
        private set

    // Backing Flow used to recompute results when filter changes
    private val _filterFlow = kotlinx.coroutines.flow.MutableStateFlow(GameFilter())

    /**
     * Updates search filter in both Compose state & Flow.
     */
    fun updateFilterState(newFilter: GameFilter) {
        filter = newFilter
        _filterFlow.value = newFilter
    }

    /**
     * Combined stream of:
     * - all games from local Room DB
     * - current search filter
     *
     * Produces grouped HomeUiState for use in HomeScreen.
     */
    val homeUiState: StateFlow<HomeUiState> = combine(
        gameRepository.getAllGames(),
        _filterFlow
    ) { games, currentFilter ->

        // Apply search text filter
        val filteredGames = if (currentFilter.query.isBlank()) {
            games
        } else {
            games.filter { game ->
                game.title.contains(currentFilter.query, ignoreCase = true)
            }
        }

        // Build UI state by grouping games by status
        HomeUiState(
            nowPlayingGames = filteredGames.filter { it.status == GameStatus.NOW_PLAYING },
            purchasedGames = filteredGames.filter { it.status == GameStatus.PURCHASED },
            completedGames = filteredGames.filter { it.status == GameStatus.COMPLETED },
            abandonedGames = filteredGames.filter { it.status == GameStatus.ABANDONED },
            isFiltering = currentFilter.query.isNotBlank()
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}