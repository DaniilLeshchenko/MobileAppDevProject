package com.example.gamebacklogmanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.ui.navigation.GameBacklogNavHost
import com.example.gamebacklogmanager.ui.theme.GameBacklogManagerTheme
import com.example.gamebacklogmanager.utils.ShakeDetector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private lateinit var shakeDetector: ShakeDetector
    private var showRecommendationDialog by mutableStateOf(false)
    private var recommendedGame by mutableStateOf<GameEntity?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        shakeDetector = ShakeDetector(this)

        setContent {
            GameBacklogManagerTheme {
                val context = LocalContext.current
                
                // Request Notification Permission for Android 13+
                if (Build.VERSION.SDK_INT >= 33) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            // Handle permission granted/denied if needed
                        }
                    )
                    
                    LaunchedEffect(key1 = true) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameBacklogNavHost()
                    
                    if (showRecommendationDialog && recommendedGame != null) {
                        val description = recommendedGame?.description
                        val displayDescription = if (!description.isNullOrBlank() && description != "Imported from Steam") {
                            "\n\n${description.take(100)}..."
                        } else {
                            ""
                        }

                        AlertDialog(
                            onDismissRequest = { showRecommendationDialog = false },
                            title = { Text("What to play?") },
                            text = { 
                                Text("Fate suggests you play: ${recommendedGame?.title}$displayDescription") 
                            },
                            confirmButton = {
                                Button(onClick = { showRecommendationDialog = false }) {
                                    Text("Cool!")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start {
            handleShake()
        }
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.stop()
    }

    private fun handleShake() {
        if (showRecommendationDialog) return

        val repository = (application as GameApplication).container.gameRepository

        lifecycleScope.launch {
            val games = repository.getAllGames().first()
            if (games.isNotEmpty()) {
                recommendedGame = games.random()
                showRecommendationDialog = true
            }
        }
    }
}