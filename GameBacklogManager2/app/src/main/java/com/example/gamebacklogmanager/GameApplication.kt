package com.example.gamebacklogmanager

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.gamebacklogmanager.data.AppContainer
import com.example.gamebacklogmanager.data.DefaultAppContainer
import com.example.gamebacklogmanager.worker.GameReminderWorker
import java.util.concurrent.TimeUnit

class GameApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        setupWorker()
    }

    private fun setupWorker() {
        val workManager = WorkManager.getInstance(this)

        val immediateRequest = OneTimeWorkRequestBuilder<GameReminderWorker>()
            .build()
        workManager.enqueue(immediateRequest)

        val periodicRequest = PeriodicWorkRequestBuilder<GameReminderWorker>(1, TimeUnit.HOURS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "GameReminderWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicRequest
        )
    }
}