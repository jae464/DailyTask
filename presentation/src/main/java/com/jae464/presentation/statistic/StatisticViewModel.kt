package com.jae464.presentation.statistic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.Category
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.TaskType
import com.jae464.domain.usecase.category.GetAllCategoriesUseCase
import com.jae464.domain.usecase.progresstask.GetFilteredProgressTaskUseCase
import com.jae464.domain.usecase.progresstask.GetProgressTaskByDates
import com.jae464.presentation.statistic.model.TotalProgressTaskUiModel
import com.jae464.presentation.statistic.model.toTotalProgressTaskUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val getProgressTaskByDates: GetProgressTaskByDates,
    private val getFilteredProgressTaskUseCase: GetFilteredProgressTaskUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase
) : ViewModel() {

    // UI State
    val totalProgressTasksUiState = MutableStateFlow<TotalProgressTasksUiState>(TotalProgressTasksUiState.Empty)

    val categories = getAllCategoriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _filteredCategories = MutableStateFlow<List<Category>>(emptyList())
    val filteredCategories: StateFlow<List<Category>>
        get() = _filteredCategories

    private val _filteredTaskType = MutableStateFlow(TaskType.All)
    val filteredTaskType: StateFlow<TaskType>
        get() = _filteredTaskType

    private val _filteredDayOfWeeks = MutableStateFlow<List<DayOfWeek>>(emptyList())
    val filteredDayOfWeeks: StateFlow<List<DayOfWeek>>
        get() = _filteredDayOfWeeks

    private var getProgressTaskJob: Job? = null

    private val _filterCardHeight = MutableStateFlow(0)
    val filterCardHeight: StateFlow<Int> get() = _filterCardHeight

    private val _calendarHeight = MutableStateFlow(0)
    val calendarHeight: StateFlow<Int> get() = _calendarHeight

    private val _loadButtonHeight = MutableStateFlow(0)
    val loadButtonHeight: StateFlow<Int> get() = _loadButtonHeight

    // Event
    private val _event = MutableSharedFlow<StatisticEvent>()
    val event: SharedFlow<StatisticEvent> get() = _event

    fun getProgressTasks(startDate: LocalDate, endDate: LocalDate) {
        getProgressTaskJob?.cancel()

        getProgressTaskJob = viewModelScope.launch {
            totalProgressTasksUiState.value = TotalProgressTasksUiState.Loading
            val useFilterCategory = filteredCategories.value.isNotEmpty()
            val useFilterTaskType = filteredTaskType.value != TaskType.All
            val useFilterDayOfWeeks = filteredDayOfWeeks.value.isNotEmpty()
            getFilteredProgressTaskUseCase(
                usePeriod = true,
                startDate = startDate,
                endDate = endDate,
                useFilterCategory = useFilterCategory,
                filterCategoryIds = filteredCategories.value.map { it.id }.toSet(),
                useFilterTaskType = useFilterTaskType,
                filterTaskType = filteredTaskType.value,
                useFilterDayOfWeeks = useFilterDayOfWeeks,
                filterDayOfWeeks = filteredDayOfWeeks.value.toSet()
            ).collectLatest {
                Log.d("StatisticViewModel", "getProgressTasks $it")
                if (it.isEmpty()) {
                    totalProgressTasksUiState.value = TotalProgressTasksUiState.Empty
                }
                else {
                    totalProgressTasksUiState.value = TotalProgressTasksUiState.Success(it.toTotalProgressTaskUiModels())
                }
            }
        }
    }

    fun filterCategories(filteredCategories: List<Category>) {
        _filteredCategories.value = filteredCategories
    }

    fun filterTaskType(taskType: TaskType) {
        _filteredTaskType.value = taskType
    }

    fun filterDayOfWeeks(dayOfWeeks: List<DayOfWeek>) {
        _filteredDayOfWeeks.value = dayOfWeeks
    }

    fun setCalendarHeight(height: Int) {
        _calendarHeight.value = height
    }

    fun setFilterCardHeight(height: Int) {
        _filterCardHeight.value = height
    }

    fun setLoadButtonHeight(height: Int) {
        _loadButtonHeight.value = height
    }

    fun scrollToFilterCard() {
        viewModelScope.launch {
            _event.emit(StatisticEvent.ScrollToFilterCard(calendarHeight.value))
        }
    }

    fun scrollToStatisticList() {
        viewModelScope.launch {
            _event.emit(StatisticEvent.ScrollToStatisticList(calendarHeight.value + filterCardHeight.value + loadButtonHeight.value))
        }
    }
}

sealed interface TotalProgressTasksUiState {
    object Empty: TotalProgressTasksUiState
    object Loading: TotalProgressTasksUiState
    data class Success(val totalProgressTasks: List<TotalProgressTaskUiModel>): TotalProgressTasksUiState
}

sealed interface StatisticEvent {
    data class ScrollToFilterCard(val offset: Int) : StatisticEvent
    data class ScrollToStatisticList(val offset: Int): StatisticEvent
}