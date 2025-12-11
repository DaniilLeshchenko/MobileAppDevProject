package com.example.gamebacklogmanager

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.gamebacklogmanager.data.AppContainer
import com.example.gamebacklogmanager.data.DefaultAppContainer
import com.example.gamebacklogmanager.worker.GameReminderWorker
import java.util.concurrent.TimeUnit
// This class initializes the app dependency container and configures a periodic
// WorkManager task used for sending game reminder notifications.
class GameApplication : Application() {
    lateinit var container: AppContainer // Used to provide repositories across the app

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this) // Initialize dependency container
        setupWorker() // Start reminder worker
    }

    private fun setupWorker() {
        val workManager = WorkManager.getInstance(this)

        // Periodic worker request — runs GameReminderWorker every 20 minutes
        // Used to send game reminder notifications
        val periodicRequest = PeriodicWorkRequestBuilder<GameReminderWorker>(20, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "GameReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }
}