package com.jae464.presentation.statistic

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.launch
import java.time.DateTimeException
import java.time.LocalDate

private const val TAG = "StatisticScreen"

@Composable
fun StatisticScreen(
    viewModel: StatisticViewModel = hiltViewModel()
) {

    var selectPeriod by remember { mutableStateOf(false) }
    var fromLocalDate by remember { mutableStateOf(LocalDate.now()) }
    var toLocalDate by remember { mutableStateOf(LocalDate.now()) }

    var yearMonthDay by remember { mutableStateOf(YearMonthDay(0, 0, 0)) }
    val progressTasks by viewModel.progressTasks.collectAsStateWithLifecycle()
    var showCalendar by remember { mutableStateOf(true) }
    val calendarState = rememberCalendarState()
    val context = LocalContext.current

    Log.d(TAG, progressTasks.toString())

    Surface(
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
        ),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .verticalScroll(rememberScrollState())
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
//                Spacer(modifier = Modifier.height(16.dp))
                CustomCalendar(
                    calendarState = calendarState,
                    showCalendar = showCalendar
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                ProgressTaskPieChart(progressTasks = progressTasks)
            }
        }
    }
}

@Composable
fun SelectPeriodRadioButton(
    selectPeriod: Boolean,
    onChangedSelectPeriod: (Boolean) -> Unit
) {
    Text(text = "안함")
    RadioButton(selected = !selectPeriod, onClick = { onChangedSelectPeriod(false) })
    Text(text = "함")
    RadioButton(selected = selectPeriod, onClick = { onChangedSelectPeriod(true) })
}

@Composable
fun SelectYearMonthDay(
    yearMonthDay: YearMonthDay,
    onChangedYear: (Int) -> Unit,
    onChangedMonth: (Int) -> Unit,
    onChangedDay: (Int) -> Unit
) {
    val years = mutableListOf<Int>()
    years.add(0)
    for (i in 2023 downTo 2000) {
        years.add(i)
    }
    val months = mutableListOf<Int>()
    for (i in 0..12) {
        months.add(i)
    }
    val days = mutableListOf<Int>()
    for (i in 0..31) {
        days.add(i)
    }

    Spinner(
        modifier = Modifier.wrapContentSize(),
        dropDownModifier = Modifier.height(200.dp),
        items = years,
        selectedItem = yearMonthDay.year,
        onItemSelected = onChangedYear,
        selectedItemFactory = { modifier, item ->
            Row(
                modifier = modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.Center,
            ) {
                if (item == 0) {
                    Text(
                        text = "전체",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                } else {
                    Text(
                        text = item.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }
        },
        dropDownItemFactory = { item, _ ->
            if (item == 0) {
                Text(text = "전체")
            } else {
                Text(text = item.toString())
            }
        }
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = "년")

    if (yearMonthDay.year != 0) {
        Spacer(modifier = Modifier.width(16.dp))
        Spinner(
            modifier = Modifier.wrapContentSize(),
            dropDownModifier = Modifier.height(200.dp),
            items = months,
            selectedItem = yearMonthDay.month,
            onItemSelected = onChangedMonth,
            selectedItemFactory = { modifier, item ->
                Row(
                    modifier = modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (item == 0) {
                        Text(
                            text = "전체",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    } else {
                        Text(
                            text = item.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }
            },
            dropDownItemFactory = { item, _ ->
                if (item == 0) {
                    Text(text = "전체")
                } else {
                    Text(text = item.toString())
                }
            }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "월")
    }
    if (yearMonthDay.month != 0 && yearMonthDay.year != 0) {
        Spacer(modifier = Modifier.width(16.dp))
        Spinner(
            modifier = Modifier.wrapContentSize(),
            dropDownModifier = Modifier.height(200.dp),
            items = days.filter {
                it <= LocalDate.of(yearMonthDay.year, yearMonthDay.month, 1).lengthOfMonth()
            },
            selectedItem = yearMonthDay.day,
            onItemSelected = onChangedDay,
            selectedItemFactory = { modifier, item ->
                Row(
                    modifier = modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (item == 0) {
                        Text(
                            text = "전체",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    } else {
                        Text(
                            text = item.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }
            },
            dropDownItemFactory = { item, _ ->
                if (item == 0) {
                    Text(text = "전체")
                } else {
                    Text(text = item.toString())
                }
            }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "일")
    }


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
fun GetStatisticButton(
    modifier: Modifier = Modifier,
    selectPeriod: Boolean,
    yearMonthDay: YearMonthDay,
    viewModel: StatisticViewModel
) {
    if (!selectPeriod) {
        val fromYear = if (yearMonthDay.year == 0) 2000 else yearMonthDay.year
        val fromMonth = if (yearMonthDay.month == 0) 1 else yearMonthDay.month
        val fromDay = if (yearMonthDay.day == 0) 1 else yearMonthDay.day
        val toYear = if (yearMonthDay.year == 0) 2023 else yearMonthDay.year
        val toMonth = if (yearMonthDay.month == 0) 12 else yearMonthDay.month
        val toDay = if (yearMonthDay.day == 0) LocalDate.of(toYear, toMonth, 1)
            .lengthOfMonth() else yearMonthDay.day
        val fromLocalDate = LocalDate.of(fromYear, fromMonth, fromDay)
        val toLocalDate = LocalDate.of(toYear, toMonth, toDay)
        Log.d(TAG, "$fromLocalDate $toLocalDate")
        Button(
            modifier = modifier,
            onClick = {
                viewModel.getProgressTasks(
                    fromLocalDate ?: LocalDate.now(),
                    toLocalDate ?: LocalDate.now()
                )
            }) {
            Text(text = "불러오기")
        }


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
