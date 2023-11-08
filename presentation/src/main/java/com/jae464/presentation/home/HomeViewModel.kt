package com.jae464.presentation.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import com.jae464.domain.model.toDayOfWeek
import com.jae464.domain.repository.TaskRepository
import com.jae464.domain.usecase.GetTasksByDayOfWeekUseCase
import com.jae464.domain.usecase.GetTodayProgressTaskUseCase
import com.jae464.domain.usecase.UpdateProgressedTimeUseCase
import com.jae464.domain.usecase.UpdateTodayProgressTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTasksByDayOfWeekUseCase: GetTasksByDayOfWeekUseCase,
    private val getTodayProgressTaskUseCase: GetTodayProgressTaskUseCase,
    private val updateTodayProgressTasksUseCase: UpdateTodayProgressTasksUseCase,
    private val updateProgressedTimeUseCase: UpdateProgressedTimeUseCase
) : ViewModel() {

    private val progressingTaskManager = ProgressingTaskManager.getInstance()
    val progressingTask = progressingTaskManager.progressingState

    private var isUploading = false

    val tasks =
        getTasksByDayOfWeekUseCase(LocalDate.now().dayOfWeek.toDayOfWeek())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    private val progressTasks =
        getTodayProgressTaskUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val progressTaskState: StateFlow<ProgressTaskState> = combine(
        tasks,
        progressTasks
    ) { tasks, progressTasks ->
        if (tasks == null || progressTasks == null || isUploading) {
            ProgressTaskState.Loading
        }
        else {
            Log.d(TAG, "tasks : $tasks progressTasks: $progressTasks")
            val progressTaskIds = progressTasks.map { it.task.id }
            val addProgressTasks = tasks.filter { task -> task.id !in progressTaskIds }
            Log.d(TAG, "Have to Insert Progress Task : $addProgressTasks")

            if (addProgressTasks.isNotEmpty()) {
                isUploading = true
                updateProgressTasks(addProgressTasks)
                ProgressTaskState.Loading
            }
            else {
                ProgressTaskState.Success(progressTasks.map { it.toProgressTaskUiModel() })
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressTaskState.Loading
    )

    private fun updateProgressTasks(tasks: List<Task>) {
        viewModelScope.launch {
            Log.d(TAG, "ProgressTask 업데이트 합니다.")
            updateTodayProgressTasksUseCase(tasks)
            isUploading = false
        }
    }

    fun startProgressTask(id: String, context: Context) {
        val progressTask = progressTasks.value?.firstOrNull{it.id == id} ?: return

        if (progressingTaskManager.progressingState.value is ProgressingState.Progressing) {
            val progressingTaskId = progressingTaskManager.getCurrentProgressTask()?.id

            stopCurrentProgressingTask()

            if (progressingTaskId != id) {
                progressingTaskManager.startProgressTask(progressTask, context)
            }
        }
        else {
            progressingTaskManager.startProgressTask(progressTask, context)
        }
    }

    private fun stopCurrentProgressingTask() {
        if (progressingTaskManager.progressingState.value is ProgressingState.Progressing) {
            val progressingTaskId = progressingTaskManager.getCurrentProgressTask()?.id ?: return
            val progressedTime = progressingTaskManager.stopProgressTask()
            viewModelScope.launch {
                updateProgressedTimeUseCase(progressingTaskId, progressedTime)
            }
        }
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}