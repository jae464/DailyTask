package com.jae464.presentation.statistic

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.usecase.progresstask.GetProgressTaskByTaskIdUseCase
import com.jae464.domain.usecase.task.GetTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class StatisticDetailUiState(
    val title: String = "",
    val progressTasks: ProgressTaskState = ProgressTaskState.Loading,
    val selectedLocalDate: LocalDate = LocalDate.now()
)

sealed interface StatisticDetailUiEvent {
    data class SetSelectedLocalDate(val localDate: LocalDate) : StatisticDetailUiEvent
}

sealed interface StatisticDetailUiEffect {

}

sealed interface ProgressTaskState {
    data class Success(val progressTasks: List<ProgressTask>) : ProgressTaskState
    data object Loading : ProgressTaskState
}

@HiltViewModel
class StatisticDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getProgressTaskByTaskIdUseCase: GetProgressTaskByTaskIdUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : ViewModel() {

    private val taskId = savedStateHandle.get<String>(TASK_ID_KEY) ?: ""
    private val startDateStr = savedStateHandle.get<String>(START_DATE_KEY) ?: ""
    private val endDateStr = savedStateHandle.get<String>(END_DATE_KEY) ?: ""

    private val _uiState = MutableStateFlow(StatisticDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<StatisticDetailUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        fetchTaskTitle()
        fetchProgressTasks()
    }

    fun handleEvent(event: StatisticDetailUiEvent) {
        when (event) {
            is StatisticDetailUiEvent.SetSelectedLocalDate -> {
                setSelectedLocalDate(event.localDate)
            }
        }
    }

    private fun fetchTaskTitle() {
        getTaskUseCase(taskId).onEach {
            _uiState.update { state ->
                state.copy(title = it.title)
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchProgressTasks() {
        val usePeriod = startDateStr.isNotEmpty() && endDateStr.isNotEmpty()
        val startDate = if (usePeriod) LocalDate.parse(startDateStr) else LocalDate.now()
        val endDate = if (usePeriod) LocalDate.parse(endDateStr) else LocalDate.now()
        Log.d(
            "StatisticDetailViewModel",
            "taskId = ${taskId} startDate = ${startDate} endDate = ${endDate}"
        )
        getProgressTaskByTaskIdUseCase(usePeriod, taskId, startDate, endDate).onEach {
            val filteredProgressTasks = it.filter { p -> p.progressedTime > 0 }
            _uiState.update { state ->
                state.copy(
                    progressTasks = ProgressTaskState.Success(filteredProgressTasks),
                    selectedLocalDate = filteredProgressTasks.last().createdAt
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun setSelectedLocalDate(localDate: LocalDate) {
        _uiState.update { state ->
            state.copy(selectedLocalDate = localDate)
        }
    }

    companion object {
        const val TAG = "StatisticDetailViewModel"
        const val TASK_ID_KEY = "taskId"
        const val START_DATE_KEY = "startDate"
        const val END_DATE_KEY = "endDate"
    }
}
