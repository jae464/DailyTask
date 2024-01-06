package com.jae464.presentation.statistic

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.common.extensions.isNotNull
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.jae464.domain.model.ProgressTask
import com.jae464.presentation.common.calendar.CalendarMode
import com.jae464.presentation.common.calendar.CalendarState
import com.jae464.presentation.common.calendar.CustomCalendar
import com.jae464.presentation.common.calendar.rememberCalendarState
import com.jae464.presentation.utils.intToProgressTimeFormat
import com.jae464.presentation.utils.toKrFormat
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
    val selectedLocalDate by viewModel.selectedLocalDate.collectAsStateWithLifecycle()

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
                        onClickLocalDate =
                            viewModel::setSelectedLocalDate
                        )
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
                    ProgressTaskStatistic(
                        uiState,
                        selectedLocalDate = selectedLocalDate,
                    )
                }

            }
        }

    }
}

@Composable
fun ProgressTaskStatistic(
    uiState: StatisticDetailUiState,
    selectedLocalDate: LocalDate,
) {
    if (uiState is StatisticDetailUiState.Success) {
        val progressTask = uiState.progressTasks.firstOrNull { it.createdAt == selectedLocalDate }
        if (progressTask != null) {
            ProgressTaskStatisticItem(
                progressTask = progressTask,
                selectedLocalDate = selectedLocalDate
            )
        }
    }
}

@Composable
fun ProgressTaskStatisticItem(
    progressTask: ProgressTask,
    selectedLocalDate: LocalDate
) {
    val totalTime = progressTask.task.progressTime.toFloat()
    val progressedTime = progressTask.progressedTime.toFloat()
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
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = selectedLocalDate.toKrFormat(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(8f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "전체 시간 : ${intToProgressTimeFormat(progressTask?.task?.progressTime ?: 0)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "진행한 시간 : ${intToProgressTimeFormat(progressTask?.progressedTime ?: 0)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(
                modifier = Modifier.weight(2f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PieChart(
                    modifier = Modifier
                        .size(56.dp),
                    pieChartData = pieChartData,
                    pieChartConfig = pieChartConfig
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${((progressedTime / totalTime) * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        if (progressTask.todayMemo.isNotBlank()) {
            Text(
                text = "메모",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = progressTask.todayMemo)
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
        calendarState.selectedDates = uiState.progressTasks
            .filter {
                it.progressedTime > 0
            }
            .map {
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