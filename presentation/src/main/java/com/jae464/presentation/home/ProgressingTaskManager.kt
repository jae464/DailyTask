package com.jae464.presentation.home

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jae464.domain.model.ProgressTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgressingTaskManager {

    private var _progressingState: MutableStateFlow<ProgressingState> = MutableStateFlow(ProgressingState.Ready)
    val progressingState: StateFlow<ProgressingState>
        get() = _progressingState.asStateFlow()

    private var progressTask: ProgressTask? = null

    fun startProgressTask(progressTask: ProgressTask, context: Context) {
        _progressingState.value = ProgressingState.Progressing(progressTask)
        this.progressTask = progressTask

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = OneTimeWorkRequestBuilder<ProgressTaskWorker>()
            .setConstraints(constraints)
            .build()
        val workManager = WorkManager.getInstance(context)
        workManager.beginUniqueWork(
            "taskWorker",
            ExistingWorkPolicy.KEEP,
            request
        )
            .enqueue()
    }

    fun tick() {
        if (progressingState.value is ProgressingState.Progressing) {
            if (this.progressTask != null) {
                this.progressTask = this.progressTask!!.copy(progressedTime = progressTask!!.progressedTime + 1)
                _progressingState.value = ProgressingState.Progressing(progressTask!!)
            }
        }
    }

    fun stopProgressTask(): Int {
        _progressingState.value = ProgressingState.Ready
        val progressedTime = progressTask?.progressedTime ?: 0
        progressTask = null
        return progressedTime
    }

    fun getCurrentProgressTask(): ProgressTask? {
        return this.progressTask
    }

    companion object {
        private var INSTANCE: ProgressingTaskManager? = null

        @Synchronized
        fun getInstance(): ProgressingTaskManager {
            return INSTANCE ?: ProgressingTaskManager().also {
                INSTANCE = it
            }
        }
    }
}

sealed interface ProgressingState {
    object Ready : ProgressingState
    data class Progressing(val progressTask: ProgressTask): ProgressingState
}