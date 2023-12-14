package com.jae464.presentation.tasks

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.Category
import com.jae464.domain.usecase.category.AddCategoryUseCase
import com.jae464.domain.usecase.category.GetAllCategoriesUseCase
import com.jae464.domain.usecase.task.GetTaskUseCase
import com.jae464.domain.usecase.task.SaveTaskUseCase
import com.jae464.domain.usecase.task.UpdateTaskUseCase
import com.jae464.presentation.model.AddTaskUIModel
import com.jae464.presentation.model.toAddTaskUiModel
import com.jae464.presentation.model.toTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTaskUseCase: GetTaskUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val addCategoryUseCase: AddCategoryUseCase
): ViewModel() {

    val saveCompleted = MutableStateFlow(false)

    // 기존 Task를 편집하는 경우 기존의 데이터를 가져온다.
    @OptIn(ExperimentalCoroutinesApi::class)
    val task = savedStateHandle.getStateFlow(
        key = "taskId",
        initialValue = ""
    )
        .flatMapLatest { taskId ->
            if (taskId.isEmpty()) {
                flowOf(null)
            }
            else {
                getTaskUseCase(taskId)
            }
        }
        .map { task ->
            if (task == null) {
                AddTaskState.Empty
            }
            else {
                AddTaskState.LoadSavedTask(task.toAddTaskUiModel())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AddTaskState.Loading
        )

    // 모든 카테고리 가져오기
    val categories = getAllCategoriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun saveTask(addTaskUiModel: AddTaskUIModel) {
        viewModelScope.launch {
            when (task.value) {
                is AddTaskState.Empty -> {
                    saveTaskUseCase(addTaskUiModel.toTask())
                }
                is AddTaskState.LoadSavedTask -> {
                    val taskId = savedStateHandle.get<String>("taskId")
                    Log.d("AddTaskViewModel", "taskId : $taskId")
                    if (taskId == null) return@launch
                    updateTaskUseCase(addTaskUiModel.toTask().copy(id = taskId))
                }
                is AddTaskState.Loading -> return@launch
            }
            saveCompleted.value = true
        }
    }

    fun addCategory(categoryName: String) {
        viewModelScope.launch {
            addCategoryUseCase(Category(0L, categoryName))
        }
    }
}

sealed interface AddTaskState {
    object Loading: AddTaskState
    data class LoadSavedTask(val addTaskUiModel: AddTaskUIModel): AddTaskState
    object Empty: AddTaskState
}