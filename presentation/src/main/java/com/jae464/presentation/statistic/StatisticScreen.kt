package com.jae464.presentation.statistic

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.jae464.domain.model.Category
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.TaskType
import com.jae464.presentation.common.CategoryFilterChips
import com.jae464.presentation.common.RoundedFilterChip
import com.jae464.presentation.common.TaskTypeRadioButton
import com.jae464.presentation.common.calendar.CalendarState
import com.jae464.presentation.common.calendar.CustomCalendar
import com.jae464.presentation.common.calendar.rememberCalendarState
import com.jae464.presentation.statistic.model.StatisticViewMode
import com.jae464.presentation.statistic.model.TotalProgressTaskUiModel
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val TAG = "StatisticScreen"

@Composable
fun StatisticScreen(
    viewModel: StatisticViewModel = hiltViewModel()
) {
    val totalProgressTasksUiState by viewModel.totalProgressTasksUiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val filteredCategories by viewModel.filteredCategories.collectAsStateWithLifecycle()
    val filteredTaskType by viewModel.filteredTaskType.collectAsStateWithLifecycle()
    val filteredDayOfWeeks by viewModel.filteredDayOfWeeks.collectAsStateWithLifecycle()
    
    val calendarState = rememberCalendarState()
    val context = LocalContext.current
    
    val scrollState = rememberScrollState()
    var toYOffset by remember { mutableStateOf(0) }
    var calendarHeight by remember { mutableStateOf(0) }
    var filterHeight by remember { mutableStateOf(0) }
    var showCalendar by remember { mutableStateOf(true) }
    var showFilterOption by remember { mutableStateOf(false) }

//    val filteredProgressTasks by viewModel.filteredProgressTasks.collectAsStateWithLifecycle()
//
//    Log.d(TAG, filteredProgressTasks.toString())

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
                .animateContentSize()
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
                        .onSizeChanged {
                            calendarHeight = it.height
                        }
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
                            } else {
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
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(end = 16.dp, bottom = 16.dp, top = 16.dp),
//                        horizontalArrangement = Arrangement.End
//                    ) {
//                        LoadPieChartButton(
//                            calendarState = calendarState,
//                            onClickLoad = { startDate, endDate ->
//                                if (startDate == null || endDate == null) {
//                                    val msg = if (startDate == null) "시작" else "종료"
//                                    Toast.makeText(context, "${msg}기간을 지정해주세요", Toast.LENGTH_SHORT)
//                                        .show()
//                                    return@LoadPieChartButton
//                                }
//                                viewModel.getProgressTasks2(startDate, endDate)
//                            },
//                            scrollState = scrollState,
//                            toYOffset = calendarHeight + filterHeight
//                        )
//                    }
                }
                FilterOption(
                    categories = categories,
                    filteredCategories = filteredCategories,
                    onChangedFilteredCategories = viewModel::filterCategories,
                    filteredTaskType = filteredTaskType,
                    onChangedFilteredTaskType = viewModel::filterTaskType,
                    filteredDayOfWeeks = filteredDayOfWeeks,
                    onChangedFilteredDayOfWeeks = viewModel::filterDayOfWeeks,
                    onChangedSize = {filterHeight = it},
                    showFilterOption = showFilterOption,
                    onChangedShowFilterOption = {showFilterOption = it}
                )
                LoadPieChartButton(
                    calendarState = calendarState,
                    onClickLoad = { startDate, endDate ->
                        if (startDate == null || endDate == null) {
                            val msg = if (startDate == null) "시작" else "종료"
                            Toast.makeText(context, "${msg}기간을 지정해주세요", Toast.LENGTH_SHORT)
                                .show()
                            return@LoadPieChartButton
                        }
                        viewModel.getProgressTasks2(startDate, endDate)
                    },
                    scrollState = scrollState,
                    toYOffset = calendarHeight + filterHeight
                )
                Column(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    StatisticTabLayout(
                        totalProgressTasksUiState = totalProgressTasksUiState,
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterOption(
    categories: List<Category>,
    filteredCategories: List<Category>,
    onChangedFilteredCategories: (List<Category>) -> Unit,
    filteredTaskType: TaskType,
    onChangedFilteredTaskType: (TaskType) -> Unit,
    filteredDayOfWeeks: List<DayOfWeek>,
    onChangedFilteredDayOfWeeks: (List<DayOfWeek>) -> Unit,
    onChangedSize: (Int) -> Unit = {},
    showFilterOption: Boolean = true,
    onChangedShowFilterOption: (Boolean) -> Unit = {}
) {
    val dayOfWeeks = DayOfWeek.values()
    Column(
        modifier = Modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .onSizeChanged {
                onChangedSize(it.height)
            }
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text = "상세옵션",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(16.dp),
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = {
                onChangedShowFilterOption(!showFilterOption)
            }) {
                if (showFilterOption) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "drop-down"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowDropUp,
                        contentDescription = "drop-up"
                    )
                }
            }
        }
        if (showFilterOption) {
            // TODO 카테고리 필터링
            Column {
                Text(text = "카테고리", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
                CategoryFilterChips(
                    categories = categories,
                    filteredCategories = filteredCategories,
                    onChangedFilteredCategories = onChangedFilteredCategories
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // TODO 정기, 비정기 필터링
            Column {
                Text(text = "정기 / 비정기", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    TaskType.values().forEach {
                        TaskTypeRadioButton(
                            text = it.taskName,
                            selected = it == filteredTaskType,
                            onOptionSelected = onChangedFilteredTaskType,
                            item = it)
                    }
                }
            }

            // TODO 요일 필터링
            Column {
                Text(text = "요일", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(dayOfWeeks) { dayOfWeek ->
                        RoundedFilterChip(
                            text = dayOfWeek.day,
                            checked = filteredDayOfWeeks.contains(dayOfWeek),
                            onCheckedChanged = { checked ->
                                if (checked) {
                                    onChangedFilteredDayOfWeeks((filteredDayOfWeeks + listOf(dayOfWeek)).sorted())
                                } else {
                                    onChangedFilteredDayOfWeeks(filteredDayOfWeeks.filter { day -> day != dayOfWeek })
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatisticTabLayout(
    modifier: Modifier = Modifier,
    totalProgressTasksUiState: TotalProgressTasksUiState,
) {
    val pages = StatisticViewMode.values()
    val pagerState = rememberPagerState(
        pageCount = { 2 }
    )

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    Text(
        text = "일정통계",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .padding(16.dp)
            .onPlaced {
//                toYOffset = it.positionInWindow().y.toInt()
//                Log.d(TAG, toYOffset.toString())
            },
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
                text = { Text(text = page.title) }
            )
        }
    }

    HorizontalPager(
        state = pagerState, modifier = Modifier
            .padding(bottom = 16.dp)
            .onSizeChanged {
                val size = density.run { DpSize(it.width.toDp(), it.height.toDp()) }
                Log.d("StatisticScreen", "Tab Height : $size")
            }
            .wrapContentHeight(),
        verticalAlignment = Alignment.Top
    ) {
        when (it) {
            0 -> {
                TotalProgressTaskList(
                    totalProgressTasksUiState = totalProgressTasksUiState,
                )
            }

            1 -> {
                TotalProgressTaskPieChart(totalProgressTasksUiState = totalProgressTasksUiState)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TotalProgressTaskList(
    totalProgressTasksUiState: TotalProgressTasksUiState,
) {
    when (totalProgressTasksUiState) {
        is TotalProgressTasksUiState.Success -> {
            val filteredTotalProgressTask =
                totalProgressTasksUiState.totalProgressTasks.filter { it.totalProgressedTime > 60 }
            if (filteredTotalProgressTask.isEmpty()) {
                Text(
                    text = "해당 기간에 진행된 일정이 없습니다.",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                val chunkedTotalProgressTask = filteredTotalProgressTask.chunked(4)
                val pagerState = rememberPagerState(
                    pageCount = { chunkedTotalProgressTask.size }
                )
                // 방법 1. HorizontalPager 사용
//                HorizontalPager(
//                    state = pagerState,
//                    verticalAlignment = Alignment.Top,
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .fillMaxWidth(),
//                        verticalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        chunkedTotalProgressTask[it].forEach { totalProgressTaskUiModel ->
//                            TotalProgressTaskItem(totalProgressTaskUiModel = totalProgressTaskUiModel)
//                            Divider(
//                                modifier = Modifier.padding(top = 16.dp),
//                                color = MaterialTheme.colorScheme.surface,
//                                thickness = 1.dp
//                            )
//                        }
//                    }
//                }

                // 방법 2. Column 사용
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    filteredTotalProgressTask.forEach {
                        TotalProgressTaskItem(totalProgressTaskUiModel = it)
                        Divider(
                            modifier = Modifier.padding(top = 16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            thickness = 1.dp
                        )
                    }
                }
//                LazyColumn(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth()
//                        .wrapContentHeight(),
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    items(
//                        totalProgressTasksUiState.totalProgressTasks.filter { it.totalProgressedTime > 60 },
//                        key = {it.title}
//                    ) { totalProgressTask ->
//                        TotalProgressTaskItem(totalProgressTaskUiModel = totalProgressTask)
//                        Divider(
//                            modifier = Modifier.padding(top = 16.dp),
//                            color = MaterialTheme.colorScheme.surface,
//                            thickness = 1.dp
//                        )
//                    }
//                }
            }
        }

        is TotalProgressTasksUiState.Empty -> {
            Text(
                text = "해당 기간에 진행된 일정이 없습니다.",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        else -> {}
    }

}

@Composable
fun TotalProgressTaskItem(
    totalProgressTaskUiModel: TotalProgressTaskUiModel
) {
    Column(modifier = Modifier.wrapContentHeight()) {
        Text(
            text = totalProgressTaskUiModel.category.name,
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = totalProgressTaskUiModel.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "총 진행한 시간 : ${totalProgressTaskUiModel.totalProgressedTimeStr}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LoadPieChartButton(
    calendarState: CalendarState,
    onClickLoad: (LocalDate?, LocalDate?) -> Unit,
    scrollState: ScrollState,
    toYOffset: Int
) {
    val scope = rememberCoroutineScope()
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            Log.d(
                "StatisticScreen",
                "startDate : ${calendarState.startDate} endDate : ${calendarState.endDate}"
            )
            onClickLoad(calendarState.startDate, calendarState.endDate)
            scope.launch {
                if (!scrollState.isScrollInProgress) {
                    scrollState.animateScrollTo(toYOffset)
                }
            }
        }
    ) {
        Text(text = "불러오기")
    }
}

@Composable
fun TotalProgressTaskPieChart(totalProgressTasksUiState: TotalProgressTasksUiState) {
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

    when (totalProgressTasksUiState) {
        is TotalProgressTasksUiState.Success -> {
            val sumOfTotalProgressedTime =
                totalProgressTasksUiState.totalProgressTasks.sumOf { it.totalProgressedTime }
                    .toFloat()
            val pieChartSlices =
                totalProgressTasksUiState.totalProgressTasks.mapIndexed { index, totalProgressTaskUiModel ->
                    val title =
                        if (totalProgressTaskUiModel.title.length >= 10) totalProgressTaskUiModel.title.substring(
                            0,
                            10
                        ) + "..." else totalProgressTaskUiModel.title
                    val totalProgressedTime = totalProgressTaskUiModel.totalProgressedTime.toFloat()
                    PieChartData.Slice(
                        title,
                        (totalProgressedTime / sumOfTotalProgressedTime),
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

        is TotalProgressTasksUiState.Empty -> {
            Text(
                text = "해당 기간에 진행된 일정이 없습니다.",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        else -> {}
    }
}
