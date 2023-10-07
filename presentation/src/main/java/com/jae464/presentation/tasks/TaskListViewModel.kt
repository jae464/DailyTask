package com.jae464.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
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
        TaskState.Success(tasks.map { task ->
            task.toTaskUIModel(categories.first { it.id == task.categoryId }.name)
        })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TaskState.Loading
    )
}

sealed interface TaskState {
    object Loading : TaskState
    data class Success(val taskUIModels: List<TaskUIModel>) : TaskState
}