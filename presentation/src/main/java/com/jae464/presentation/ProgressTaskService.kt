package com.jae464.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.jae464.domain.usecase.progresstask.UpdateProgressedTimeUseCase
import com.jae464.presentation.detail.DEEP_LINK_URI_PATTERN
import com.jae464.presentation.detail.DETAIL_ROUTE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ProgressTaskService : LifecycleService() {

    @Inject
    lateinit var updateProgressedTimeUseCase: UpdateProgressedTimeUseCase
    @Inject
    lateinit var progressingTaskManager: ProgressingTaskManager

    private var timerJob: Job? = null
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        setForeground()
    }

    private fun setForeground() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = Intent.ACTION_VIEW
            data = "$DEEP_LINK_URI_PATTERN/$DETAIL_ROUTE/${progressingTaskManager.getCurrentProgressTask()?.id}".toUri()
            component = ComponentName(
                packageName,
                "com.jae464.presentation.MainActivity"
            )
        }

        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        createChannel()

        notificationBuilder = NotificationCompat.Builder(applicationContext,
            CHANNEL_ID
        )
            .setContentTitle("현재 일정이 진행중입니다.")
            .setContentText(progressingTaskManager.getCurrentProgressTask()?.title ?: "")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSilent(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        notificationBuilder.build()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = lifecycleScope.launch {
            while (true) {
                if (progressingTaskManager.progressingState.value is ProgressingState.Progressing) {
                    val progressingTask = ((progressingTaskManager.progressingState.value as ProgressingState.Progressing).progressTask)
                    if (progressingTask.progressedTime % 10 == 0) {
                        updateProgressedTimeUseCase(progressingTask.id, progressingTask.progressedTime)
                    }
                    delay(1000)
                    progressingTaskManager.tick()
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    private fun createChannel() {
        val name = "일정 관리"
        val description = "현재 진행중인 일정"
        val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
        mChannel.description = description
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTimer()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 500
        const val CHANNEL_ID = "DailyTask"
    }

}
