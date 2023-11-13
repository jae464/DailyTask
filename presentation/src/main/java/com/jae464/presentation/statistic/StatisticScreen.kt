package com.jae464.presentation.statistic

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.jae464.domain.model.ProgressTask
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

    Log.d(TAG, progressTasks.toString())

    Surface(
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
        ),
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "구간지정", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(16.dp))
                    SelectPeriodRadioButton(
                        selectPeriod = selectPeriod,
                        onChangedSelectPeriod = { selectPeriod = it })
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "기간", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(16.dp))
                    if (selectPeriod) {

                    } else {
                        SelectYearMonthDay(
                            yearMonthDay = yearMonthDay,
                            onChangedYear = { yearMonthDay = yearMonthDay.copy(year = it) },
                            onChangedMonth = { yearMonthDay = yearMonthDay.copy(month = it) },
                            onChangedDay = { yearMonthDay = yearMonthDay.copy(day = it) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                GetStatisticButton(
                    modifier = Modifier.fillMaxWidth(),
                    selectPeriod = selectPeriod,
                    yearMonthDay = yearMonthDay,
                    viewModel = viewModel
                )
//                LazyColumn() {
//                    items(
//                        progressTasks,
//                        key = null
//                    ) { p ->
//                        Text(text = p.title)
//                    }
//                }
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
    if (yearMonthDay.month != 0) {
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
        isAnimationEnable = true,
        showSliceLabels = true,
        activeSliceAlpha = 0.5f,
        animationDuration = 1500
    )
    val colors = listOf(Color.Blue, Color.LightGray, Color.Magenta, Color.Gray, Color.Green, Color.Cyan, Color.DarkGray)
    val group = progressTasks.groupBy { it.title }

    val pieChartSlices = group.keys.mapIndexed { index, s ->
        Log.d(TAG, "$s 크기 : ${group[s]?.size?.toFloat() ?: 0f}")
        val title = if (s.length >= 10) s.substring(0,10) else s
        PieChartData.Slice(title, group[s]?.size?.toFloat() ?: 0f, colors[index % colors.size])
    }

    val pieChartData = PieChartData(
        slices = pieChartSlices,
        plotType = PlotType.Pie
    )

    if (pieChartSlices.isNotEmpty()) {
        PieChart(modifier = Modifier
            .width(400.dp)
            .height(400.dp),
            pieChartData = pieChartData,
            pieChartConfig = pieChartConfig)
    }
}
