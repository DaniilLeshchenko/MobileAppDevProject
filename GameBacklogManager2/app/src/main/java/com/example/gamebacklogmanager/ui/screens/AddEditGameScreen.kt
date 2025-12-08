package com.example.gamebacklogmanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gamebacklogmanager.R
import com.example.gamebacklogmanager.data.model.GameStatus
import com.example.gamebacklogmanager.ui.AppViewModelProvider
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGameScreen(
    viewModel: AddEditGameViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateBack: () -> Unit = {},
    onOpenCamera: () -> Unit = {},
    capturedImagePath: String? = null,
    steamAppIdToLoad: String? = null
) {
    val uiState = viewModel.gameUiState
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(capturedImagePath) {
        if (capturedImagePath != null) {
            viewModel.updateLocalBoxImage(capturedImagePath)
        }
    }
    
    LaunchedEffect(steamAppIdToLoad) {
        if (!steamAppIdToLoad.isNullOrBlank()) {
            viewModel.searchGameByAppId(steamAppIdToLoad)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.id == 0) "Add Game" else "Edit Game") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Steam Import Section - Store Search logic removed (moved to StoreScreen)
            // Kept specific App ID search
            Text(text = "Find on Steam by ID", style = MaterialTheme.typography.titleMedium)
            
            // Search Specific Game by App ID
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.gameAppIdQuery,
                    onValueChange = { viewModel.updateGameAppIdQuery(it) },
                    label = { Text("Game App ID (e.g. 730)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.searchGameByAppId()
                            keyboardController?.hide()
                        }
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { 
                    viewModel.searchGameByAppId()
                    keyboardController?.hide()
                }) {
                    Text("Find")
                }
            }

            if (viewModel.isLoadingSteamGames) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp))
            }
            
            viewModel.searchError?.let { error ->
                 Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Game Details", style = MaterialTheme.typography.titleMedium)

            // Box Art Image
            if (uiState.localBoxImagePath != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(File(uiState.localBoxImagePath))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Box Art",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Fit
                )
            } else if (uiState.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Header Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background)
                )
            }

            Button(
                onClick = onOpenCamera,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(if (uiState.localBoxImagePath == null) "Take Photo of Box" else "Retake Photo")
            }

            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateUiState(uiState.copy(title = it)) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateUiState(uiState.copy(description = it)) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            OutlinedTextField(
                value = uiState.progressHours,
                onValueChange = { viewModel.updateUiState(uiState.copy(progressHours = it)) },
                label = { Text("Hours Played") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Status")
            GameStatus.values().forEach { status ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateUiState(uiState.copy(status = status)) }
                ) {
                    RadioButton(
                        selected = (status == uiState.status),
                        onClick = { viewModel.updateUiState(uiState.copy(status = status)) }
                    )
                    Text(
                        text = status.name.replace("_", " "),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { 
                    viewModel.saveGame()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.title.isNotBlank()
            ) {
                Text("Save Game")
            }
        }
    }
}