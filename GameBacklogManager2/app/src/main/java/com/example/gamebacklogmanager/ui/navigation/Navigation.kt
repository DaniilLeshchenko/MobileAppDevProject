package com.example.gamebacklogmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gamebacklogmanager.ui.screens.AddEditGameScreen
import com.example.gamebacklogmanager.ui.screens.CameraScreen
import com.example.gamebacklogmanager.ui.screens.DetailScreen
import com.example.gamebacklogmanager.ui.screens.HomeScreen
import com.example.gamebacklogmanager.ui.screens.LoginScreen
import com.example.gamebacklogmanager.ui.screens.SettingsScreen
import com.example.gamebacklogmanager.ui.screens.StatsScreen
import com.example.gamebacklogmanager.ui.screens.StoreScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Detail : Screen("detail/{gameId}") {
        fun createRoute(gameId: Int) = "detail/$gameId"
    }
    object AddEditGame : Screen("add_edit_game?gameId={gameId}&steamAppId={steamAppId}") {
        fun createRoute(gameId: Int? = null, steamAppId: String? = null) = 
            "add_edit_game?gameId=${gameId ?: ""}&steamAppId=${steamAppId ?: ""}"
    }
    object Stats : Screen("stats")
    object Settings : Screen("settings")
    object Camera : Screen("camera")
    object Store : Screen("store")
}

@Composable
fun GameBacklogNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onGameClick = { gameId ->
                    navController.navigate(Screen.Detail.createRoute(gameId))
                },
                onAddGameClick = {
                    navController.navigate(Screen.AddEditGame.createRoute(null))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onStatsClick = {
                    navController.navigate(Screen.Stats.route)
                },
                onStoreClick = {
                    navController.navigate(Screen.Store.route)
                }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            DetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.AddEditGame.route,
            arguments = listOf(
                navArgument("gameId") { 
                    type = NavType.StringType 
                    nullable = true 
                },
                navArgument("steamAppId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle
            val capturedImage = savedStateHandle.get<String>("captured_image_path")
            
            // Note: Argument processing happens in ViewModel usually or passed here
            val steamAppIdArg = backStackEntry.arguments?.getString("steamAppId")
            
            AddEditGameScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenCamera = { navController.navigate(Screen.Camera.route) },
                capturedImagePath = capturedImage,
                steamAppIdToLoad = steamAppIdArg
            )
        }
        composable(Screen.Camera.route) {
            CameraScreen(
                onImageCaptured = { path ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("captured_image_path", path)
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.Store.route) {
            StoreScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddGame = { steamAppId ->
                    navController.navigate(Screen.AddEditGame.createRoute(null, steamAppId))
                }
            )
        }
    }
}