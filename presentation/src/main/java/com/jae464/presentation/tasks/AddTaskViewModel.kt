package com.jae464.presentation.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.usecase.GetAllCategoriesUseCase
import com.jae464.domain.usecase.GetCategoryUseCase
import com.jae464.domain.usecase.GetTaskUseCase
import com.jae464.domain.usecase.SaveTaskUseCase
import com.jae464.presentation.model.AddTaskUIModel
import com.jae464.presentation.model.toAddTaskUiModel
import com.jae464.presentation.model.toTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase
): ViewModel() {

    // 기존 Task를 편집하는 경우 기존의 데이터를 가져온다.
    @OptIn(ExperimentalCoroutinesApi::class)
    val task = savedStateHandle.getStateFlow<String?>(
        key = "taskId",
        initialValue = null
    )
        .flatMapLatest { taskId ->
            if (taskId == null) {
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
                AddTaskState.Success(task.toAddTaskUiModel())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
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
            saveTaskUseCase(addTaskUiModel.toTask())
        }
    }
}

sealed interface AddTaskState {
    data class Success(val addTaskUiModel: AddTaskUIModel): AddTaskState
    object Empty: AddTaskState
}