package com.example.gamebacklogmanager.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogmanager.data.remote.model.StoreItem
import com.example.gamebacklogmanager.data.repository.SteamRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Steam Store search screen.
 * Handles input query, API calls, loading state and results.
 */
class StoreViewModel(
    private val steamRepository: SteamRepository
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<StoreItem>>(emptyList())
        private set
    
    var isLoading by mutableStateOf(false)
        private set
        
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** Updates the search input field. */
    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    /**
     * Performs search request through SteamRepository.
     * Updates results, loading state and error messages accordingly.
     */
    fun searchStore() {
        if (searchQuery.isBlank()) return
        
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            searchResults = emptyList()
            
            val results = steamRepository.searchStore(searchQuery)
            if (results.isEmpty()) {
                errorMessage = "No games found."
            } else {
                searchResults = results
            }
            isLoading = false
        }
    }
}