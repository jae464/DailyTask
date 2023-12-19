package com.jae464.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.Category
import com.jae464.domain.usecase.task.DeleteTaskUseCase
import com.jae464.domain.usecase.category.GetAllCategoriesUseCase
import com.jae464.domain.usecase.task.GetAllTasksUseCase
import com.jae464.presentation.home.ProgressingTaskManager
import com.jae464.presentation.model.TaskUiModel
import com.jae464.presentation.model.toTaskUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
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
    val searchText: StateFlow<String>
        get() = _searchText

    private val filteredSearchText = MutableStateFlow("")

    init {
        viewModelScope.launch {
            searchText.debounce(500).collect {
                filteredSearchText.value = it
            }
        }
    }

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
                                .contains(it.categoryId)
                        }
                        .filter {
                            filteredSearchText.isEmpty() || it.title.contains(filteredSearchText)
                        }
                        .map { task ->
                            task.toTaskUiModel(categories.first { it.id == task.categoryId }.name)
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

}

sealed interface TaskListUiState {
    object Loading : TaskListUiState
    data class Success(val taskUiModels: List<TaskUiModel>) : TaskListUiState
    object Empty : TaskListUiState
}