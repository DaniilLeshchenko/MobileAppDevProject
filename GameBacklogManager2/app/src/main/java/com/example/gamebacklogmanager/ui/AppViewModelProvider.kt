package com.example.gamebacklogmanager.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamebacklogmanager.GameApplication
import com.example.gamebacklogmanager.ui.screens.AddEditGameViewModel
import com.example.gamebacklogmanager.ui.screens.DetailViewModel
import com.example.gamebacklogmanager.ui.screens.HomeViewModel
import com.example.gamebacklogmanager.ui.screens.SettingsViewModel
import com.example.gamebacklogmanager.ui.screens.StatsViewModel
import com.example.gamebacklogmanager.ui.screens.StoreViewModel

/**
 * Provides a single centralized ViewModelFactory for the entire app.
 * Each ViewModel is registered via an initializer and receives the
 * required repositories from the app’s dependency container.
 */
object AppViewModelProvider {

    // Shared ViewModel factory used by all composables via viewModel(...)
    val Factory = viewModelFactory {

        // Home screen ViewModel
        initializer {
            HomeViewModel(
                gameRepository = gameApplication().container.gameRepository
            )
        }

        // Add/Edit screen ViewModel
        initializer {
            AddEditGameViewModel(
                gameRepository = gameApplication().container.gameRepository,
                steamRepository = gameApplication().container.steamRepository
            )
        }

        // Detail screen ViewModel (requires SavedStateHandle for gameId)
        initializer {
            DetailViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                gameRepository = gameApplication().container.gameRepository,
                steamRepository = gameApplication().container.steamRepository,
                userPreferencesRepository = gameApplication().container.userPreferencesRepository
            )
        }

        // Stats screen ViewModel
        initializer {
            StatsViewModel(
                gameRepository = gameApplication().container.gameRepository
            )
        }

        // Settings screen ViewModel (Steam ID, sync, preferences)
        initializer {
            SettingsViewModel(
                userPreferencesRepository = gameApplication().container.userPreferencesRepository,
                steamRepository = gameApplication().container.steamRepository,
                gameRepository = gameApplication().container.gameRepository
            )
        }

        // Store search ViewModel
        initializer {
            StoreViewModel(
                steamRepository = gameApplication().container.steamRepository
            )
        }
    }
}

/**
 * Extension function to retrieve the GameApplication instance
 * from the CreationExtras used by the ViewModelFactory.
 */
fun CreationExtras.gameApplication(): GameApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameApplication)