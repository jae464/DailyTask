package com.jae464.presentation.tasks

import android.util.Log
import android.view.RoundedCorner
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import co.yml.charts.common.extensions.isNotNull
import com.jae464.domain.model.Category
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.TaskType
import com.jae464.presentation.common.RoundedFilterChip
import com.jae464.presentation.common.TaskTypeRadioButton
import com.jae464.presentation.extension.addFocusCleaner
import java.time.LocalDateTime
import java.time.LocalTime

const val addTaskScreenRoute = "add_task"
private const val TAG = "AddTaskScreen"

@Composable
fun AddTaskScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val title by viewModel.title.collectAsStateWithLifecycle()
    val progressTimeHour by viewModel.progressTimeHour.collectAsStateWithLifecycle()
    val progressTimeMinute by viewModel.progressTimeMinute.collectAsStateWithLifecycle()
    val selectedTaskType by viewModel.selectedTaskType.collectAsStateWithLifecycle()
    val selectedDayOfWeeks by viewModel.selectedDayOfWeeks.collectAsStateWithLifecycle()
    val useAlarm by viewModel.useAlarm.collectAsStateWithLifecycle()
    val alarmTime by viewModel.alarmTime.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val memo by viewModel.memo.collectAsStateWithLifecycle()

    val event = viewModel.event
    val context = LocalContext.current

    LaunchedEffect(categories) {
        if (categories.isEmpty()) return@LaunchedEffect
        if (selectedCategory == null) {
            viewModel.onChangeSelectedCategory(categories.first())
        }
        else {
            viewModel.onChangeSelectedCategory(categories.last())
        }
    }

    LaunchedEffect(event) {
        event.collect {
            when (it) {
                is AddTaskEvent.SaveCompleted -> {
                    onBackClick()
                }
                is AddTaskEvent.ShowToastMessage -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Log.d(TAG, "AddTaskScreen Rendered()")
    Scaffold(
        containerColor = Color.White,
        topBar = {
            AddTaskTopAppBar(
                onBackClick = onBackClick,
                onSaveClick = {
                    viewModel.saveTask()
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
                progressTimeHour = progressTimeHour,
                progressTimeMinute = progressTimeMinute,
                selectedTaskType = selectedTaskType,
                selectedDayOfWeeks = selectedDayOfWeeks,
                useAlarm = useAlarm,
                alarmTime = alarmTime,
                memo = memo,
                categories = categories,
                selectedCategory = selectedCategory,
                onTitleChanged = { newTitle ->
                    if (newTitle.length > 30) {
                        Toast.makeText(context, "제목은 최대 30자까지 입력할 수 있습니다.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        viewModel.onChangeTitle(newTitle)
                    }
                },
                onProgressTimeHourChanged = { newHour ->
                    Log.d("AddTaskScreen", "progressTimeHour : $newHour")
                    viewModel.onChangeProgressTimeHour(newHour)
                },
                onProgressTimeMinuteChanged = { newMinute ->
                    Log.d("AddTaskScreen", "progressTimeMinute : $newMinute")
                    viewModel.onChangeProgressTimeMinute(newMinute)
                },
                onSelectedTaskType = {
                    viewModel.onChangeSelectedTaskType(it)
                },
                onDayOfWeeksChanged = viewModel::onChangeSelectedDayOfWeeks,
                onUseAlarmChanged = viewModel::onChangeUseAlarm,
                onAlarmTimeChanged = viewModel::onChangeAlarmTime,
                onMemoChanged = viewModel::onChangeMemo,
                onCategoryChanged = viewModel::onChangeSelectedCategory,
                onAddCategoryClick = { categoryName ->
                    if (categories.firstOrNull { it.name == categoryName }.isNotNull()) {
                        Toast.makeText(context, "이미 존재하는 카테고리입니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.addCategory(categoryName)
                    }
                }
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
    CenterAlignedTopAppBar(
        title = {
            Text(text = "")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBody(
    modifier: Modifier = Modifier,
    title: String,
    progressTimeHour: Int,
    progressTimeMinute: Int,
    selectedTaskType: TaskType,
    selectedDayOfWeeks: List<DayOfWeek>,
    useAlarm: Boolean,
    alarmTime: LocalDateTime,
    memo: String,
    categories: List<Category>,
    selectedCategory: Category?,
    onTitleChanged: (String) -> Unit,
    onProgressTimeHourChanged: (Int) -> Unit,
    onProgressTimeMinuteChanged: (Int) -> Unit,
    onSelectedTaskType: (TaskType) -> Unit,
    onDayOfWeeksChanged: (List<DayOfWeek>) -> Unit,
    onUseAlarmChanged: (Boolean) -> Unit,
    onAlarmTimeChanged: (LocalDateTime) -> Unit,
    onMemoChanged: (String) -> Unit,
    onCategoryChanged: (Category) -> Unit,
    onAddCategoryClick: (String) -> Unit
) {
    // variables
    val taskOptions = listOf(TaskType.Regular, TaskType.Irregular)
    val dayOfWeeks = DayOfWeek.values()
    val focusManager = LocalFocusManager.current

    // states
    val scrollState = rememberScrollState()
    var showAddCategoryDialog by remember { mutableStateOf(false) }

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
            focusManager = focusManager,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "진행시간", fontWeight = FontWeight.Bold)
            RoundedNumberSpinner(items = List(23) { i -> i + 1 },
                selectedItem = progressTimeHour,
                onItemSelected = { hour ->
                    onProgressTimeHourChanged(
                        hour
                    )
                }
            )
            Text(text = "시간")
            RoundedNumberSpinner(items = List(59) { i -> i + 1 },
                selectedItem = progressTimeMinute,
                onItemSelected = { minute ->
                    onProgressTimeMinuteChanged(
                        minute
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
        // 일정 타입이 정기일때만 요일 선택 칩을 보여준다.
        if (selectedTaskType == TaskType.Regular) {
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
                                onDayOfWeeksChanged((selectedDayOfWeeks + listOf(dayOfWeek)).sorted())
                            } else {
                                onDayOfWeeksChanged(selectedDayOfWeeks.filter { day -> day != dayOfWeek })
                            }
                        }
                    )
                }
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
                Text(
                    text = "새 카테고리",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable {
                        showAddCategoryDialog = true
                    },
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "알람", fontWeight = FontWeight.Bold)
            Switch(
                checked = useAlarm,
                onCheckedChange = onUseAlarmChanged,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (useAlarm) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "알람시간", fontWeight = FontWeight.Bold)
                RoundedNumberSpinner(items = List(23) { i -> i + 1 },
                    selectedItem = alarmTime.hour,
                    onItemSelected = {
                        val localDateTime = LocalDateTime.now()
                        val localTime = LocalTime.of(it, alarmTime.minute)
                        onAlarmTimeChanged(localDateTime.with(localTime))
                    }
                )
                Text(text = "시")
                RoundedNumberSpinner(items = List(59) { i -> i + 1 },
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
        }
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
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onSaveCategory = onAddCategoryClick,
            onChangedShowDialog = { showAddCategoryDialog = it })
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
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary
        )

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
            val title = if (item.name.length > 5) item.name.substring(0, 5) + "..." else item.name
            Row(
                modifier = modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "expand",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.onSecondary
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
    var showCategoryAddDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        selectedItemFactory(
            Modifier
                .background(color = MaterialTheme.colorScheme.secondary, CircleShape)
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
fun AddCategoryDialog(
    onSaveCategory: (String) -> Unit,
    onChangedShowDialog: (Boolean) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    AlertDialog(
        modifier = Modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
        ,
        onDismissRequest = { onChangedShowDialog(false) },
        title = {
            Column(

            ) {
                Text(
                    text = "새로운 카테고리 추가",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = categoryName,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = {
                        categoryName = it
                    })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSaveCategory(categoryName)
                onChangedShowDialog(false)
            }) {
                Text(text = "추가")
            }
        }
    )
}

//@Composable
//fun <T> TaskTypeRadioButton(
//    text: String,
//    selected: Boolean,
//    onOptionSelected: (T) -> Unit,
//    item: T
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(text = text)
//        Spacer(modifier = Modifier.width(4.dp))
//        RadioButton(selected = selected,
//            onClick = {
//                onOptionSelected(item)
//            }
//        )
//    }
//}

@Composable
fun ContentTextField(
    modifier: Modifier = Modifier,
    content: String,
    onContentChanged: (String) -> Unit
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
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


