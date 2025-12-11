package com.example.gamebacklogmanager.data.repository

import android.content.Context
/**
 * Simple repository for storing user preferences using SharedPreferences.
 */
class UserPreferencesRepository(context: Context) {
    /** Local storage for user settings (Steam ID, notifications, etc.). */
    private val sharedPreferences = context.getSharedPreferences("game_backlog_prefs", Context.MODE_PRIVATE)

    /** Saved Steam ID used for API requests. */
    var steamId: String
        get() = sharedPreferences.getString("steam_id", "") ?: ""
        set(value) = sharedPreferences.edit().putString("steam_id", value).apply()

    /** Whether notifications are enabled for the user. */
    var isNotificationsEnabled: Boolean
        get() = sharedPreferences.getBoolean("notifications_enabled", false)
        set(value) = sharedPreferences.edit().putBoolean("notifications_enabled", value).apply()
}