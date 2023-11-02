package com.jae464.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import com.jae464.domain.model.toDayOfWeek
import com.jae464.domain.repository.TaskRepository
import com.jae464.domain.usecase.GetTasksByDayOfWeekUseCase
import com.jae464.domain.usecase.GetTodayProgressTaskUseCase
import com.jae464.domain.usecase.UpdateTodayProgressTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTasksByDayOfWeekUseCase: GetTasksByDayOfWeekUseCase,
    private val getTodayProgressTaskUseCase: GetTodayProgressTaskUseCase,
    private val updateTodayProgressTasksUseCase: UpdateTodayProgressTasksUseCase
) : ViewModel() {
    val tasks =
        getTasksByDayOfWeekUseCase(LocalDate.now().dayOfWeek.toDayOfWeek())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // TODO progress task 가져오기
    private val progressTasks = getTodayProgressTaskUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // TODO tasks, progressTasks combine
    val progressTaskState: StateFlow<ProgressTaskState> = combine(
        tasks,
        progressTasks
    ) { tasks, progressTasks ->
        Log.d(TAG, "tasks : $tasks progressTasks: $progressTasks")
        val progressTaskIds = progressTasks.map { it.task.id }
        val addProgressTasks = tasks.filter { task -> task.id !in progressTaskIds }
        Log.d(TAG, "Have to Insert Progress Task : $addProgressTasks")

        if (addProgressTasks.isNotEmpty()) {
            updateProgressTask(addProgressTasks)
            ProgressTaskState.Loading
        }
        else {
            ProgressTaskState.Success(progressTasks.map { it.toProgressTaskUiModel() })
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressTaskState.Loading
    )

    private fun updateProgressTask(tasks: List<Task>) {
        viewModelScope.launch {
            Log.d(TAG, "ProgressTask 업데이트 합니다.")
            updateTodayProgressTasksUseCase(tasks)
        }
    }



    companion object {
        const val TAG = "HomeViewModel"
    }
}