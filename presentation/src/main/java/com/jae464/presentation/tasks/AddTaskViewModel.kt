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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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

    private val _addTaskUiState: MutableStateFlow<AddTaskUiState> = MutableStateFlow(AddTaskUiState.Loading)
    val addTaskUiState: StateFlow<AddTaskUiState> get() = _addTaskUiState

    private val savedTaskId: String? = savedStateHandle.get<String>("taskId")

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    private val _progressTimeHour = MutableStateFlow(0)
    val progressTimeHour: StateFlow<Int> get() = _progressTimeHour

    private val _progressTimeMinute = MutableStateFlow(0)
    val progressTimeMinute: StateFlow<Int> get() = _progressTimeMinute

    private val _selectedTaskType = MutableStateFlow(TaskType.Regular)
    val selectedTaskType: StateFlow<TaskType> get() = _selectedTaskType

    private val _selectedDayOfWeeks = MutableStateFlow<List<DayOfWeek>>(emptyList())
    val selectedDayOfWeeks: StateFlow<List<DayOfWeek>> get() = _selectedDayOfWeeks

    private val _useAlarm = MutableStateFlow(false)
    val useAlarm: StateFlow<Boolean> get() = _useAlarm

    private val _alarmTime = MutableStateFlow(LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)))
    val alarmTime: StateFlow<LocalDateTime> get() = _alarmTime

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> get() = _selectedCategory

    private val _memo = MutableStateFlow("")
    val memo: StateFlow<String> get() = _memo

    // event
    private val _event = MutableSharedFlow<AddTaskEvent>()
    val event: SharedFlow<AddTaskEvent> get() = _event

    init {
        Log.d("AddTaskViewModel", savedTaskId.toString())
        if (!savedTaskId.isNullOrEmpty()) {
            viewModelScope.launch {
                getTaskUseCase(savedTaskId).collectLatest { task ->
                    _addTaskUiState.value = AddTaskUiState.Success(task)
                    _title.value = task.title
                    _progressTimeHour.value = task.progressTime.getHour()
                    _progressTimeMinute.value = task.progressTime.getMinute()
                    _selectedTaskType.value = task.taskType
                    _selectedDayOfWeeks.value = task.dayOfWeeks
                    _useAlarm.value = task.useAlarm
                    _alarmTime.value = task.alarmTime
                    _selectedCategory.value = task.category
                    _memo.value = task.memo
                }
            }
        }
        else {
            _addTaskUiState.value = AddTaskUiState.Empty
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("AddTaskViewModel", "onCleared()")
    }

    // 모든 카테고리 가져오기
    val categories = getAllCategoriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun saveTask() {
        viewModelScope.launch {
            if (selectedCategory.value == null) return@launch
            if (title.value.isEmpty()) {
                _event.emit(AddTaskEvent.ShowToastMessage("제목을 입력해주세요."))
                return@launch
            }
            if (progressTimeHour.value == 0 && progressTimeMinute.value == 0) {
                _event.emit(AddTaskEvent.ShowToastMessage("진행시간을 설정해주세요."))
                return@launch
            }
            if (selectedTaskType.value == TaskType.Regular && selectedDayOfWeeks.value.isEmpty()) {
                _event.emit(AddTaskEvent.ShowToastMessage("하나 이상의 요일을 설정해주세요."))
                return@launch
            }

            if (!savedTaskId.isNullOrEmpty()) {
                updateTaskUseCase(
                    Task(
                        id = savedTaskId,
                        title = title.value,
                        progressTime = progressTimeHour.value * 3600 + progressTimeMinute.value * 60,
                        taskType = selectedTaskType.value,
                        dayOfWeeks = selectedDayOfWeeks.value,
                        useAlarm = useAlarm.value,
                        alarmTime = alarmTime.value,
                        memo = memo.value,
                        category = selectedCategory.value!!
                    )
                )
            }
            else {
                saveTaskUseCase(
                    Task(
                        id = "",
                        title = title.value,
                        progressTime = progressTimeHour.value * 3600 + progressTimeMinute.value * 60,
                        taskType = selectedTaskType.value,
                        dayOfWeeks = selectedDayOfWeeks.value,
                        useAlarm = useAlarm.value,
                        alarmTime = alarmTime.value,
                        memo = memo.value,
                        category = selectedCategory.value!!
                    )
                )
            }
            _event.emit(AddTaskEvent.SaveCompleted)
        }
    }

    fun addCategory(categoryName: String) {
        viewModelScope.launch {
            addCategoryUseCase(Category(0L, categoryName))
        }
    }

    fun onChangeTitle(title: String) {
        _title.value = title
    }

    fun onChangeProgressTimeHour(hour: Int) {
        _progressTimeHour.value = hour
    }

    fun onChangeProgressTimeMinute(minute: Int) {
        _progressTimeMinute.value = minute
    }

    fun onChangeSelectedTaskType(taskType: TaskType) {
        _selectedTaskType.value = taskType
    }

    fun onChangeSelectedDayOfWeeks(dayOfWeeks: List<DayOfWeek>) {
        _selectedDayOfWeeks.value = dayOfWeeks
    }

    fun onChangeUseAlarm(useAlarm: Boolean) {
        _useAlarm.value = useAlarm
    }

    fun onChangeAlarmTime(alarmTime: LocalDateTime) {
        _alarmTime.value = alarmTime
    }

    fun onChangeSelectedCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun onChangeMemo(memo: String) {
        _memo.value = memo
    }
}

sealed interface AddTaskUiState {
    object Loading: AddTaskUiState
    data class Success(val task: Task): AddTaskUiState
    object Empty: AddTaskUiState
}

sealed interface AddTaskEvent {
    object SaveCompleted : AddTaskEvent
    data class ShowToastMessage(val message: String) : AddTaskEvent
}