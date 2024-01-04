package com.jae464.presentation.statistic

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.common.extensions.isNotNull
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import com.jae464.presentation.common.calendar.CalendarMode
import com.jae464.presentation.common.calendar.CalendarState
import com.jae464.presentation.common.calendar.CustomCalendar
import com.jae464.presentation.common.calendar.rememberCalendarState
import com.jae464.presentation.utils.intToTimeFormatString
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
        },
        containerColor = MaterialTheme.colorScheme.surface
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
                
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .fillMaxWidth()
                ) {
                    ProgressTaskItem(
                        uiState,
                        selectedLocalDate = selectedLocalDate,
                    )
                }
                
            }
        }

    }
}

@Composable
fun ProgressTaskItem(
    uiState: StatisticDetailUiState,
    selectedLocalDate: LocalDate,
) {
    if (uiState is StatisticDetailUiState.Success) {
        val progressTask = uiState.progressTasks.firstOrNull { it.createdAt == selectedLocalDate }
        if (progressTask.isNotNull()) {
            val totalTime = progressTask?.task?.progressTime?.toFloat() ?: 0f
            val progressedTime = progressTask?.progressedTime?.toFloat() ?: 0f
            val notProgressedTime = totalTime - progressedTime

            val progressedTimeSlice = PieChartData.Slice(
                "",
                (progressedTime / totalTime),
                MaterialTheme.colorScheme.tertiary
            )

            val notProgressedTimeSlice = PieChartData.Slice(
                "",
                (notProgressedTime / totalTime),
                MaterialTheme.colorScheme.error
            )

            val pieChartConfig = PieChartConfig(
                sliceLabelTextColor = Color(0xFF333333),
                isAnimationEnable = false,
                showSliceLabels = false,
                activeSliceAlpha = 0.5f,
                animationDuration = 1500,
                backgroundColor = Color.White
            )

            val pieChartData = PieChartData(
                slices = listOf(progressedTimeSlice, notProgressedTimeSlice),
                plotType = PlotType.Pie
            )
            Column(
                modifier = Modifier.padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "전체 시간 : ${intToTimeFormatString(progressTask?.task?.progressTime ?: 0)}")
                        Text(text = "진행한 시간 : ${intToTimeFormatString(progressTask?.progressedTime ?: 0)}")
                    }
                    PieChart(
                        modifier = Modifier
                            .size(56.dp),
                        pieChartData = pieChartData,
                        pieChartConfig = pieChartConfig
                    )
                }

                Text(text = "메모")
                Text(text = progressTask?.todayMemo ?: "")

            }
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
            containerColor = Color.White,
        ),
    )
}