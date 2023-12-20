package com.jae464.presentation.common.calendar

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CustomCalendar(
    calendarState: CalendarState = rememberCalendarState()
) {
    CalendarHeader(
        calendarSelectState = calendarState.selectState,
        onChangedCalendarSelectState = {calendarSelectState -> calendarState.selectState = calendarSelectState }
    )
    when(calendarState.selectState) {
        CalendarSelectState.YEAR -> {
            YearCalendar()
        }
        CalendarSelectState.MONTH -> {
            MonthCalendar()
        }
        CalendarSelectState.DAY -> {
            DateCalendar()
        }
    }
}

@Composable
fun CalendarHeader(
    calendarSelectState: CalendarSelectState,
    onChangedCalendarSelectState: (CalendarSelectState) -> Unit
) {
    when (calendarSelectState) {
        CalendarSelectState.YEAR -> {
            Button(onClick = {  }) {
                Text(text = "년")
            }
        }
        CalendarSelectState.MONTH -> {
            Button(onClick = {onChangedCalendarSelectState(CalendarSelectState.YEAR)}) {
                Text(text = "월")
            }
        }
        CalendarSelectState.DAY -> {
            Button(onClick = {
                onChangedCalendarSelectState(CalendarSelectState.MONTH)
            }) {
                Text(text = "일")
            }
        }
    }
}

@Composable
fun YearCalendar() {
    val years = List(20) { i -> i + 1}
    LazyRow {
        items(
            years
        ) { year ->
            Text(text = year.toString())
        }
    }
}

@Composable
fun MonthCalendar() {
    val years = List(12) { i -> i + 1}
    LazyRow {
        items(
            years
        ) { year ->
            Text(text = year.toString())
        }
    }
}

@Composable
fun DateCalendar() {
    val years = List(30) { i -> i + 1}
    LazyRow {
        items(
            years
        ) { year ->
            Text(text = year.toString())
        }
    }
}