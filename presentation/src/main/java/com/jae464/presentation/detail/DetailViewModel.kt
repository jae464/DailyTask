package com.jae464.presentation.detail

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.usecase.progresstask.GetProgressTaskUseCase
import com.jae464.domain.usecase.progresstask.UpdateProgressedTimeUseCase
import com.jae464.domain.usecase.progresstask.UpdateTodayMemoUseCase
import com.jae464.presentation.ProgressTaskService
import com.jae464.presentation.ProgressingState
import com.jae464.presentation.ProgressingTaskManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val progressTaskState: ProgressTaskState = ProgressTaskState.Loading
)

sealed interface ProgressTaskState {
    data object Loading: ProgressTaskState
    data class Success(val progressTask: ProgressTask, val isProgressing: Boolean): ProgressTaskState
}

sealed interface DetailUiEvent {
    data object StartProgressTask : DetailUiEvent
    data object StopProgressTask: DetailUiEvent
    data class SetTodayMemo(val todayMemo: String): DetailUiEvent
}

sealed interface DetailUiEffect {
    data object SetTodayMemoCompleted: DetailUiEffect
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val progressingTaskManager: ProgressingTaskManager,
    private val savedStateHandle: SavedStateHandle,
    private val getProgressTaskUseCase: GetProgressTaskUseCase,
    private val updateProgressedTimeUseCase: UpdateProgressedTimeUseCase,
    private val updateTodayMemoUseCase: UpdateTodayMemoUseCase
) : ViewModel() {

    private val TAG = "DetailViewModel"

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<DetailUiEffect>()
    val uiEffect: SharedFlow<DetailUiEffect> = _uiEffect.asSharedFlow()

    private lateinit var progressTask: ProgressTask
    private val progressingTask = progressingTaskManager.progressingState
    private var progressTaskServiceIntent: Intent = Intent(context, ProgressTaskService::class.java)

    init {
        getProgressTask()
    }

    fun handleEvent(event: DetailUiEvent) {
        when(event) {
            is DetailUiEvent.StartProgressTask -> {
                startProgressTask()
            }
            is DetailUiEvent.StopProgressTask -> {
                stopProgressTask()
            }
            is DetailUiEvent.SetTodayMemo -> {
                updateTodayMemo(event.todayMemo)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getProgressTask() {
        savedStateHandle.getStateFlow(key = PROGRESS_TASK_ID, "").flatMapLatest {
            if (it.isBlank()) {
                flowOf(ProgressTaskState.Loading)
            }
            else {
                combine(getProgressTaskUseCase(it), progressingTask) { progressTask, progressingTask ->
                    if (progressTask == null) ProgressTaskState.Loading
                    else {
                        if (progressingTask is ProgressingState.Progressing && progressingTask.progressTask.id == progressTask.id) {
                            ProgressTaskState.Success(progressTask.copy(progressedTime = progressingTask.progressTask.progressedTime), true)
                        }
                        else {
                            ProgressTaskState.Success(progressTask, false)
                        }
                    }
                }
            }
        }.onEach { progressTaskState ->
            if (progressTaskState is ProgressTaskState.Success) {
                this.progressTask = progressTaskState.progressTask
                _uiState.update {  state ->
                    state.copy(progressTaskState = progressTaskState)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun startProgressTask() {
        stopProgressTask()
        context.startService(progressTaskServiceIntent)
        progressingTaskManager.startProgressTask(progressTask)
    }

    private fun stopProgressTask() {
        if (progressingTaskManager.progressingState.value is ProgressingState.Progressing) {
            val progressingTaskId = progressingTaskManager.getCurrentProgressTask()?.id ?: return
            context.stopService(progressTaskServiceIntent)
            val progressedTime = progressingTaskManager.stopProgressTask()
            viewModelScope.launch {
                updateProgressedTimeUseCase(progressingTaskId, progressedTime)
            }
        }
    }

    private fun updateTodayMemo(todayMemo: String) {
        val progressTaskId = progressTask.id
        viewModelScope.launch {
            updateTodayMemoUseCase(progressTaskId, todayMemo)
            _uiEffect.emit(DetailUiEffect.SetTodayMemoCompleted)
        }
    }

    companion object {
        const val PROGRESS_TASK_ID = "progressTaskId"
    }
}
