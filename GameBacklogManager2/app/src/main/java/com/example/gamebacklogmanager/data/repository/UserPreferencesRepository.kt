package com.example.gamebacklogmanager.data.repository

import android.content.Context

class UserPreferencesRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("game_backlog_prefs", Context.MODE_PRIVATE)

    var steamId: String
        get() = sharedPreferences.getString("steam_id", "") ?: ""
        set(value) = sharedPreferences.edit().putString("steam_id", value).apply()

    var isNotificationsEnabled: Boolean
        get() = sharedPreferences.getBoolean("notifications_enabled", false)
        set(value) = sharedPreferences.edit().putBoolean("notifications_enabled", value).apply()
}