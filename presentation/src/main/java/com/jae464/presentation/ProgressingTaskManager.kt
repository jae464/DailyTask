package com.jae464.presentation

import com.jae464.domain.model.ProgressTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ProgressingState {
    object Ready : ProgressingState
    data class Progressing(val progressTask: ProgressTask): ProgressingState
}

@Singleton
class ProgressingTaskManager @Inject constructor() {

    private var _progressingState: MutableStateFlow<ProgressingState> = MutableStateFlow(
        ProgressingState.Ready
    )
    val progressingState: StateFlow<ProgressingState>
        get() = _progressingState.asStateFlow()

    private var progressTask: ProgressTask? = null

    fun startProgressTask(progressTask: ProgressTask) {
        _progressingState.value = ProgressingState.Progressing(progressTask)
        this.progressTask = progressTask
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
}
