package com.jae464.presentation.home

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.jae464.presentation.MainActivity
import com.jae464.presentation.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class ProgressTaskWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
): CoroutineWorker(context, params){

    private val progressingTaskManager = ProgressingTaskManager.getInstance()

    override suspend fun doWork(): Result {
        return if (progressingTaskManager.progressingState.value is ProgressingState.Progressing) {
            setForeground(createForegroundInfo())
            while(progressingTaskManager.progressingState.value is ProgressingState.Progressing) {
                if (progressingTaskManager.progressingState.value is ProgressingState.Ready) {
                    break
                }
                progressingTaskManager.tick()
                delay(1000)
            }
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        createChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(progressingTaskManager.getCurrentProgressTask()?.title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)

    }

    private fun createChannel() {
        val name = "일정 관리"
        val description = "현재 진행중인 일정"
        val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
        mChannel.description = description
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    companion object {
        const val NOTIFICATION_ID = 1000
        const val CHANNEL_ID = "DailyTask"
    }
}