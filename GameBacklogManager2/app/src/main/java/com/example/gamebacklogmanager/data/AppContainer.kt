package com.example.gamebacklogmanager.data

import android.content.Context
import com.example.gamebacklogmanager.data.local.AppDatabase
import com.example.gamebacklogmanager.data.remote.SteamApiService
import com.example.gamebacklogmanager.data.remote.SteamStoreService
import com.example.gamebacklogmanager.data.repository.GameRepository
import com.example.gamebacklogmanager.data.repository.SteamRepository
import com.example.gamebacklogmanager.data.repository.UserPreferencesRepository
import com.example.gamebacklogmanager.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Dependency container that exposes repositories used across the app.
 */
interface AppContainer {
    val gameRepository: GameRepository
    val steamRepository: SteamRepository
    val userPreferencesRepository: UserPreferencesRepository
}

/**
 * Default implementation of AppContainer.
 * Creates Retrofit services, Room database, and repositories.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    /** Logging for all network requests (useful for debugging). */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /** Shared OkHttp client for Steam API and Store API. */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    /** Retrofit instance for Steam Web API. */
    private val steamRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.STEAM_API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** Retrofit instance for the Steam Store API. */
    private val steamStoreRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.STEAM_STORE_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** Provides GameRepository (Room + Store API). */
    override val gameRepository: GameRepository by lazy {
        GameRepository(
            gameDao = AppDatabase.getDatabase(context).gameDao(),
            steamStoreService = steamStoreService
        )
    }

    /** Lazily created Steam API service. */
    private val steamApiService: SteamApiService by lazy {
        steamRetrofit.create(SteamApiService::class.java)
    }

    /** Lazily created Steam Store API service. */
    private val steamStoreService: SteamStoreService by lazy {
        steamStoreRetrofit.create(SteamStoreService::class.java)
    }


    /** Provides SteamRepository (Web API + Store API). */
    override val steamRepository: SteamRepository by lazy {
        SteamRepository(steamApiService, steamStoreService)
    }

    /** Provides user preference storage. */
    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }
}