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

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                gameRepository = gameApplication().container.gameRepository
            )
        }
        initializer {
            AddEditGameViewModel(
                gameRepository = gameApplication().container.gameRepository,
                steamRepository = gameApplication().container.steamRepository
            )
        }
        initializer {
            DetailViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                gameRepository = gameApplication().container.gameRepository,
                steamRepository = gameApplication().container.steamRepository,
                userPreferencesRepository = gameApplication().container.userPreferencesRepository
            )
        }
        initializer {
            StatsViewModel(
                gameRepository = gameApplication().container.gameRepository
            )
        }
        initializer {
            SettingsViewModel(
                userPreferencesRepository = gameApplication().container.userPreferencesRepository,
                steamRepository = gameApplication().container.steamRepository,
                gameRepository = gameApplication().container.gameRepository
            )
        }
        initializer {
            StoreViewModel(
                steamRepository = gameApplication().container.steamRepository
            )
        }
    }
}

fun CreationExtras.gameApplication(): GameApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameApplication)