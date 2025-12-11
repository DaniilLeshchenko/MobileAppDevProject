package com.example.gamebacklogmanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.ui.AppViewModelProvider
import com.example.gamebacklogmanager.ui.components.GameCard
import com.example.gamebacklogmanager.ui.components.GameListItem

/**
 * Main screen showing user’s game library grouped by status.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onGameClick: (Int) -> Unit = {},
    onAddGameClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    onStoreClick: () -> Unit = {}
) {
    val uiState by viewModel.homeUiState.collectAsState()
    val filterState = viewModel.filter
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Games") },
                actions = {
                    IconButton(onClick = onStoreClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Steam Store")
                    }
                    TextButton(onClick = onStatsClick) {
                        Text("Stats")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGameClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Game")
            }
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {

            // Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = filterState.query,
                    onValueChange = { viewModel.updateFilterState(filterState.copy(query = it)) },
                    placeholder = { Text("Search my games...") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = if (filterState.query.isNotEmpty()) {
                        {
                            IconButton(onClick = { viewModel.updateFilterState(filterState.copy(query = "")) }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
                )
            }

            // Content list of groups
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Horizontal Now Playing carousel
                if (uiState.nowPlayingGames.isNotEmpty()) {
                    item {
                        Text(
                            text = "Now Playing",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(uiState.nowPlayingGames) { game ->
                                GameCard(
                                    game = game,
                                    onClick = onGameClick,
                                    modifier = Modifier.width(200.dp)
                                )
                            }
                        }
                    }
                }

                // Expandable categories
                if (uiState.purchasedGames.isNotEmpty()) {
                    item {
                        ExpandableSection(
                            title = "Purchased / Backlog",
                            games = uiState.purchasedGames,
                            onGameClick = onGameClick
                        )
                    }
                }
                
                if (uiState.completedGames.isNotEmpty()) {
                    item {
                        ExpandableSection(
                            title = "Completed",
                            games = uiState.completedGames,
                            onGameClick = onGameClick
                        )
                    }
                }

                if (uiState.abandonedGames.isNotEmpty()) {
                    item {
                        ExpandableSection(title = "Abandoned", games = uiState.abandonedGames, onGameClick = onGameClick)
                    }
                }

                // Empty state message
                if (uiState.nowPlayingGames.isEmpty() && uiState.purchasedGames.isEmpty() && 
                    uiState.completedGames.isEmpty() && uiState.abandonedGames.isEmpty()) {
                    item {
                        Text(
                            text = if (uiState.isFiltering) "No games match your search." else "No games added yet. Tap + to add or Sync in Settings.",
                            modifier = Modifier.padding(32.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

/**
 * Expandable category showing a list of games (Purchased, Completed, etc.).
 */
@Composable
fun ExpandableSection(
    title: String,
    games: List<GameEntity>,
    onGameClick: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {

        // Section header with expand/collapse toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$title (${games.size})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }

        // Show list when expanded
        if (expanded) {
            games.forEach { game ->
                GameListItem(game = game, onClick = onGameClick)
            }
        }
    }
}