package com.jae464.presentation.tasks

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.Category
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.SortBy
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType
import com.jae464.domain.model.toProgressTask
import com.jae464.domain.usecase.category.GetAllCategoriesUseCase
import com.jae464.domain.usecase.progresstask.InsertProgressTaskUseCase
import com.jae464.domain.usecase.progresstask.IsExistProgressTaskUseCase
import com.jae464.domain.usecase.task.DeleteTaskUseCase
import com.jae464.domain.usecase.task.GetFilteredTasksUseCase
import com.jae464.domain.usecase.task.GetTasksByTitleUseCase
import com.jae464.presentation.ProgressTaskService
import com.jae464.presentation.ProgressingTaskManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TaskListUiState(
    val categories: List<Category> = emptyList(),
    val tasks: TasksState = TasksState.Loading,
    val selectedCategories: List<Category> = emptyList(),
    val searchText: String = "",
    val sortBy: SortBy = SortBy.DESC,
    val selectedTaskType: TaskType = TaskType.All,
    val selectedDayOfWeeks: List<DayOfWeek> = emptyList(),
    val showBottomSheetDialog: Boolean = false
)

data class TaskFilterOption(
    val selectedCategories: List<Category> = emptyList(),
    val searchText: String = "",
    val sortBy: SortBy = SortBy.DESC,
    val selectedTaskType: TaskType = TaskType.All,
    val selectedDayOfWeeks: List<DayOfWeek> = emptyList()
)

sealed interface TaskListUiEvent {
    data class UpdateSearchText(val text: String): TaskListUiEvent
    data class UpdateSelectedCategories(val categories: List<Category>): TaskListUiEvent
    data class InsertProgressTask(val task: Task): TaskListUiEvent
    data class DeleteTask(val task: Task): TaskListUiEvent
    data class UpdateFilterOptions(val sortBy: SortBy, val taskType: TaskType, val dayOfWeeks: List<DayOfWeek>): TaskListUiEvent
    data class ToggleBottomSheetDialog(val isShow: Boolean): TaskListUiEvent
}

sealed interface TaskListUiEffect {
    object InsertProgressTaskCompleted : TaskListUiEffect
    object DeleteTaskCompleted : TaskListUiEffect
}

sealed interface TasksState {
    object Loading: TasksState
    data class Success(val tasks: List<Task>): TasksState
    object Empty: TasksState
}

@OptIn(FlowPreview::class)
@HiltViewModel
class TaskListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val progressingTaskManager: ProgressingTaskManager,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val isExistProgressTaskUseCase: IsExistProgressTaskUseCase,
    private val insertProgressTaskUseCase: InsertProgressTaskUseCase,
    private val getFilteredTasksUseCase: GetFilteredTasksUseCase,
    private val getTasksByTitleUseCase: GetTasksByTitleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState : StateFlow<TaskListUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<TaskListUiEffect>()
    val uiEffect : SharedFlow<TaskListUiEffect> = _uiEffect.asSharedFlow()

    private val _filterOption = MutableStateFlow(TaskFilterOption())
    val filterOption: StateFlow<TaskFilterOption> = _filterOption.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    init {
        getCategories()
        getTasks()
    }

    fun handleEvent(event: TaskListUiEvent) {
        when(event) {
            is TaskListUiEvent.DeleteTask -> {
                deleteTask(event.task)
            }
            is TaskListUiEvent.InsertProgressTask -> {
                insertProgressTaskToday(event.task)
            }
            is TaskListUiEvent.UpdateSearchText -> {
                updateSearchText(event.text)
            }
            is TaskListUiEvent.UpdateFilterOptions -> {
                updateFilterOptions(event.sortBy, event.taskType, event.dayOfWeeks)
            }
            is TaskListUiEvent.UpdateSelectedCategories -> {
                updateSelectedCategories(event.categories)
            }
            is TaskListUiEvent.ToggleBottomSheetDialog -> {
                _uiState.update { state -> state.copy(showBottomSheetDialog = event.isShow) }
            }
        }
    }

    private fun getCategories() {
        getAllCategoriesUseCase().onEach {
            _uiState.update { state -> state.copy(categories = it) }
        }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getTasks() {
        filterOption.flatMapLatest {
            if (it.searchText.isNotBlank()) {
                getTasksByTitleUseCase(it.searchText)
            }
            else {
                getFilteredTasksUseCase(
                    sortBy = it.sortBy,
                    useFilterTaskType = it.selectedTaskType != TaskType.All,
                    filterTaskType = it.selectedTaskType,
                    useFilterCategory = it.selectedCategories.isNotEmpty(),
                    filterCategoryIds = it.selectedCategories.map { category -> category.id }.toSet(),
                    useFilterDayOfWeeks = it.selectedDayOfWeeks.isNotEmpty(),
                    filterDayOfWeeks = it.selectedDayOfWeeks.toSet()
                )
            }
        }.onEach {
            Log.d(TAG, it.toString())
            _uiState.update { state -> state.copy(tasks = TasksState.Success(it)) }
        }.launchIn(viewModelScope)

        searchText.debounce(500).flatMapLatest {
            getTasksByTitleUseCase(it)
        }.onEach {
            _uiState.update { state -> state.copy(tasks = TasksState.Success(it)) }
        }.launchIn(viewModelScope)
    }

    private fun updateSearchText(text: String) {
        _searchText.value = text
    }

    private fun updateSelectedCategories(categories: List<Category>) {
        _filterOption.update { state ->
            state.copy(selectedCategories = categories)
        }
    }

    private fun updateFilterOptions(sortBy: SortBy, taskType: TaskType, dayOfWeeks: List<DayOfWeek>) {
        _filterOption.update { state ->
            state.copy(sortBy = sortBy, selectedTaskType = taskType, selectedDayOfWeeks = dayOfWeeks)
        }
        _uiState.update { state -> state.copy(showBottomSheetDialog = false) }
    }

    private fun deleteTask(task: Task) {
        viewModelScope.launch {
            if ((progressingTaskManager.getCurrentProgressTask()?.task?.id ?: "") == task.id) {
                val service = Intent(context, ProgressTaskService::class.java)
                context.stopService(service)
                progressingTaskManager.stopProgressTask()
            }
            deleteTaskUseCase(task.id)
            _uiEffect.emit(TaskListUiEffect.DeleteTaskCompleted)
        }
    }

    private fun insertProgressTaskToday(task: Task) {
        viewModelScope.launch {
            val isExist = checkIsExistProgressToday(task.id)
            if (!isExist) {
                insertProgressTaskUseCase(task.toProgressTask())
                _uiEffect.emit(TaskListUiEffect.InsertProgressTaskCompleted)
            }
        }
    }

    private suspend fun checkIsExistProgressToday(taskId: String): Boolean {
        return isExistProgressTaskUseCase(taskId, LocalDate.now())
    }

    companion object {
        const val TAG = "TaskListViewModel"
    }
}
