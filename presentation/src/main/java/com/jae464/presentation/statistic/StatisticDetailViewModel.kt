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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getProgressTaskByTaskIdUseCase: GetProgressTaskByTaskIdUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<StatisticDetailUiState> =  MutableStateFlow(StatisticDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val task = savedStateHandle.getStateFlow(key = "taskId", initialValue = "")
        .flatMapLatest {
            if (it.isNotBlank()) {
                getTaskUseCase(it)
            }
            else flowOf(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    init {
        val taskId = savedStateHandle["taskId"] ?: ""
        val startDateStr = savedStateHandle["startDate"] ?: ""
        val endDateStr = savedStateHandle["endDate"] ?: ""
        val usePeriod = startDateStr.isNotEmpty() && endDateStr.isNotEmpty()
        val startDate = if (usePeriod) LocalDate.parse(startDateStr) else LocalDate.now()
        val endDate = if (usePeriod) LocalDate.parse(endDateStr) else LocalDate.now()

        Log.d("StatisticDetailViewModel", "taskId = ${taskId} startDate = ${startDate} endDate = ${endDate}")
        viewModelScope.launch {
            getProgressTaskByTaskIdUseCase(usePeriod = usePeriod, taskId, startDate, endDate)
                .collectLatest {
                    Log.d("StatisticDetailViewModel", it.toString())
                    _uiState.value = StatisticDetailUiState.Success(it)
                }
        }

    }
}

sealed interface StatisticDetailUiState {
    object Loading : StatisticDetailUiState
    data class Success(val progressTasks: List<ProgressTask>) : StatisticDetailUiState
}