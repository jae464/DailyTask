package com.jae464.presentation.common.calendar

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
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
    startDate: LocalDate = LocalDate.now().minusYears(1),
    endDate: LocalDate = LocalDate.now(),
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
): ScrollableState {
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

    val prevMonth: Int
        get() {
            return LocalDate.of(selectedYear, selectedMonth, 1).minusMonths(1).monthValue
        }

    val nextMonth: Int
        get() {
            return LocalDate.of(selectedYear, selectedMonth, 1).plusMonths(1).monthValue
        }

    val lazyListState = LazyListState()

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

    override val isScrollInProgress: Boolean
        get() = lazyListState.isScrollInProgress

    override fun dispatchRawDelta(delta: Float): Float {
        return lazyListState.dispatchRawDelta(delta)
    }

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        lazyListState.scroll(scrollPriority, block)
    }
}

enum class CalendarSelectState {
    YEAR, MONTH, DAY
}

