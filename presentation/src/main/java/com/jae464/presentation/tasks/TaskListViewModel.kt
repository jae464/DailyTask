package com.jae464.presentation.tasks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.usecase.DeleteTaskUseCase
import com.jae464.domain.usecase.GetAllCategoriesUseCase
import com.jae464.domain.usecase.GetAllTasksUseCase
import com.jae464.domain.usecase.GetCategoryUseCase
import com.jae464.presentation.home.ProgressingTaskManager
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

    private val progressingTaskManager = ProgressingTaskManager.getInstance()

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
            if (categories.isNotEmpty()) {
                if (tasks.isEmpty()) {
                    TaskState.Empty
                }
                else {
                    TaskState.Success(tasks.map { task ->
                        task.toTaskUIModel(categories.first { it.id == task.categoryId }.name)
                    })
                }
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
            // 삭제하려는 일정이 현재 진행중인 일정이면 진행을 멈춘다.
            if ((progressingTaskManager.getCurrentProgressTask()?.task?.id ?: "") == taskId) {
                progressingTaskManager.stopProgressTask()
            }
            deleteTaskUseCase(taskId)
        }
    }

}

sealed interface TaskState {
    object Loading : TaskState
    data class Success(val taskUIModels: List<TaskUIModel>) : TaskState
    object Empty : TaskState
}