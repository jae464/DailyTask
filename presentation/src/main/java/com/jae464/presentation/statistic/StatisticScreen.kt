package com.jae464.presentation.statistic

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.jae464.presentation.utils.noRippleClickable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val TAG = "StatisticScreen"

@Composable
fun StatisticScreen(
    viewModel: StatisticViewModel = hiltViewModel(),
    onClickProgressTask: (StatisticDetailNavigationArgument) -> Unit = {}
) {
    val totalProgressTasksUiState by viewModel.totalProgressTasksUiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val filteredCategories by viewModel.filteredCategories.collectAsStateWithLifecycle()
    val filteredTaskType by viewModel.filteredTaskType.collectAsStateWithLifecycle()
    val filteredDayOfWeeks by viewModel.filteredDayOfWeeks.collectAsStateWithLifecycle()
    val event = viewModel.event

    val calendarState = rememberCalendarState()
    val context = LocalContext.current

    val scrollState = rememberScrollState()
    var showCalendar by remember { mutableStateOf(true) }
    var showFilterOption by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(event) {
        event.collect { event ->
            when (event) {
                is StatisticEvent.ScrollToFilterCard -> {
                    scrollState.animateScrollTo(event.offset)
                }
                is StatisticEvent.ScrollToStatisticList -> {
                    scrollState.animateScrollTo(event.offset)
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
                            viewModel.setCalendarHeight(it.height)
                        }
                        .animateContentSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .noRippleClickable {
                                showCalendar = !showCalendar
                            },
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
                    if (showCalendar) {
                        CustomCalendar(
                            calendarState = calendarState,
                        )
                    }
                }
                FilterOption(
                    categories = categories,
                    filteredCategories = filteredCategories,
                    onChangedFilteredCategories = viewModel::filterCategories,
                    filteredTaskType = filteredTaskType,
                    onChangedFilteredTaskType = viewModel::filterTaskType,
                    filteredDayOfWeeks = filteredDayOfWeeks,
                    onChangedFilteredDayOfWeeks = viewModel::filterDayOfWeeks,
                    onChangedSize = {
                        viewModel.setFilterCardHeight(it)
                    },
                    showFilterOption = showFilterOption,
                    onChangedShowFilterOption = {
                        showFilterOption = it
                        viewModel.scrollToFilterCard()
                    },
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
                        viewModel.getProgressTasks(startDate, endDate)
                        viewModel.scrollToStatisticList()

                    },
                    scrollState = scrollState,
                    onChangedButtonSize = viewModel::setLoadButtonHeight
                )
                Column(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    StatisticTabLayout(
                        calendarState = calendarState,
                        totalProgressTasksUiState = totalProgressTasksUiState,
                        onClickProgressTask = onClickProgressTask
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
    onChangedShowFilterOption: (Boolean) -> Unit = {},
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
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable {
                    onChangedShowFilterOption(!showFilterOption)
                },
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
            Column {
                Text(
                    text = "카테고리",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                CategoryFilterChips(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    categories = categories,
                    filteredCategories = filteredCategories,
                    onChangedFilteredCategories = onChangedFilteredCategories
                )
            }
            Column {
                Text(
                    text = "정기 / 비정기",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    TaskType.values().forEach {
                        TaskTypeRadioButton(
                            text = it.taskName,
                            selected = it == filteredTaskType,
                            onOptionSelected = onChangedFilteredTaskType,
                            item = it
                        )
                    }
                }
            }

            // TODO 요일 필터링
            Column {
                Text(
                    text = "요일",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
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
                                    onChangedFilteredDayOfWeeks(
                                        (filteredDayOfWeeks + listOf(
                                            dayOfWeek
                                        )).sorted()
                                    )
                                } else {
                                    onChangedFilteredDayOfWeeks(filteredDayOfWeeks.filter { day -> day != dayOfWeek })
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White,
                                labelColor = MaterialTheme.colorScheme.onSecondary,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
                                disabledLabelColor = MaterialTheme.colorScheme.secondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = MaterialTheme.colorScheme.primary,
                                disabledBorderColor = MaterialTheme.colorScheme.background,
                                disabledSelectedBorderColor = MaterialTheme.colorScheme.onSecondary
                            )
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
    calendarState: CalendarState,
    totalProgressTasksUiState: TotalProgressTasksUiState,
    onClickProgressTask: (StatisticDetailNavigationArgument) -> Unit = {}
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
                    calendarState = calendarState,
                    totalProgressTasksUiState = totalProgressTasksUiState,
                    onClickProgressTask = onClickProgressTask
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
    calendarState: CalendarState,
    totalProgressTasksUiState: TotalProgressTasksUiState,
    onClickProgressTask: (StatisticDetailNavigationArgument) -> Unit = {}
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
//                val chunkedTotalProgressTask = filteredTotalProgressTask.chunked(4)
//                val pagerState = rememberPagerState(
//                    pageCount = { chunkedTotalProgressTask.size }
//                )
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
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    filteredTotalProgressTask.forEach {
                        Spacer(modifier = Modifier.height(16.dp))
                        TotalProgressTaskItem(
                            calendarState = calendarState,
                            totalProgressTaskUiModel = it,
                            onClickProgressTask = onClickProgressTask
                        )
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
    calendarState: CalendarState,
    totalProgressTaskUiModel: TotalProgressTaskUiModel,
    onClickProgressTask: (StatisticDetailNavigationArgument) -> Unit = {}
) {
    val sumOfTotalTime = totalProgressTaskUiModel.totalTime.toFloat()
    val sumOfProgressedTime = totalProgressTaskUiModel.totalProgressedTime.toFloat()
    val sumOfNotProgressedTime = (sumOfTotalTime - sumOfProgressedTime).coerceAtLeast(0f)

    val progressedTimeSlice = PieChartData.Slice(
        "",
        (sumOfProgressedTime / sumOfTotalTime),
        MaterialTheme.colorScheme.tertiary
    )

    val notProgressedTimeSlice = PieChartData.Slice(
        "",
        (sumOfNotProgressedTime / sumOfTotalTime),
        MaterialTheme.colorScheme.error
    )

    val pieChartData = PieChartData(
        slices = listOf(progressedTimeSlice, notProgressedTimeSlice),
        plotType = PlotType.Pie
    )

    val pieChartConfig = PieChartConfig(
        sliceLabelTextColor = Color(0xFF333333),
        isAnimationEnable = false,
        showSliceLabels = false,
        activeSliceAlpha = 0.5f,
        animationDuration = 1500,
        backgroundColor = Color.White
    )

    Column(
        modifier = Modifier
            .clickable {
                onClickProgressTask(
                    StatisticDetailNavigationArgument(
                        totalProgressTaskUiModel.task.id,
                        calendarState.startDate.toString(),
                        calendarState.endDate.toString()
                    )
                )
            }
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(8f)
            ) {
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(2f)
            ) {
                PieChart(
                    modifier = Modifier
                        .size(56.dp),
                    pieChartData = pieChartData,
                    pieChartConfig = pieChartConfig
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${((sumOfProgressedTime / sumOfTotalTime) * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )

            }
        }
    }
}

@Composable
fun LoadPieChartButton(
    calendarState: CalendarState,
    onClickLoad: (LocalDate?, LocalDate?) -> Unit,
    scrollState: ScrollState,
    onChangedButtonSize: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var buttonHeight by remember { mutableIntStateOf(0) }
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
//                buttonHeight = it.height
                           onChangedButtonSize(it.height)
            },
        onClick = {
            Log.d(
                "StatisticScreen",
                "startDate : ${calendarState.startDate} endDate : ${calendarState.endDate}"
            )
            onClickLoad(calendarState.startDate, calendarState.endDate)
//            scope.launch {
//                if (!scrollState.isScrollInProgress) {
//                    scrollState.animateScrollTo(toYOffset + buttonHeight)
//                }
//            }
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
        backgroundColor = Color.White
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
