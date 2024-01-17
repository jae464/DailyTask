package com.jae464.presentation.tasks

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
import com.jae464.domain.usecase.task.GetAllTasksUseCase
import com.jae464.domain.usecase.task.GetFilteredTasksUseCase
import com.jae464.presentation.home.ProgressingTaskManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val isExistProgressTaskUseCase: IsExistProgressTaskUseCase,
    private val insertProgressTaskUseCase: InsertProgressTaskUseCase,
    private val getFilteredTasksUseCase: GetFilteredTasksUseCase
) : ViewModel() {

    private val progressingTaskManager = ProgressingTaskManager.getInstance()

    val categories = getAllCategoriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val tasks = getAllTasksUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _filteredCategories = MutableStateFlow<List<Category>>(emptyList())
    val filteredCategories: StateFlow<List<Category>>
        get() = _filteredCategories

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> get() = _searchText

    private val filteredSearchText = MutableStateFlow("")

    private val _sortBy = MutableStateFlow(SortBy.ASC)
    val sortBy: StateFlow<SortBy> get() = _sortBy

    private val _filteredTaskType = MutableStateFlow(TaskType.All)
    val filteredTaskType: StateFlow<TaskType> get() = _filteredTaskType

    private val _filteredDayOfWeeks = MutableStateFlow<List<DayOfWeek>>(emptyList())
    val filteredDayOfWeeks: StateFlow<List<DayOfWeek>> get() = _filteredDayOfWeeks


    // UIState
    val taskListUiState2 = MutableStateFlow<TaskListUiState>(TaskListUiState.Loading)

    private var getTasksJob: Job? = null

    // Event
    private val _event = MutableSharedFlow<TaskListEvent>()
    val event: SharedFlow<TaskListEvent>
        get() = _event

    init {
        viewModelScope.launch {
            searchText.debounce(500).collect {
                filteredSearchText.value = it
            }
        }
        getTasksJob?.cancel()
        getTasksJob = viewModelScope.launch {
            getAllTasksUseCase().collectLatest { tasks ->
                if (tasks.isEmpty()) {
                    taskListUiState2.value = TaskListUiState.Loading
                }
                else {
                    taskListUiState2.value = TaskListUiState.Success(tasks)
                }
            }
        }
    }

    fun getFilteredTasks() {
        getTasksJob?.cancel()
        getTasksJob = viewModelScope.launch {
            val useFilterTaskType = filteredTaskType.value != TaskType.All
            val useFilterCategory = filteredCategories.value.isNotEmpty()
            val useFilterDayOfWeeks = filteredDayOfWeeks.value.isNotEmpty()
            getFilteredTasksUseCase(
                useFilterTaskType = useFilterTaskType,
                filterTaskType = filteredTaskType.value,
                useFilterCategory = useFilterCategory,
                filterCategoryIds = filteredCategories.value.map { it.id }.toSet(),
                useFilterDayOfWeeks = useFilterDayOfWeeks,
                filterDayOfWeeks = filteredDayOfWeeks.value.toSet(),
                sortBy = sortBy.value
            )
                .collectLatest {tasks ->
                    if (tasks.isEmpty()) {
                        taskListUiState2.value = TaskListUiState.Empty
                    }
                    else {
                        taskListUiState2.value = TaskListUiState.Success(
                            tasks
                        )
                    }
                    _event.emit(TaskListEvent.HideBottomSheetDialog)

                }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            // 삭제하려는 일정이 현재 진행중인 일정이면 진행을 멈춘다.
            if ((progressingTaskManager.getCurrentProgressTask()?.task?.id ?: "") == task.id) {
                progressingTaskManager.stopProgressTask()
            }
            deleteTaskUseCase(task.id)
        }
    }

    fun filterCategories(filteredCategories: List<Category>) {
        _filteredCategories.value = filteredCategories
        getFilteredTasks()
    }

    fun setSearchText(text: String) {
        _searchText.value = text
    }

    fun insertProgressTaskToday(task: Task) {
        viewModelScope.launch {
            val isExist = checkIsExistProgressToday(task.id)
            if (!isExist) {
                insertProgressTaskUseCase(task.toProgressTask())
                _event.emit(TaskListEvent.SendToastMessage("오늘 할일에 추가되었습니다."))
            }
        }
    }

    private suspend fun checkIsExistProgressToday(taskId: String): Boolean {
        return isExistProgressTaskUseCase(taskId, LocalDate.now())
    }

    fun setSortBy(sortBy: SortBy) {
        _sortBy.value = sortBy
    }

    fun setTaskType(taskType: TaskType) {
        _filteredTaskType.value = taskType
    }

    fun setFilteredDayOfWeeks(dayOfWeeks: List<DayOfWeek>) {
        _filteredDayOfWeeks.value = dayOfWeeks
    }
}

sealed interface TaskListUiState {
    object Loading : TaskListUiState
    data class Success(val tasks: List<Task>) : TaskListUiState
    object Empty : TaskListUiState
}

sealed interface TaskListEvent {
    data class SendToastMessage(val message: String) : TaskListEvent
    object HideBottomSheetDialog : TaskListEvent
}