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

interface AppContainer {
    val gameRepository: GameRepository
    val steamRepository: SteamRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val steamRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.STEAM_API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val steamStoreRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.STEAM_STORE_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val steamApiService: SteamApiService by lazy {
        steamRetrofit.create(SteamApiService::class.java)
    }

    private val steamStoreService: SteamStoreService by lazy {
        steamStoreRetrofit.create(SteamStoreService::class.java)
    }

    override val gameRepository: GameRepository by lazy {
        GameRepository(
            gameDao = AppDatabase.getDatabase(context).gameDao(),
            steamStoreService = steamStoreService
        )
    }

    override val steamRepository: SteamRepository by lazy {
        SteamRepository(steamApiService, steamStoreService)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }
}