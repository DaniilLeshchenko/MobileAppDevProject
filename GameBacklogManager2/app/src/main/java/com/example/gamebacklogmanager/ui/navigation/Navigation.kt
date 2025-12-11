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

/**
 * Defines all app navigation routes.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")

    /** Detail screen with a required gameId argument. */
    object Detail : Screen("detail/{gameId}") {
        fun createRoute(gameId: Int) = "detail/$gameId"
    }

    /** Add/Edit screen with optional gameId and steamAppId. */
    object AddEditGame : Screen("add_edit_game?gameId={gameId}&steamAppId={steamAppId}") {
        fun createRoute(gameId: Int? = null, steamAppId: String? = null) = 
            "add_edit_game?gameId=${gameId ?: ""}&steamAppId=${steamAppId ?: ""}"
    }
    object Stats : Screen("stats")
    object Settings : Screen("settings")
    object Camera : Screen("camera")
    object Store : Screen("store")
}

/**
 * Main navigation graph for the application.
 */
@Composable
fun GameBacklogNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        /** Login to Home navigation. */
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        /** Home screen with navigation to detail, add/edit, stats, etc. */
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
        /** Detail screen (requires gameId). */
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            DetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        /** Add/Edit Game screen with optional arguments. */
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
            
            val steamAppIdArg = backStackEntry.arguments?.getString("steamAppId")
            
            AddEditGameScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenCamera = { navController.navigate(Screen.Camera.route) },
                capturedImagePath = capturedImage,
                steamAppIdToLoad = steamAppIdArg
            )
        }
        /** Camera screen returns captured image via savedStateHandle. */
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
        /** Stats screen (read-only). */
        composable(Screen.Stats.route) {
            StatsScreen()
        }
        /** Settings screen. */
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        /** Steam Store search screen. */
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