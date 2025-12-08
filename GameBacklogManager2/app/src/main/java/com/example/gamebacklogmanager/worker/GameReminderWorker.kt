package com.example.gamebacklogmanager.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gamebacklogmanager.R
import com.example.gamebacklogmanager.data.local.AppDatabase
import com.example.gamebacklogmanager.data.model.GameStatus
import com.example.gamebacklogmanager.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first

class GameReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val preferencesRepository = UserPreferencesRepository(applicationContext)
        if (!preferencesRepository.isNotificationsEnabled) {
            return Result.success()
        }

        val database = AppDatabase.getDatabase(applicationContext)
        val allGames = database.gameDao().getAllGames().first()
        
        val candidates = allGames.filter { 
            it.status == GameStatus.NOW_PLAYING || 
            it.status == GameStatus.PURCHASED 
        }

        if (candidates.isNotEmpty()) {
            val game = candidates.random()
            showNotification(game.title)
        }

        return Result.success()
    }

    private fun showNotification(gameTitle: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "game_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Game Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Time to Play!")
            .setContentText("Why not play $gameTitle?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}