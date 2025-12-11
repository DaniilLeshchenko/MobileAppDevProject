package com.example.gamebacklogmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamebacklogmanager.ui.AppViewModelProvider

/**
 * Screen displaying summary statistics about user's game library.
 */
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header title
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Total games
            StatCard(
                title = "TOTAL GAMES",
                value = uiState.totalGames.toString(),
                borderColor = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Completed & Backlog
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatCard(
                    title = "COMPLETED",
                    value = uiState.completedGames.toString(),
                    modifier = Modifier.weight(1f),
                    borderColor = MaterialTheme.colorScheme.tertiary // Green
                )
                Spacer(modifier = Modifier.width(16.dp))
                StatCard(
                    title = "BACKLOG",
                    value = uiState.purchasedGames.toString(),
                    modifier = Modifier.weight(1f),
                    borderColor = MaterialTheme.colorScheme.secondary // Purple
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Now playing & Abandoned
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatCard(
                    title = "NOW PLAYING",
                    value = uiState.nowPlayingGames.toString(),
                    modifier = Modifier.weight(1f),
                    borderColor = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                StatCard(
                    title = "ABANDONED",
                    value = uiState.abandonedGames.toString(),
                    modifier = Modifier.weight(1f),
                    borderColor = MaterialTheme.colorScheme.error
                )
            }

            // Total playtime
            Spacer(modifier = Modifier.height(16.dp))
            StatCard(
                title = "TOTAL HOURS",
                value = "%.1f".format(uiState.totalHours),
                borderColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * Reusable card component for displaying a single numeric stat.
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = borderColor
            )
        }
    }
}