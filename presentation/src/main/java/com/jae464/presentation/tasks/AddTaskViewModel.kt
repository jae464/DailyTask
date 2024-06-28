package com.jae464.presentation.tasks

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.Category
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType
import com.jae464.domain.usecase.category.AddCategoryUseCase
import com.jae464.domain.usecase.category.GetAllCategoriesUseCase
import com.jae464.domain.usecase.task.GetTaskUseCase
import com.jae464.domain.usecase.task.SaveTaskUseCase
import com.jae464.domain.usecase.task.UpdateTaskUseCase
import com.jae464.presentation.utils.getHour
import com.jae464.presentation.utils.getMinute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

data class AddTaskUiState (
    val title: String = "",
    val progressTimeHour: Int = 0,
    val progressTimeMinute: Int = 0,
    val selectedTaskType: TaskType = TaskType.Regular,
    val selectedDayOfWeeks: List<DayOfWeek> = emptyList(),
    val useAlarm: Boolean = false,
    val alarmTime: LocalDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)),
    val selectedCategory: Category? = null,
    val memo: String = "",
    val categories: CategoriesState = CategoriesState.Loading
)

sealed interface AddTaskUiEvent {
    data class AddCategory(val categoryName: String) : AddTaskUiEvent
    data class SaveTask(val task: Task) : AddTaskUiEvent
    data class SetSelectedCategory(val category: Category) : AddTaskUiEvent
    data class SetTitle(val title: String) : AddTaskUiEvent
    data class SetProgressTimeHour(val hour: Int) : AddTaskUiEvent
    data class SetProgressTimeMinute(val minute: Int) : AddTaskUiEvent
    data class SetSelectedTaskType(val taskType: TaskType) : AddTaskUiEvent
    data class SetSelectedDayOfWeeks(val dayOfWeeks: List<DayOfWeek>) : AddTaskUiEvent
    data class SetUseAlarm(val useAlarm: Boolean) : AddTaskUiEvent
    data class SetAlarmTime(val alarmTime: LocalDateTime) : AddTaskUiEvent
    data class SetMemo(val memo: String) : AddTaskUiEvent
}

sealed interface AddTaskUiEffect {
    data object EmptyTitle : AddTaskUiEffect
    data object EmptyProgressTime : AddTaskUiEffect
    data object EmptyDayOfWeeks : AddTaskUiEffect
    data object SaveCompleted : AddTaskUiEffect
}

sealed interface CategoriesState {
    data object Loading: CategoriesState
    data class Success(val categories: List<Category>) : CategoriesState
}

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTaskUseCase: GetTaskUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val addCategoryUseCase: AddCategoryUseCase
): ViewModel() {

    private val taskId = savedStateHandle.get<String>("taskId") ?: ""

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> get() = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AddTaskUiEffect>()
    val uiEffect: SharedFlow<AddTaskUiEffect> get() = _uiEffect

    init {
        fetchCategories()
        fetchTask()
    }

    fun handleEvent(event: AddTaskUiEvent) {
        when (event) {
            is AddTaskUiEvent.AddCategory -> addCategory(event.categoryName)
            is AddTaskUiEvent.SaveTask -> saveTask(event.task)
            is AddTaskUiEvent.SetSelectedCategory -> setSelectedCategory(event.category)
            is AddTaskUiEvent.SetTitle -> setTitle(event.title)
            is AddTaskUiEvent.SetProgressTimeHour -> setProgressTimeHour(event.hour)
            is AddTaskUiEvent.SetProgressTimeMinute -> setProgressTimeMinute(event.minute)
            is AddTaskUiEvent.SetSelectedTaskType -> setSelectedTaskType(event.taskType)
            is AddTaskUiEvent.SetSelectedDayOfWeeks -> setSelectedDayOfWeeks(event.dayOfWeeks)
            is AddTaskUiEvent.SetUseAlarm -> setUseAlarm(event.useAlarm)
            is AddTaskUiEvent.SetAlarmTime -> setAlarmTime(event.alarmTime)
            is AddTaskUiEvent.SetMemo -> setMemo(event.memo)
        }
    }

    private fun setAlarmTime(alarmTime: LocalDateTime) {
        _uiState.update { state -> state.copy(alarmTime = alarmTime) }
    }

    private fun setUseAlarm(useAlarm: Boolean) {
        _uiState.update { state -> state.copy(useAlarm = useAlarm) }
    }

    private fun setSelectedDayOfWeeks(dayOfWeeks: List<DayOfWeek>) {
        _uiState.update { state -> state.copy(selectedDayOfWeeks = dayOfWeeks) }
    }

    private fun setSelectedTaskType(taskType: TaskType) {
        _uiState.update { state -> state.copy(selectedTaskType = taskType) }
    }

    private fun setProgressTimeMinute(minute: Int) {
        _uiState.update { state -> state.copy(progressTimeMinute = minute) }
    }

    private fun setProgressTimeHour(hour: Int) {
        _uiState.update { state -> state.copy(progressTimeHour = hour) }
    }

    private fun setMemo(memo: String) {
        _uiState.update { state -> state.copy(memo = memo) }
    }

    private fun setTitle(title: String) {
        _uiState.update { state -> state.copy(title = title) }
    }

    private fun setSelectedCategory(category: Category) {
        _uiState.update { state -> state.copy(selectedCategory = category) }
    }

    private fun fetchCategories() {
        getAllCategoriesUseCase().onEach {
            _uiState.update { state -> state.copy(categories = CategoriesState.Success(it)) }
            if (taskId.isEmpty() && it.isNotEmpty()) {
                _uiState.update { state -> state.copy(selectedCategory = it.first()) }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchTask() {
        if (taskId.isNotBlank()) {
            getTaskUseCase(taskId).onEach {
                _uiState.update { state -> state.copy(
                    title = it.title,
                    progressTimeHour = it.progressTime.getHour(),
                    progressTimeMinute = it.progressTime.getMinute(),
                    selectedTaskType = it.taskType,
                    selectedDayOfWeeks = it.dayOfWeeks,
                    useAlarm = it.useAlarm,
                    alarmTime = it.alarmTime,
                    selectedCategory = it.category,
                    memo = it.memo
                )}
            }.launchIn(viewModelScope)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("AddTaskViewModel", "onCleared()")
    }

    private fun saveTask(task: Task) {
        viewModelScope.launch {
            if (task.title.isEmpty()) {
                _uiEffect.emit(AddTaskUiEffect.EmptyTitle)
                return@launch
            }
            if (task.progressTime == 0) {
                _uiEffect.emit(AddTaskUiEffect.EmptyProgressTime)
                return@launch
            }
            if (task.taskType == TaskType.Regular && task.dayOfWeeks.isEmpty()) {
                _uiEffect.emit(AddTaskUiEffect.EmptyDayOfWeeks)
                return@launch
            }
            if (taskId.isNotEmpty()) {
                updateTaskUseCase(task)
            }
            else {
                saveTaskUseCase(task)
            }
            _uiEffect.emit(AddTaskUiEffect.SaveCompleted)
        }
    }

    private fun addCategory(categoryName: String) {
        viewModelScope.launch {
            addCategoryUseCase(Category(0L, categoryName))
        }
    }


    companion object {
        const val TAG = "AddTaskViewModel"
    }


}
