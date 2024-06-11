package com.jae464.presentation.setting

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import com.jae464.domain.model.toDayOfWeek
import com.jae464.domain.model.toProgressTask
import com.jae464.domain.usecase.progresstask.DeleteAllProgressTaskByTaskIdUseCase
import com.jae464.domain.usecase.progresstask.GetProgressTaskByTaskIdUseCase
import com.jae464.domain.usecase.progresstask.InsertProgressTaskUseCase
import com.jae464.domain.usecase.task.GetAllTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.random.Random

sealed interface DialogUiState {
    data class ShowDialog(val task: Task): DialogUiState
    object HideDialog : DialogUiState
}

sealed interface TestEvent {
    data class ShowToastMessage(val message: String): TestEvent
}

@HiltViewModel
class ProgressTaskTestViewModel @Inject constructor(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getProgressTaskByTaskIdUseCase: GetProgressTaskByTaskIdUseCase,
    private val insertProgressTaskUseCase: InsertProgressTaskUseCase,
    private val deleteAllProgressTaskByTaskIdUseCase: DeleteAllProgressTaskByTaskIdUseCase
): ViewModel() {

    val tasks = getAllTasksUseCase.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _dialogUiState : MutableStateFlow<DialogUiState> = MutableStateFlow(DialogUiState.HideDialog)
    val dialogUiState: StateFlow<DialogUiState> get() = _dialogUiState

    private val _minTime = MutableStateFlow("")
    val minTime: StateFlow<String> get() = _minTime

    private val _event = MutableSharedFlow<TestEvent>()
    val event: SharedFlow<TestEvent> get() = _event

    @RequiresApi(34)
    fun insertProgressTask(task: Task, startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            if (minTime.value.isEmpty() || !minTime.value.isDigitsOnly()) {
                _event.emit(TestEvent.ShowToastMessage("숫자만 입력하세요."))
                return@launch
            }
            if (minTime.value.toInt() < 0 || minTime.value.toInt() > task.progressTime) {
                _event.emit(TestEvent.ShowToastMessage("올바른 범위를 입력하세요."))
                return@launch
            }

            val progressTasks = getProgressTasksByTask(task)
            val existDates = progressTasks.map { it.createdAt }
            val totalDates = mutableListOf<LocalDate>()
            val betweenDays = ChronoUnit.DAYS.between(startDate, endDate);
            for (i in 1..betweenDays) {
                val date = startDate.plusDays(i)
                if (task.dayOfWeeks.contains(date.dayOfWeek.toDayOfWeek())) {
                    totalDates.add(date)
                }
            }
            val needToAddProgressTasks = totalDates.subtract(existDates.toSet()).toList()

            needToAddProgressTasks.forEach {
                val randomTime = Random.nextInt(minTime.value.toInt(), task.progressTime)
                insertProgressTaskUseCase(task.toProgressTask(progressedTime = randomTime, createdAt = it))
            }

        }
    }

    fun showTaskDialog(task: Task) {
        _dialogUiState.value = DialogUiState.ShowDialog(task)
    }

    fun hideDialog() {
        _dialogUiState.value = DialogUiState.HideDialog
    }

    fun setMinTime(time: String) {
        _minTime.value = time
    }

    fun deleteAllProgressTaskByTask(task: Task) {
        viewModelScope.launch {
            deleteAllProgressTaskByTaskIdUseCase(task.id)
        }
    }



    suspend fun getProgressTasksByTask(task: Task): List<ProgressTask> {
        val progressTasks = getProgressTaskByTaskIdUseCase(taskId = task.id).first()
        Log.d("ProgressTaskTestViewModel", progressTasks.toString())
        return progressTasks
    }

}