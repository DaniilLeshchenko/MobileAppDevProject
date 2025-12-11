package com.example.gamebacklogmanager.utils

/**
 * A container for application-wide constant values.
 */
object Constants {

    /**
     *  Steam Web API key.
     */
    const val STEAM_API_KEY = "67DC1550F21511F4F021BC7E4D0F86CB"

    /**
     * Base URL for the official Steam Web API.
     * Used by Retrofit to fetch owned games, player profile, achievements, etc.
     */
    const val STEAM_API_BASE_URL = "https://api.steampowered.com/"

    /**
     * Base URL for the Steam Store API.
     * Used to search games and retrieve store-related metadata.
     */
    const val STEAM_STORE_BASE_URL = "https://store.steampowered.com/"
}