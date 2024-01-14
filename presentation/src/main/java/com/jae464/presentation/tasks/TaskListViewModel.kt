package com.jae464.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.Category
import com.jae464.domain.model.SortBy
import com.jae464.domain.model.TaskType
import com.jae464.domain.model.toProgressTask
import com.jae464.domain.usecase.task.DeleteTaskUseCase
import com.jae464.domain.usecase.category.GetAllCategoriesUseCase
import com.jae464.domain.usecase.progresstask.InsertProgressTaskUseCase
import com.jae464.domain.usecase.progresstask.IsExistProgressTaskUseCase
import com.jae464.domain.usecase.task.GetAllTasksUseCase
import com.jae464.domain.usecase.task.GetFilteredTasksUseCase
import com.jae464.presentation.home.ProgressingTaskManager
import com.jae464.presentation.model.TaskUiModel
import com.jae464.presentation.model.toTaskUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    // UIState
    val taskListUiState: StateFlow<TaskListUiState> =
        combine(
            categories,
            tasks,
            filteredCategories,
            filteredSearchText
        ) { categories, tasks, filteredCategories, filteredSearchText ->
            if (categories.isNotEmpty()) {
                if (tasks.isEmpty()) {
                    TaskListUiState.Empty
                } else {
                    TaskListUiState.Success(tasks
                        .filter {
                            filteredCategories.isEmpty() || filteredCategories.map { fc -> fc.id }
                                .contains(it.category.id)
                        }
                        .filter {
                            filteredSearchText.isEmpty() || it.title.contains(filteredSearchText)
                        }
                        .map { task ->
                            task.toTaskUiModel(categories.first { it.id == task.category.id }.name)
                        })
                }
            } else {
                TaskListUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskListUiState.Loading
        )

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
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            // 삭제하려는 일정이 현재 진행중인 일정이면 진행을 멈춘다.
            if ((progressingTaskManager.getCurrentProgressTask()?.task?.id ?: "") == taskId) {
                progressingTaskManager.stopProgressTask()
            }
            deleteTaskUseCase(taskId)
        }
    }

    fun filterCategories(filteredCategories: List<Category>) {
        _filteredCategories.value = filteredCategories
    }

    fun setSearchText(text: String) {
        _searchText.value = text
    }

    fun insertProgressTaskToday(taskId: String) {
        viewModelScope.launch {
            val isExist = checkIsExistProgressToday(taskId)
            if (!isExist) {
                val task = tasks.value.first { it.id == taskId }
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
}

sealed interface TaskListUiState {
    object Loading : TaskListUiState
    data class Success(val taskUiModels: List<TaskUiModel>) : TaskListUiState
    object Empty : TaskListUiState
}

sealed interface TaskListEvent {
    data class SendToastMessage(val message: String) : TaskListEvent
}