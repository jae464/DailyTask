package com.jae464.presentation.tasks

import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jae464.presentation.extension.addFocusCleaner
import com.jae464.presentation.model.DayOfWeek
import com.jae464.presentation.model.TaskType

const val addTaskScreenRoute = "add_task"
private const val TAG = "AddTaskScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            ),
        topBar = {
            AddTaskTopAppBar()
        }
    ) { padding ->
        Box(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            AddTaskBody(modifier = modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskTopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "일정 추가")
        },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back",
                )
            }
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
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
    modifier: Modifier = Modifier
) {
    // variables
    val taskOptions = listOf(TaskType.Regular, TaskType.Irregular)
    val dayOfWeeks = DayOfWeek.values()
    val focusManager = LocalFocusManager.current

    // states
    var title by remember { mutableStateOf("") }
    var progressHour by remember { mutableStateOf(1) }
    var progressMinute by remember { mutableStateOf(0) }
    val (selectedTaskType, onSelectedTaskType) = remember { mutableStateOf(taskOptions[0])}
    var selectedDayOfWeekState by remember { mutableStateOf(DayOfWeekState(listOf())) }

    Log.d("AddTaskBody", "Rendered")

    Column(modifier = modifier
        .fillMaxSize()
        .padding(8.dp)
        .addFocusCleaner(focusManager)
    ) {
        TitleTextField(
            title = title,
            onTitleChanged = {
                title = it
            },
            onFocusChanged = {
//                isTextFieldFocused = it
            },
            focusManager = focusManager
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "진행시간", fontWeight = FontWeight.Bold)
            RoundedNumberSpinner(items = List(24) { i -> i + 1 },
                selectedItem = progressHour,
                onItemSelected = { item -> progressHour = item }
            )
            Text(text = "시간")
            RoundedNumberSpinner(items = List(24) { i -> i + 1 },
                selectedItem = progressMinute,
                onItemSelected = { item -> progressMinute = item }
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
                    checked = selectedDayOfWeekState.selectedDayOfWeek.contains(dayOfWeek),
                    onCheckedChanged = { checked ->
                        val before = selectedDayOfWeekState.selectedDayOfWeek
                        selectedDayOfWeekState = if (checked) {
                            selectedDayOfWeekState.copy(
                                selectedDayOfWeek = before + listOf(dayOfWeek)
                            )
                        } else {
                            selectedDayOfWeekState.copy(
                                selectedDayOfWeek = before.filter { day -> day != dayOfWeek }
                            )
                        }
                    }
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTextField(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    focusManager: FocusManager
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
//                onFocusChanged(it.isFocused)
            },
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
                .background(Color.LightGray, CircleShape)
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
fun <T>TaskTypeRadioButton(
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
fun RoundedFilterChip(text: String,
                      checked: Boolean,
                      onCheckedChanged: (Boolean) -> Unit) {
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

data class DayOfWeekState(
    val selectedDayOfWeek : List<DayOfWeek>
)


