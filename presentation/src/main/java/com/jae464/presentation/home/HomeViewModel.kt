package com.jae464.presentation.home

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.Task
import com.jae464.domain.model.toDayOfWeek
import com.jae464.domain.usecase.task.GetTasksByDayOfWeekUseCase
import com.jae464.domain.usecase.progresstask.GetTodayProgressTaskUseCase
import com.jae464.domain.usecase.progresstask.UpdateProgressedTimeUseCase
import com.jae464.domain.usecase.progresstask.UpdateTodayProgressTasksUseCase
import com.jae464.presentation.model.ProgressTaskUiModel
import com.jae464.presentation.model.toProgressTaskUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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

    val progressUiTaskState: StateFlow<ProgressTaskUiState> = combine(
        tasks,
        progressTasks
    ) { tasks, progressTasks ->
        if (tasks == null || progressTasks == null || isUploading) {
            ProgressTaskUiState.Loading
        }
        else {
            Log.d(TAG, "tasks : $tasks progressTasks: $progressTasks")
            val progressTaskIds = progressTasks.map { it.task.id }
            val addProgressTasks = tasks.filter { task -> task.id !in progressTaskIds }
            Log.d(TAG, "Have to Insert Progress Task : $addProgressTasks")

            // 추가할 Task가 있는 경우
            if (addProgressTasks.isNotEmpty()) {
                isUploading = true
                updateProgressTasks(addProgressTasks)
                ProgressTaskUiState.Loading
            }
            else {
                if (progressTasks.isEmpty()) {
                    ProgressTaskUiState.Empty
                }
                else {
                    ProgressTaskUiState.Success(progressTasks.map { it.toProgressTaskUiModel() })
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressTaskUiState.Loading
    )

    private fun updateProgressTasks(tasks: List<Task>) {
        viewModelScope.launch {
            Log.d(TAG, "ProgressTask 업데이트 합니다.")
            updateTodayProgressTasksUseCase(tasks)
            isUploading = false
        }
    }

    fun startProgressTask(id: String) {
        val progressTask = progressTasks.value?.firstOrNull{it.id == id} ?: return

        if (progressingTaskManager.progressingState.value is ProgressingState.Progressing) {
            val progressingTaskId = progressingTaskManager.getCurrentProgressTask()?.id

            stopCurrentProgressingTask()

            if (progressingTaskId != id) {
                val service = Intent(context, ProgressTaskService::class.java)
                context.startService(service)
                progressingTaskManager.startProgressTask(progressTask, context)

            }
        }
        else {
            val service = Intent(context, ProgressTaskService::class.java)
            context.startService(service)
            progressingTaskManager.startProgressTask(progressTask, context)
        }
    }

    private fun stopCurrentProgressingTask() {
        if (progressingTaskManager.progressingState.value is ProgressingState.Progressing) {
            val progressingTaskId = progressingTaskManager.getCurrentProgressTask()?.id ?: return
            val service = Intent(context, ProgressTaskService::class.java)
            context.stopService(service)
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

sealed interface ProgressTaskUiState {
    object Loading: ProgressTaskUiState
    data class Success(val progressTasks: List<ProgressTaskUiModel>): ProgressTaskUiState
    object Empty : ProgressTaskUiState
}