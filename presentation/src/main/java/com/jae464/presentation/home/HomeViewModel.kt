package com.jae464.presentation.home

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import com.jae464.domain.model.toDayOfWeek
import com.jae464.domain.usecase.progresstask.GetTodayProgressTaskUseCase
import com.jae464.domain.usecase.progresstask.UpdateProgressedTimeUseCase
import com.jae464.domain.usecase.progresstask.UpdateTodayProgressTasksUseCase
import com.jae464.domain.usecase.task.GetTasksByDayOfWeekUseCase
import com.jae464.presentation.model.ProgressTaskUiModel
import com.jae464.presentation.model.toProgressTaskUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val progressTaskState: ProgressTaskState = ProgressTaskState.Loading,
)

sealed interface HomeUiEvent {
    data class StartProgressTask(val id: String) : HomeUiEvent
}

sealed interface HomeUiEffect {

}

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getTasksByDayOfWeekUseCase: GetTasksByDayOfWeekUseCase,
    private val getTodayProgressTaskUseCase: GetTodayProgressTaskUseCase,
    private val updateTodayProgressTasksUseCase: UpdateTodayProgressTasksUseCase,
    private val updateProgressedTimeUseCase: UpdateProgressedTimeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<HomeUiEffect>()
    val effect = _effect.asSharedFlow()

    private val progressingTaskManager = ProgressingTaskManager.getInstance()
    private val progressingTask = progressingTaskManager.progressingState

    private var isUploading = false

    init {
        getProgressTasks()
    }

    fun handleEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.StartProgressTask -> {
                startProgressTask(event.id)
            }
        }
    }

    private fun getProgressTasks() {
        combine(
            getTodayProgressTaskUseCase(),
            getTasksByDayOfWeekUseCase(LocalDate.now().dayOfWeek.toDayOfWeek()),
            progressingTask
        ) { progressTasks, tasks, progressingTask ->

            val progressTaskIds = progressTasks.map { it.task.id }
            val addProgressTasks = tasks.filter { task -> task.id !in progressTaskIds }

            if (addProgressTasks.isNotEmpty() && !isUploading) {
                isUploading = true
                updateProgressTasks(addProgressTasks)
                ProgressTaskState.Loading
            } else {
                if (progressTasks.isEmpty()) {
                    ProgressTaskState.Empty
                } else {
                    if (progressingTask is ProgressingState.Progressing) {
                        ProgressTaskState.Success(
                            progressTasks = progressTasks.map {
                                if (it.id == progressingTask.progressTask.id) {
                                    progressingTask.progressTask
                                } else {
                                    it
                                }
                            },
                            progressingTaskId = progressingTask.progressTask.id
                        )
                    } else {
                        ProgressTaskState.Success(
                            progressTasks = progressTasks,
                            progressingTaskId = ""
                        )
                    }
                }
            }
        }.onEach {
            _uiState.update { state ->
                state.copy(progressTaskState = it)
            }
        }.launchIn(viewModelScope)
    }

    private fun updateProgressTasks(tasks: List<Task>) {
        viewModelScope.launch {
            updateTodayProgressTasksUseCase(tasks)
            isUploading = false
        }
    }

    private fun startProgressTask(id: String) {
        val progressTaskState = uiState.value.progressTaskState
        val progressingTaskState = progressingTaskManager.progressingState.value
        var progressingTaskId = ""

        if (progressTaskState is ProgressTaskState.Success) {
            val progressTask = progressTaskState.progressTasks.firstOrNull { it.id == id } ?: return

            if (progressingTaskState is ProgressingState.Progressing) {
                progressingTaskId = progressingTaskState.progressTask.id
                stopCurrentProgressingTask()
            }

            if (progressingTaskId != id) {
                val service = Intent(context, ProgressTaskService::class.java)
                context.startService(service)
                progressingTaskManager.startProgressTask(progressTask)
            }
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

sealed interface ProgressTaskState {
    object Loading : ProgressTaskState
    data class Success(val progressTasks: List<ProgressTask>, val progressingTaskId: String) :
        ProgressTaskState

    object Empty : ProgressTaskState
}
