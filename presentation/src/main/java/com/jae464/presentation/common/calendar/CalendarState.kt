package com.jae464.presentation.common.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.time.LocalDate

@Composable
fun rememberCalendarState(
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    selectState: CalendarSelectState = CalendarSelectState.DAY,
    selectedYear: Int = LocalDate.now().year,
    selectedMonth: Int = LocalDate.now().month.value,
): CalendarState {
    return rememberSaveable(saver = CalendarState.Saver) {
        CalendarState(
            startDate = startDate,
            endDate = endDate,
            selectState = selectState,
            selectedYear = selectedYear,
            selectedMonth = selectedMonth
        )
    }
}

@Stable
class CalendarState constructor(
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    selectState: CalendarSelectState = CalendarSelectState.DAY,
    selectedYear: Int = LocalDate.now().year,
    selectedMonth: Int = LocalDate.now().month.value,
){
    private var _startDate by mutableStateOf(startDate)
    var startDate: LocalDate?
        get() = _startDate
        set(value) {
            _startDate = value
        }

    private var _endDate by mutableStateOf(endDate)
    var endDate: LocalDate?
        get() = _endDate
        set(value) {
            _endDate = value
        }

    private var _selectState by mutableStateOf(selectState)
    var selectState: CalendarSelectState
        get() = _selectState
        set(value) {
            _selectState = value
        }

    private var _selectedYear by mutableStateOf(selectedYear)
    var selectedYear: Int
        get() = _selectedYear
        set(value) {
            _selectedYear = value
        }

    private var _selectedMonth by mutableStateOf(selectedMonth)
    var selectedMonth: Int
        get() = _selectedMonth
        set(value) {
            _selectedMonth = value
        }

    companion object {
        internal val Saver: Saver<CalendarState, Any> = listSaver(
            save = {
                listOf(
                    it.startDate,
                    it.endDate,
                    it.selectState,
                    it.selectedYear,
                    it.selectedMonth
                )
            },
            restore = {
                CalendarState(
                    startDate = it[0] as LocalDate?,
                    endDate = it[1] as LocalDate?,
                    selectState = it[2] as CalendarSelectState,
                    selectedYear = it[3] as Int,
                    selectedMonth = it[4] as Int
                )
            }
        )
    }
}

enum class CalendarSelectState {
    YEAR, MONTH, DAY
}

