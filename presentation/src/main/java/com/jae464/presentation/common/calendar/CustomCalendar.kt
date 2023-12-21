package com.jae464.presentation.common.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun CustomCalendar(
    calendarState: CalendarState = rememberCalendarState()
) {
    var currentFocus by remember { mutableStateOf(CurrentFocus.START) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp)
    ) {
        DateSelector(calendarState = calendarState, currentFocus = currentFocus, onChangedFocus = {currentFocus = it})
        CalendarHeader(
            calendarState = calendarState,
            onChangedCalendarSelectState = { calendarSelectState ->
                calendarState.selectState = calendarSelectState
            }
        )
        when (calendarState.selectState) {
            CalendarSelectState.YEAR -> {
                YearCalendar(
                    calendarState = calendarState,
                    onChangedYear = {
                        calendarState.selectedYear = it
                        calendarState.selectState = CalendarSelectState.MONTH
                    }
                )
            }

            CalendarSelectState.MONTH -> {
                MonthCalendar(
                    calendarState = calendarState,
                    onChangedMonth = {
                        calendarState.selectedMonth = it
                        calendarState.selectState = CalendarSelectState.DAY
                    }
                )
            }

            CalendarSelectState.DAY -> {
                DateCalendar(
                    calendarState = calendarState,
                    currentFocus = currentFocus,
                    onChangedFocus = {currentFocus = it}
                )
            }
        }
    }
}

enum class CurrentFocus {
    START, END
}

@Composable
fun DateSelector(
    calendarState: CalendarState,
    currentFocus: CurrentFocus,
    onChangedFocus: (CurrentFocus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (currentFocus == CurrentFocus.START) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    onChangedFocus(CurrentFocus.START)
                }
                .padding(16.dp)
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "시작날짜", style = MaterialTheme.typography.labelSmall)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (calendarState.startDate == null) "" else
                            "${calendarState.startDate!!}",
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                    if (currentFocus == CurrentFocus.START && calendarState.startDate != null) {
                        IconButton(onClick = { calendarState.startDate = null }) {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = "delete-start-date")
                        }
                    }
                }

            }
        }
        Box(
            modifier = Modifier
                .background(
                    color = if (currentFocus == CurrentFocus.END) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    onChangedFocus(CurrentFocus.END)
                }
                .padding(16.dp)
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "종료날짜", style = MaterialTheme.typography.labelSmall)

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (calendarState.endDate == null) "" else
                            "${calendarState.endDate!!}",
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                    if (currentFocus == CurrentFocus.END && calendarState.endDate != null) {
                        IconButton(onClick = { calendarState.endDate = null }) {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = "delete-end-date")
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun CalendarHeader(
    calendarState: CalendarState,
    onChangedCalendarSelectState: (CalendarSelectState) -> Unit
) {
    when (calendarState.selectState) {
        CalendarSelectState.YEAR -> {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "${calendarState.selectedYear}년",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        CalendarSelectState.MONTH -> {
            Button(
                onClick = { onChangedCalendarSelectState(CalendarSelectState.YEAR) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "${calendarState.selectedYear}년",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        CalendarSelectState.DAY -> {
            Button(
                onClick = {
                    onChangedCalendarSelectState(CalendarSelectState.MONTH)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "${calendarState.selectedYear}년 ${calendarState.selectedMonth}월",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun YearCalendar(
    calendarState: CalendarState,
    onChangedYear: (Int) -> Unit
) {
    val years = mutableListOf<Int>()
    for (i in LocalDate.now().year - 10..LocalDate.now().year) {
        years.add(i)
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            years
        ) { year ->
            Button(
                onClick = {
                    onChangedYear(year)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer

                )
            ) {
                Text(text = year.toString())
            }
        }
    }
}

@Composable
fun MonthCalendar(
    calendarState: CalendarState,
    onChangedMonth: (Int) -> Unit
) {
    val months = List(12) { i -> i + 1 }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            months
        ) { month ->
            Button(
                onClick = { onChangedMonth(month) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer

                )
            ) {
                Text(text = month.toString())
            }
        }
    }
}

@Composable
fun DateCalendar(
    calendarState: CalendarState,
    currentFocus: CurrentFocus,
    onChangedFocus: (CurrentFocus) -> Unit
) {
    val dayLength =
        LocalDate.of(calendarState.selectedYear, calendarState.selectedMonth, 1).lengthOfMonth()
    val days = List(dayLength) { i -> i + 1 }
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            days
        ) { day ->
            val localDate = LocalDate.of(calendarState.selectedYear, calendarState.selectedMonth, day)
            val isOneSelected =
                (calendarState.startDate != null && calendarState.endDate == null) ||
                        (calendarState.startDate == null && calendarState.endDate != null)
            val isSelected =
                (calendarState.startDate != null && localDate == calendarState.startDate) ||
                        (calendarState.endDate != null && localDate == calendarState.endDate)

            val isBetween =
                (calendarState.startDate != null && calendarState.endDate != null && localDate > calendarState.startDate && localDate < calendarState.endDate)
            Box(
                modifier = Modifier
                    .background(
                        color = if ((isSelected && !isOneSelected) || isBetween) MaterialTheme.colorScheme.secondaryContainer else Color.White,
                        shape = if (isBetween) RectangleShape else if (isSelected && localDate == calendarState.startDate) RoundedCornerShape(
                            topStart = 32.dp,
                            bottomStart = 32.dp
                        ) else if (isSelected && localDate == calendarState.endDate) RoundedCornerShape(
                            topEnd = 32.dp,
                            bottomEnd = 32.dp
                        ) else CircleShape
                    )
                    .clickable {
                        if (currentFocus == CurrentFocus.START) {
                            calendarState.startDate = localDate
                            if (calendarState.endDate != null && localDate > calendarState.endDate) {
                                calendarState.endDate = null
                            }
                            onChangedFocus(CurrentFocus.END)
                        } else {
                            if (calendarState.startDate != null && localDate < calendarState.startDate) {
                                calendarState.startDate = localDate
                            } else {
                                calendarState.endDate = localDate
                            }
                        }
                    }
                    .wrapContentHeight()
                    .wrapContentWidth()
            ) {
                Text(
                    text = day.toString(), style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .wrapContentHeight()
                        .width(32.dp)
                        .padding(8.dp)
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}