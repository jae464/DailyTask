package com.jae464.presentation.statistic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.Category
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.TaskType
import com.jae464.domain.usecase.category.GetAllCategoriesUseCase
import com.jae464.domain.usecase.progresstask.GetFilteredProgressTaskUseCase
import com.jae464.domain.usecase.progresstask.GetProgressTaskByDates
import com.jae464.presentation.statistic.model.TotalProgressTaskUiModel
import com.jae464.presentation.statistic.model.toTotalProgressTaskUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class StatisticUiState(
    val totalProgressTasksUiState: TotalProgressTasksUiState = TotalProgressTasksUiState.Loading,
    val categories: List<Category> = emptyList(),
    val filteredCategories: List<Category> = emptyList(),
    val filteredTaskType: TaskType = TaskType.All,
    val filteredDayOfWeeks: List<DayOfWeek> = emptyList(),
    val filterCardHeight: Int = Int.MAX_VALUE,
    val calendarHeight: Int = Int.MAX_VALUE,
    val loadButtonHeight: Int = Int.MAX_VALUE,
    val showCalendar: Boolean = false,
    val showFilterOption: Boolean = false,
)

sealed interface TotalProgressTasksUiState {
    object Empty : TotalProgressTasksUiState
    object Loading : TotalProgressTasksUiState
    data class Success(val totalProgressTasks: List<TotalProgressTaskUiModel>) :
        TotalProgressTasksUiState
}

sealed interface StatisticUiEffect {
    data class ScrollToFilterCard(val offset: Int) : StatisticUiEffect
    data class ScrollToStatisticList(val offset: Int) : StatisticUiEffect
}

sealed interface StatisticUiEvent {

    data class OnClickLoadButton(
        val startDate: LocalDate,
        val endDate: LocalDate,
        val filteredCategories: List<Category>,
        val filteredTaskType: TaskType,
        val filteredDayOfWeeks: List<DayOfWeek>
    ) : StatisticUiEvent
    data class SetFilteredCategories(val filteredCategories: List<Category>) : StatisticUiEvent
    data class SetFilteredTaskType(val filteredTaskType: TaskType) : StatisticUiEvent
    data class SetFilteredDayOfWeeks(val filteredDayOfWeeks: List<DayOfWeek>) : StatisticUiEvent
    data class SetCalendarHeight(val height: Int) : StatisticUiEvent
    data class SetFilterCardHeight(val height: Int) : StatisticUiEvent
    data class SetLoadButtonHeight(val height: Int) : StatisticUiEvent
    data class ToggleCalendar(val showCalendar: Boolean) : StatisticUiEvent
    data class ToggleFilterOption(val showFilterOption: Boolean) : StatisticUiEvent

}

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val getProgressTaskByDates: GetProgressTaskByDates,
    private val getFilteredProgressTaskUseCase: GetFilteredProgressTaskUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticUiState())
    val uiState: StateFlow<StatisticUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<StatisticUiEffect>()
    val uiEffect: SharedFlow<StatisticUiEffect> = _uiEffect.asSharedFlow()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        getAllCategoriesUseCase()
            .onEach {
                _uiState.update { state -> state.copy(categories = it) }
            }
            .launchIn(viewModelScope)
    }

    fun handleEvent(event: StatisticUiEvent) {
        Log.d("StatisticViewModel", "handleEvent $event")
        when (event) {
            is StatisticUiEvent.OnClickLoadButton -> {
                getProgressTasks(
                    event.startDate,
                    event.endDate,
                    event.filteredCategories,
                    event.filteredTaskType,
                    event.filteredDayOfWeeks
                )
            }
            is StatisticUiEvent.SetFilteredCategories -> setFilteredCategories(event.filteredCategories)
            is StatisticUiEvent.SetFilteredTaskType -> setFilteredTaskType(event.filteredTaskType)
            is StatisticUiEvent.SetFilteredDayOfWeeks -> setFilteredDayOfWeeks(event.filteredDayOfWeeks)
            is StatisticUiEvent.SetCalendarHeight -> setCalendarHeight(event.height)
            is StatisticUiEvent.SetFilterCardHeight -> setFilterCardHeight(event.height)
            is StatisticUiEvent.SetLoadButtonHeight -> setLoadButtonHeight(event.height)
            is StatisticUiEvent.ToggleCalendar -> toggleCalendar(event.showCalendar)
            is StatisticUiEvent.ToggleFilterOption -> toggleFilterOption(event.showFilterOption)
        }
    }

    private fun toggleCalendar(showCalendar: Boolean) {
        _uiState.update { it.copy(showCalendar = showCalendar) }
    }

    private fun toggleFilterOption(showFilterOption: Boolean) {
        _uiState.update { it.copy(showFilterOption = showFilterOption) }
    }

    private fun setFilteredCategories(filteredCategories: List<Category>) {
        _uiState.update { it.copy(filteredCategories = filteredCategories) }
    }

    private fun setFilteredTaskType(filteredTaskType: TaskType) {
        _uiState.update { it.copy(filteredTaskType = filteredTaskType) }
    }

    private fun setFilteredDayOfWeeks(filteredDayOfWeeks: List<DayOfWeek>) {
        _uiState.update { it.copy(filteredDayOfWeeks = filteredDayOfWeeks) }
    }

    private fun setCalendarHeight(height: Int) {
        _uiState.update { it.copy(calendarHeight = height) }
    }

    private fun setFilterCardHeight(height: Int) {
        _uiState.update { it.copy(filterCardHeight = height) }
    }

    private fun setLoadButtonHeight(height: Int) {
        _uiState.update { it.copy(loadButtonHeight = height) }
    }

    private fun getProgressTasks(
        startDate: LocalDate,
        endDate: LocalDate,
        filteredCategories: List<Category>,
        filteredTaskType: TaskType,
        filteredDayOfWeeks: List<DayOfWeek>
    ) {
        getFilteredProgressTaskUseCase(
            usePeriod = true,
            startDate = startDate,
            endDate = endDate,
            useFilterCategory = filteredCategories.isNotEmpty(),
            filterCategoryIds = filteredCategories.map { it.id }.toSet(),
            useFilterTaskType = filteredTaskType != TaskType.All,
            filterTaskType = filteredTaskType,
            useFilterDayOfWeeks = filteredDayOfWeeks.isNotEmpty(),
            filterDayOfWeeks = filteredDayOfWeeks.toSet()
        )
            .onStart {
                _uiState.update { state -> state.copy(totalProgressTasksUiState = TotalProgressTasksUiState.Loading) }
            }
            .onEach {
                Log.d("StatisticViewModel", "getProgressTasks $it")
                if (it.isEmpty()) {
                    _uiState.update { state -> state.copy(totalProgressTasksUiState = TotalProgressTasksUiState.Empty) }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            totalProgressTasksUiState = TotalProgressTasksUiState.Success(
                                it.toTotalProgressTaskUiModels()
                            )
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

}
