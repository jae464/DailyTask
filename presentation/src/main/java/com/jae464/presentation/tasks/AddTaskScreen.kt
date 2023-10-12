package com.jae464.presentation.tasks

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jae464.presentation.extension.addFocusCleaner
import com.jae464.domain.model.Category
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.HourMinute
import com.jae464.domain.model.TaskType
import com.jae464.presentation.model.AddTaskUIModel
import java.time.LocalDateTime
import java.time.LocalTime

const val addTaskScreenRoute = "add_task"
private const val TAG = "AddTaskScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: AddTaskViewModel = hiltViewModel()
) {

    val addTaskState by viewModel.task.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val isCompleted by viewModel.saveCompleted.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var progressTime by remember { mutableStateOf(HourMinute(1, 0)) }
    val (selectedTaskType, onSelectedTaskType) = remember { mutableStateOf(TaskType.Regular) }
    var selectedDayOfWeeks by remember { mutableStateOf(emptyList<DayOfWeek>()) }
    var alarmTime by remember { mutableStateOf(LocalDateTime.now()) }
    var memo by remember { mutableStateOf("") }
    var selectedCategory: Category? by remember { mutableStateOf(null) }

    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            Log.d(TAG, "saved completed! go to back screen")
            onBackClick()
        }
    }

    LaunchedEffect(addTaskState) {
        when (addTaskState) {
            is AddTaskState.LoadSavedTask -> {
                val savedTaskModel = (addTaskState as AddTaskState.LoadSavedTask).addTaskUiModel
                title = savedTaskModel.title
                progressTime = savedTaskModel.progressTime
                onSelectedTaskType(savedTaskModel.taskType)
                selectedDayOfWeeks = savedTaskModel.dayOfWeeks
                alarmTime = savedTaskModel.alarmTime
                memo = savedTaskModel.memo
                selectedCategory = categories.firstOrNull { it.id == savedTaskModel.categoryId }
            }
            else -> {}
        }
    }

    if (categories.isNotEmpty()) {
        selectedCategory = categories[0]
    }

    Log.d(TAG, "AddTaskScreen Rendered()")
    Scaffold(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            ),
        topBar = {
            AddTaskTopAppBar(
                onBackClick = onBackClick,
                onSaveClick = {
                    if (selectedCategory == null) return@AddTaskTopAppBar
                    viewModel.saveTask(
                        AddTaskUIModel(
                            title = title,
                            progressTime = progressTime,
                            taskType = selectedTaskType,
                            dayOfWeeks = selectedDayOfWeeks,
                            alarmTime = alarmTime,
                            memo = memo,
                            categoryId = selectedCategory!!.id
                        )
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            AddTaskBody(
                modifier = modifier,
                title = title,
                progressTime = progressTime,
                selectedTaskType = selectedTaskType,
                selectedDayOfWeeks = selectedDayOfWeeks,
                alarmTime = alarmTime,
                memo = memo,
                categories = categories,
                selectedCategory = selectedCategory,
                onTitleChanged = { newTitle -> title = newTitle },
                onProgressTimeChanged = { newProgressTime -> progressTime = newProgressTime },
                onSelectedTaskType = onSelectedTaskType,
                onDayOfWeeksChanged = { dayOfWeeks -> selectedDayOfWeeks = dayOfWeeks },
                onAlarmTimeChanged = { newAlarmTime -> alarmTime = newAlarmTime },
                onMemoChanged = { newMemo -> memo = newMemo },
                onCategoryChanged = { category -> selectedCategory = category }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskTopAppBar(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = {
            Text(text = "일정 추가")
        },
        navigationIcon = {
            IconButton(onClick = {
                onBackClick()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back",
                )
            }
        },
        actions = {
            IconButton(onClick = {
                Toast.makeText(context, "저장 버튼 클릭", Toast.LENGTH_SHORT).show()
                onSaveClick()

            }) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "save_task"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
        ),
    )
}

@Composable
fun AddTaskBody(
    modifier: Modifier = Modifier,
    title: String,
    progressTime: HourMinute,
    selectedTaskType: TaskType,
    selectedDayOfWeeks: List<DayOfWeek>,
    alarmTime: LocalDateTime,
    memo: String,
    categories: List<Category>,
    selectedCategory: Category?,
    onTitleChanged: (String) -> Unit,
    onProgressTimeChanged: (HourMinute) -> Unit,
    onSelectedTaskType: (TaskType) -> Unit,
    onDayOfWeeksChanged: (List<DayOfWeek>) -> Unit,
    onAlarmTimeChanged: (LocalDateTime) -> Unit,
    onMemoChanged: (String) -> Unit,
    onCategoryChanged: (Category) -> Unit
) {
    // variables
    val taskOptions = listOf(TaskType.Regular, TaskType.Irregular)
    val dayOfWeeks = DayOfWeek.values()
    val focusManager = LocalFocusManager.current

    // states
    val scrollState = rememberScrollState()

    Log.d(TAG, "AddTaskBody Rendered()")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .addFocusCleaner(focusManager)
            .verticalScroll(scrollState)
    ) {
        TitleTextField(
            title = title,
            onTitleChanged = onTitleChanged,
            focusManager = focusManager
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "진행시간", fontWeight = FontWeight.Bold)
            RoundedNumberSpinner(items = List(24) { i -> i + 1 },
                selectedItem = progressTime.hour,
                onItemSelected = { hour ->
                    onProgressTimeChanged(
                        HourMinute(
                            hour,
                            progressTime.minute
                        )
                    )
                }
            )
            Text(text = "시간")
            RoundedNumberSpinner(items = List(60) { i -> i + 1 },
                selectedItem = progressTime.minute,
                onItemSelected = { minute ->
                    onProgressTimeChanged(
                        HourMinute(
                            progressTime.hour,
                            minute
                        )
                    )
                }
            )
            Text(text = "분")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            taskOptions.forEach { taskType ->
                TaskTypeRadioButton(
                    text = taskType.taskName,
                    selected = taskType == selectedTaskType,
                    onOptionSelected = onSelectedTaskType,
                    item = taskType
                )
            }
        }
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(dayOfWeeks) { dayOfWeek ->
                RoundedFilterChip(
                    text = dayOfWeek.day,
                    checked = selectedDayOfWeeks.contains(dayOfWeek),
                    onCheckedChanged = { checked ->
                        if (checked) {
                            onDayOfWeeksChanged(selectedDayOfWeeks + listOf(dayOfWeek))
                        } else {
                            onDayOfWeeksChanged(selectedDayOfWeeks.filter { day -> day != dayOfWeek })
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (selectedCategory != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "카테고리", fontWeight = FontWeight.Bold)
                RoundedCategorySpinner(
                    items = categories,
                    selectedItem = selectedCategory,
                    onItemSelected = onCategoryChanged
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "알림", fontWeight = FontWeight.Bold)
            RoundedNumberSpinner(items = List(24) { i -> i + 1 },
                selectedItem = alarmTime.hour,
                onItemSelected = {
                    val localDateTime = LocalDateTime.now()
                    val localTime = LocalTime.of(it, alarmTime.minute)
                    onAlarmTimeChanged(localDateTime.with(localTime))
                }
            )
            Text(text = "시")
            RoundedNumberSpinner(items = List(60) { i -> i + 1 },
                selectedItem = alarmTime.minute,
                onItemSelected = {
                    val localDateTime = LocalDateTime.now()
                    val localTime = LocalTime.of(alarmTime.hour, it)
                    onAlarmTimeChanged(localDateTime.with(localTime))
                }
            )
            Text(text = "분")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            Text(text = "메모", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            ContentTextField(
                content = memo,
                onContentChanged = onMemoChanged
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTextField(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChanged: (String) -> Unit,
    focusManager: FocusManager
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = title,
        onValueChange = { newText ->
            onTitleChanged(newText)
        },
        placeholder = {
            Text(
                text = "제목",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.titleLarge,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions {
            focusManager.clearFocus()
        }
    )
}

@Composable
fun RoundedNumberSpinner(
    items: List<Int>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    Spinner(
        modifier = Modifier.wrapContentSize(),
        dropDownModifier = Modifier.height(200.dp),
        items = items,
        selectedItem = selectedItem,
        onItemSelected = onItemSelected,
        selectedItemFactory = { modifier, item ->
            Row(
                modifier = modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = item.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        },
        dropDownItemFactory = { item, _ ->
            Text(text = item.toString())
        }
    )
}

@Composable
fun RoundedCategorySpinner(
    items: List<Category>,
    selectedItem: Category,
    onItemSelected: (Category) -> Unit
) {
    Spinner(
        modifier = Modifier.wrapContentSize(),
        items = items,
        selectedItem = selectedItem,
        onItemSelected = onItemSelected,
        selectedItemFactory = { modifier, item ->
            Row(
                modifier = modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        },
        dropDownItemFactory = { item, _ ->
            Text(text = item.name)
        }
    )
}

@Composable
fun <T> Spinner(
    modifier: Modifier,
    dropDownModifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    selectedItemFactory: @Composable (Modifier, T) -> Unit,
    dropDownItemFactory: @Composable (T, Int) -> Unit
) {
    var expanded: Boolean by remember { mutableStateOf(false) }

    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        selectedItemFactory(
            Modifier
                .background(color = MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                .clickable { expanded = true },
            selectedItem
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = dropDownModifier
        ) {
            items.forEachIndexed { index, element ->
                DropdownMenuItem(
                    text = { dropDownItemFactory(element, index) },
                    onClick = {
                        onItemSelected(items[index])
                        expanded = false
                        Log.d(TAG, selectedItem.toString())
                    })
            }
        }
    }
}

@Composable
fun <T> TaskTypeRadioButton(
    text: String,
    selected: Boolean,
    onOptionSelected: (T) -> Unit,
    item: T
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.width(4.dp))
        RadioButton(selected = selected,
            onClick = {
                onOptionSelected(item)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedFilterChip(
    text: String,
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit
) {
    FilterChip(
        modifier = Modifier.wrapContentSize(),
        selected = checked,
        onClick = {
            onCheckedChanged(!checked)
        },
        label = {
            Text(text = text, style = MaterialTheme.typography.labelSmall)
        },
        shape = CircleShape
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentTextField(
    modifier: Modifier = Modifier,
    content: String,
    onContentChanged: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        value = content,
        onValueChange = {
            onContentChanged(it)
        },
    )
}

data class DayOfWeekState(
    val selectedDayOfWeek: List<DayOfWeek>
)


