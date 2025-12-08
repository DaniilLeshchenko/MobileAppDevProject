package com.example.gamebacklogmanager.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogmanager.data.remote.model.StoreItem
import com.example.gamebacklogmanager.data.repository.SteamRepository
import kotlinx.coroutines.launch

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

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

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