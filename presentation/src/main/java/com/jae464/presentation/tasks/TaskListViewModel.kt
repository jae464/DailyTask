package com.jae464.presentation.tasks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.usecase.DeleteTaskUseCase
import com.jae464.domain.usecase.GetAllCategoriesUseCase
import com.jae464.domain.usecase.GetAllTasksUseCase
import com.jae464.domain.usecase.GetCategoryUseCase
import com.jae464.presentation.model.TaskUIModel
import com.jae464.presentation.model.toTaskUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val categories = getAllCategoriesUseCase()
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

    val taskState: StateFlow<TaskState> =
        combine(categories, tasks) { categories, tasks ->
            // check categories and tasks are not empty
            if (categories.isNotEmpty()) {
                TaskState.Success(tasks.map { task ->
                    task.toTaskUIModel(categories.first { it.id == task.categoryId }.name)
                })
            } else {
                TaskState.Loading
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TaskState.Loading
    )

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            deleteTaskUseCase(taskId)
        }
    }

}

sealed interface TaskState {
    object Loading : TaskState
    data class Success(val taskUIModels: List<TaskUIModel>) : TaskState
}