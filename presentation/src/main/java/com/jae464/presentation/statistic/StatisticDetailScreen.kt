package com.jae464.presentation.statistic

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.common.extensions.isNotNull
import com.jae464.domain.model.ProgressTask
import com.jae464.presentation.common.calendar.CalendarMode
import com.jae464.presentation.common.calendar.CalendarState
import com.jae464.presentation.common.calendar.CustomCalendar
import com.jae464.presentation.common.calendar.rememberCalendarState
import java.time.LocalDate

@Composable
fun StatisticDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    taskId: String,
    viewModel: StatisticDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val task by viewModel.task.collectAsStateWithLifecycle()
    val calendarState = rememberCalendarState(
        calendarMode = CalendarMode.MULTISELECT
    )
    var selectedLocalDate by remember {
        mutableStateOf(LocalDate.now())
    }

    Scaffold(
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            )
            .fillMaxSize(),
        topBar = {
            StatisticDetailTopAppBar(title = task?.title ?: "", onBackClick = onBackClick)
        }
    ) { padding ->
        Box(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            )

            {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    MultiSelectCalendar(uiState = uiState, calendarState = calendarState,
                        onClickLocalDate = {
                            selectedLocalDate = it
                        })
                }
                
                Column {
                    ProgressTaskItem(
                        uiState,
                        selectedLocalDate = selectedLocalDate
                    )
                }
                
            }
        }

    }
}

@Composable
fun ProgressTaskItem(
    uiState: StatisticDetailUiState,
    selectedLocalDate: LocalDate
) {
    if (uiState is StatisticDetailUiState.Success) {
//        Log.d("StatisticDetailScreen", uiState.toString())
        val progressTask = uiState.progressTasks.firstOrNull() { it.createdAt == selectedLocalDate }
        Log.d("StatisticDetailScreen", progressTask.toString())
        if (progressTask.isNotNull()) {
            Text(text = "진행 일정")
            Text(text = progressTask?.title.toString())
        }
    }
}

@Composable
fun MultiSelectCalendar(
    uiState: StatisticDetailUiState,
    calendarState: CalendarState,
    onClickLocalDate: (LocalDate) -> Unit
) {
    if (uiState is StatisticDetailUiState.Success) {
        calendarState.selectedDates = uiState.progressTasks.map {
            it.createdAt
        }
    }

    CustomCalendar(
        calendarState = calendarState,
        onClickLocalDate = onClickLocalDate
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticDetailTopAppBar(
    title: String,
    onBackClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back",
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
        ),
    )
}