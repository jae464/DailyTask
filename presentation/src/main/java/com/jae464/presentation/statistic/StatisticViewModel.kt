package com.jae464.presentation.statistic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.TaskType
import com.jae464.domain.usecase.progresstask.GetFilteredProgressTaskUseCase
import com.jae464.domain.usecase.progresstask.GetProgressTaskByDates
import com.jae464.presentation.statistic.model.TotalProgressTaskUiModel
import com.jae464.presentation.statistic.model.toTotalProgressTaskUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val getProgressTaskByDates: GetProgressTaskByDates,
    private val getFilteredProgressTaskUseCase: GetFilteredProgressTaskUseCase
) : ViewModel() {

    val totalProgressTasksUiState = MutableStateFlow<TotalProgressTasksUiState>(TotalProgressTasksUiState.Empty)

    val filteredProgressTasks = getFilteredProgressTaskUseCase(
        usePeriod = false,
        useFilterCategory = true,
        filterCategoryIds = setOf(1L),
        useFilterTaskType = true,
        filterTaskType = TaskType.Regular,
        useFilterDayOfWeeks = true,
        filterDayOfWeeks = setOf(DayOfWeek.SUNDAY)
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun getProgressTasks(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            totalProgressTasksUiState.value = TotalProgressTasksUiState.Loading
            // TODO 호출할때마다 Collector가 계속해서 증가할 것으로 보임. 수정 필요
            getProgressTaskByDates(startDate, endDate).collectLatest {
                Log.d("StatisticViewModel", "collector ${this.coroutineContext.hashCode()}")
                if (it.isEmpty()) {
                    totalProgressTasksUiState.value = TotalProgressTasksUiState.Empty
                }
                else {
                    totalProgressTasksUiState.value = TotalProgressTasksUiState.Success(it.toTotalProgressTaskUiModels())
                }
            }
        }
    }
}

sealed interface TotalProgressTasksUiState {
    object Empty: TotalProgressTasksUiState
    object Loading: TotalProgressTasksUiState
    data class Success(val totalProgressTasks: List<TotalProgressTaskUiModel>): TotalProgressTasksUiState
}