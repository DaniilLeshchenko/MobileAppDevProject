package com.example.gamebacklogmanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gamebacklogmanager.R
import com.example.gamebacklogmanager.data.model.GameStatus
import com.example.gamebacklogmanager.data.remote.model.AchievementUiModel
import com.example.gamebacklogmanager.ui.AppViewModelProvider
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateBack: () -> Unit = {}
) {
    val game by viewModel.uiState.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val isLoadingAchievements by viewModel.isLoadingAchievements.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(game?.title ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteGame()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            game?.let { g ->
                val imageModel = if (g.localBoxImagePath != null) {
                    File(g.localBoxImagePath)
                } else {
                    g.imageUrl
                }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = g.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = g.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Playtime: ${g.progressHours.toInt()} hours",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    if (g.steamAppId != null) {
                        Text(
                            text = "Steam App ID: ${g.steamAppId}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Change Status", style = MaterialTheme.typography.titleMedium)
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            GameStatus.values().forEach { status ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.updateStatus(status) }
                                ) {
                                    RadioButton(
                                        selected = (status == g.status),
                                        onClick = { viewModel.updateStatus(status) }
                                    )
                                    Text(
                                        text = status.name.replace("_", " "),
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Description", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = g.description.ifBlank { "No description available." },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    // Achievements Section
                    if (achievements.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "Achievements", style = MaterialTheme.typography.titleMedium)
                        
                        val unlockedCount = achievements.count { it.isUnlocked }
                        val totalCount = achievements.size
                        val progress = if (totalCount > 0) unlockedCount.toFloat() / totalCount else 0f
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.weight(1f).height(8.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "$unlockedCount / $totalCount", style = MaterialTheme.typography.bodyMedium)
                        }

                        AchievementsList(achievements = achievements)
                    } else if (isLoadingAchievements) {
                        Spacer(modifier = Modifier.height(24.dp))
                         Row(verticalAlignment = Alignment.CenterVertically) {
                             CircularProgressIndicator(modifier = Modifier.size(24.dp))
                             Spacer(modifier = Modifier.width(8.dp))
                             Text("Loading achievements...")
                         }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsList(achievements: List<AchievementUiModel>) {
    var expanded by remember { mutableStateOf(false) }
    // Sort: Unlocked first, then hidden ones last
    val sortedAchievements = remember(achievements) {
        achievements.sortedWith(compareByDescending<AchievementUiModel> { it.isUnlocked }.thenBy { it.isHidden })
    }
    
    // Show top 5 or all if expanded
    val displayList = if (expanded) sortedAchievements else sortedAchievements.take(5)

    Column(modifier = Modifier.fillMaxWidth()) {
        displayList.forEach { achievement ->
            AchievementItem(achievement)
        }
        
        if (achievements.size > 5) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Show Less" else "Show All (${achievements.size})",
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: AchievementUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(achievement.iconUrl)
                .crossfade(true)
                .build(),
            contentDescription = achievement.displayName,
            modifier = Modifier.size(48.dp),
            // Optional: apply saturation 0 if locked, but the API provides a gray icon usually
            colorFilter = if (!achievement.isUnlocked && achievement.iconUrl.contains("gray")) null else if (!achievement.isUnlocked) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = achievement.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (achievement.isUnlocked) FontWeight.Bold else FontWeight.Normal,
                color = if (achievement.isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray
            )
            if (!achievement.isHidden || achievement.isUnlocked) {
                 achievement.description?.let {
                     Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                 }
            } else {
                Text(
                    text = "Hidden Achievement",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}