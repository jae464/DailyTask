package com.jae464.presentation.home

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val progressTaskState: ProgressTaskState = ProgressTaskState.Loading
)

sealed interface HomeUiEvent {

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

    private val _uiState = MutableStateFlow(HomeUiState(ProgressTaskState.Loading))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<HomeUiEffect>()
    val effect = _effect.asSharedFlow()

    private val progressingTaskManager = ProgressingTaskManager.getInstance()
    val progressingTask = progressingTaskManager.progressingState

    init {
        getProgressTasks()
    }

    private fun getProgressTasks() {
        combine(
            getTodayProgressTaskUseCase(),
            getTasksByDayOfWeekUseCase(LocalDate.now().dayOfWeek.toDayOfWeek()),
            progressingTask
        ) { progressTasks, tasks, progressingTask ->
            Log.d("HomeViewModel", "getProgressTasks: $progressTasks $tasks $progressingTask")
            val progressTaskIds = progressTasks.map { it.task.id }
            val addProgressTasks = tasks.filter { task -> task.id !in progressTaskIds }

            if (addProgressTasks.isNotEmpty()) {
                updateProgressTasks(addProgressTasks)
                ProgressTaskState.Loading
            }

        }.onEach {
            Log.d(TAG, "getProgressTasks: $it")
        }.launchIn(viewModelScope)
    }



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
        progressTasks,
        progressingTask
    ) { tasks, progressTasks, progressingTask ->
        if (tasks == null || progressTasks == null || isUploading) {
            ProgressTaskUiState.Loading
        } else {
            val progressTaskIds = progressTasks.map { it.task.id }
            val addProgressTasks = tasks.filter { task -> task.id !in progressTaskIds }

            if (addProgressTasks.isNotEmpty()) {
                isUploading = true
                updateProgressTasks(addProgressTasks)
                ProgressTaskUiState.Loading
            } else {
                if (progressTasks.isEmpty()) {
                    ProgressTaskUiState.Empty
                } else {
                    if (progressingTask is ProgressingState.Progressing) {
                        ProgressTaskUiState.Success(progressTasks.map {
                            if (it.id == progressingTask.progressTask.id) {
                                progressingTask.progressTask
                            } else {
                                it.toProgressTaskUiModel()
                            }
                        })
                    } else {
                        ProgressTaskUiState.Success(progressTasks.map { it.toProgressTaskUiModel() })
                    }
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
            updateTodayProgressTasksUseCase(tasks)
            isUploading = false
        }
    }

    fun startProgressTask(id: String) {
        val progressTask = progressTasks.value?.firstOrNull { it.id == id } ?: return

        if (progressingTaskManager.progressingState.value is ProgressingState.Progressing) {
            val progressingTaskId = progressingTaskManager.getCurrentProgressTask()?.id

            stopCurrentProgressingTask()

            if (progressingTaskId != id) {
                val service = Intent(context, ProgressTaskService::class.java)
                context.startService(service)
                progressingTaskManager.startProgressTask(progressTask, context)

            }
        } else {
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

sealed interface ProgressTaskState {
    object Loading : ProgressTaskState
    data class Success(val progressTasks: List<ProgressTaskUiModel>) : ProgressTaskState
    object Empty : ProgressTaskState
}

sealed interface ProgressTaskUiState {
    object Loading : ProgressTaskUiState
    data class Success(val progressTasks: List<ProgressTaskUiModel>) : ProgressTaskUiState
    object Empty : ProgressTaskUiState
}