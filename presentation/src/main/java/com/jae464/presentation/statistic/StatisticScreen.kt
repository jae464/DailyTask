package com.jae464.presentation.statistic

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.jae464.domain.model.ProgressTask
import com.jae464.presentation.common.calendar.CalendarState
import com.jae464.presentation.common.calendar.CustomCalendar
import com.jae464.presentation.common.calendar.rememberCalendarState
import com.jae464.presentation.statistic.model.StatisticViewMode
import kotlinx.coroutines.launch
import java.time.DateTimeException
import java.time.LocalDate

private const val TAG = "StatisticScreen"

@Composable
fun StatisticScreen(
    viewModel: StatisticViewModel = hiltViewModel()
) {
    val progressTasks by viewModel.progressTasks.collectAsStateWithLifecycle()
    var showCalendar by remember { mutableStateOf(true) }
    val calendarState = rememberCalendarState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Log.d(TAG, progressTasks.toString())

    Surface(
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
        ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "구간선택",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = {
                            showCalendar = !showCalendar
                        }) {
                            if (showCalendar) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "drop-down"
                                )
                            }
                            else {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropUp,
                                    contentDescription = "drop-up"
                                )
                            }
                        }
                    }
                    CustomCalendar(
                        calendarState = calendarState,
                        showCalendar = showCalendar
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp, bottom = 16.dp, top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        LoadPieChartButton(
                            calendarState = calendarState,
                            onClickLoad = { startDate, endDate ->
                                if (startDate == null || endDate == null) {
                                    val msg = if (startDate == null) "시작" else "종료"
                                    Toast.makeText(context, "${msg}기간을 지정해주세요", Toast.LENGTH_SHORT).show()
                                    return@LoadPieChartButton
                                }
                                viewModel.getProgressTasks(startDate, endDate)
                            }
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    StatisticTabLayout(
                        progressTasks = progressTasks
                    )
                }

            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatisticTabLayout(
    progressTasks: List<ProgressTask>
) {
    val pages = StatisticViewMode.values()
    val pagerState = rememberPagerState(
        pageCount = { 2 }
    )

    val scope = rememberCoroutineScope()
    
    Text(
        text = "일정통계",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(16.dp),
        fontWeight = FontWeight.Bold
    )
    
    Spacer(modifier = Modifier.height(16.dp))

    TabRow(
        containerColor = Color.White,
        selectedTabIndex = pagerState.currentPage
    ) {
        pages.forEachIndexed { index, page ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(index)
                    }
                },
                text = { Text(text = page.title)}
            )
        }
    }

    HorizontalPager(state = pagerState) {
        when(pagerState.currentPage) {
            0 -> {
                Text(text = "리스트 준비중")
            }
            1 -> {
                ProgressTaskPieChart(progressTasks = progressTasks)
            }
        }
    }
}

@Composable
fun LoadPieChartButton(
    calendarState: CalendarState,
    onClickLoad: (LocalDate?, LocalDate?) -> Unit
) {
    Button(
        modifier = Modifier,
        onClick = {
            Log.d(
                "StatisticScreen",
                "startDate : ${calendarState.startDate} endDate : ${calendarState.endDate}"
            )
            onClickLoad(calendarState.startDate, calendarState.endDate)
        }
    ) {
        Text(text = "불러오기")
    }
}

@Composable
fun ProgressTaskPieChart(progressTasks: List<ProgressTask>) {
    val pieChartConfig = PieChartConfig(
        sliceLabelTextColor = Color(0xFF333333),
        isAnimationEnable = true,
        showSliceLabels = true,
        activeSliceAlpha = 0.5f,
        animationDuration = 1500,
        backgroundColor = MaterialTheme.colorScheme.background
    )

    val colors = listOf(
        Color(0xFFFFB6C1),
        Color(0xFFFFFFB6),
        Color(0xFFADD8E6),
        Color(0xFFE6E6FA),
        Color(0xFF98FB98),
        Color(0xFFFFDAB9),
        Color(0xFFE6E6FA),
        Color(0xFF90EE90),
        Color(0xFFADD8E6),
        Color(0xFFFFE4C4)
    )

    val group = progressTasks.groupBy { it.title }
    val totalProgressedTime = progressTasks.sumOf { it.progressedTime }.toFloat() // 전체 진행된 시간

    val pieChartSlices = group.keys.mapIndexed { index, s ->
        val title = if (s.length >= 10) s.substring(0, 10) + "..." else s
        val progressedTime = group[s]?.sumOf { it.progressedTime }?.toFloat() ?: 0f

        PieChartData.Slice(
            title,
            (progressedTime / totalProgressedTime),
            colors[index % colors.size]
        )
    }.filter { it.value > 0f }

    Log.d(TAG, "pieChartSlices : $pieChartSlices")

    val pieChartData = PieChartData(
        slices = pieChartSlices,
        plotType = PlotType.Pie
    )

    if (pieChartSlices.isNotEmpty()) {
        PieChart(
            modifier = Modifier
                .width(400.dp)
                .wrapContentHeight(),
            pieChartData = pieChartData,
            pieChartConfig = pieChartConfig
        )
    }
}
