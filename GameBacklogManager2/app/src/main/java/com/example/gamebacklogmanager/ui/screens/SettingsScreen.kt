package com.example.gamebacklogmanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gamebacklogmanager.data.repository.UserPreferencesRepository
import com.example.gamebacklogmanager.ui.AppViewModelProvider

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    // Simple state for notification toggle, ideally in ViewModel but for simplicity here:
    val preferencesRepository = remember { UserPreferencesRepository(context) }
    var notificationsEnabled by remember { mutableStateOf(preferencesRepository.isNotificationsEnabled) }
    
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Settings", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Steam Profile Section
            Text(text = "Steam Integration", style = MaterialTheme.typography.titleMedium)
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.steamId,
                    onValueChange = { viewModel.updateSteamId(it) },
                    label = { Text("Your Steam ID (64-bit)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.saveSteamId()
                            keyboardController?.hide()
                        }
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { 
                    viewModel.saveSteamId() 
                    keyboardController?.hide()
                }) {
                    Text("Save")
                }
            }
            
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            
            viewModel.steamProfile?.let { profile ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(profile.avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = profile.personaName, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Steam Connected", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                
                // Sync Button
                Button(
                    onClick = { viewModel.syncLibrary() },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    enabled = !viewModel.isLoading
                ) {
                    Text("Sync Steam Library")
                }
            }
            
            viewModel.syncStatus?.let { status ->
                 Text(
                     text = status, 
                     color = MaterialTheme.colorScheme.primary, 
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(top = 8.dp)
                 )
            }
            
            viewModel.errorMessage?.let { error ->
                 Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(text = "Notifications", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Enable Daily Reminders", modifier = Modifier.weight(1f))
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { 
                        notificationsEnabled = it
                        preferencesRepository.isNotificationsEnabled = it
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "About", style = MaterialTheme.typography.titleMedium)
            Text(text = "Game Backlog Manager v1.0", style = MaterialTheme.typography.bodyMedium)
        }
    }
}