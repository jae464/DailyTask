package com.jae464.presentation.setting

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jae464.domain.model.Task
import com.jae464.presentation.common.calendar.CalendarState
import com.jae464.presentation.common.calendar.CustomCalendar
import com.jae464.presentation.common.calendar.rememberCalendarState
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@RequiresApi(34)
@Composable
fun ProgressTaskTestScreen(
    onBackClick: () -> Unit,
    viewModel: ProgressTaskTestViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val dialogUiState by viewModel.dialogUiState.collectAsStateWithLifecycle()
    val minTime by viewModel.minTime.collectAsStateWithLifecycle()

    val event = viewModel.event

    val calendarState = rememberCalendarState()

    var showCalendar by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(event) {
        event.collectLatest {
            when (it) {
                is TestEvent.ShowToastMessage -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Surface(
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
        ),
        color = MaterialTheme.colorScheme.surface
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(

            ) {
                Column(
                    modifier = Modifier.background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showCalendar = !showCalendar
                            }
                    ) {
                        Text(text = "구간선택")
                    }
                    if (showCalendar) {
                        CustomCalendar(
                            calendarState = calendarState
                        )
                    }
                }
                TaskList(
                    tasks = tasks,
                    onClickTask = {
                        viewModel.showTaskDialog(it)
                    }
                )
            }
            SettingDialog(
                dialogUiState = dialogUiState,
                calendarState = calendarState,
                minTime = minTime,
                onDismissDialog = viewModel::hideDialog,
                onConfirm = {
                    viewModel.insertProgressTask(
                        it,
                        calendarState.startDate ?: LocalDate.now(),
                        calendarState.endDate ?: LocalDate.now()
                    )
                },
                onChangeMinTime = viewModel::setMinTime,
                onClickDelete = viewModel::deleteAllProgressTaskByTask
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDialog(
    dialogUiState: DialogUiState,
    calendarState: CalendarState,
    minTime: String,
    onDismissDialog: () -> Unit,
    onConfirm: (Task) -> Unit,
    onChangeMinTime: (String) -> Unit,
    onClickDelete: (Task) -> Unit
) {
    if (dialogUiState is DialogUiState.ShowDialog) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(16.dp)),
            onDismissRequest = onDismissDialog
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = dialogUiState.task.title)
                Text(text = "${calendarState.startDate} ~ ${calendarState.endDate}")
                Text(text = "최소 진행 시간 설정")
                Column {
                    Text(text = "0 ~ ${dialogUiState.task.progressTime} 사이의 값을 입력하세요.")
                    OutlinedTextField(value = minTime, onValueChange = {
                        onChangeMinTime(it)
                    })
                }
                Row {
                    Button(onClick = { onConfirm(dialogUiState.task) }) {
                        Text(text = "생성하기")
                    }
                    Button(onClick = { onClickDelete(dialogUiState.task) }) {
                        Text(text = "전체 삭제하기")
                    }
                }
            }
        }

    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onClickTask: (Task) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            tasks,
            key = { it.id }
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable {
                    onClickTask(it)
                }
                .padding(16.dp)) {
                Text(text = it.title)
            }
        }
    }
}