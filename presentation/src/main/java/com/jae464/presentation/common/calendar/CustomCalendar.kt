package com.jae464.presentation.common.calendar

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.system.measureTimeMillis

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomCalendar(
    modifier: Modifier = Modifier,
    calendarState: CalendarState = rememberCalendarState(),
    onClickLocalDate: (LocalDate) -> Unit = {}
) {
    var currentFocus by remember { mutableStateOf(CurrentFocus.START) }
//    var beforePage by remember { mutableStateOf(50) }
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 3 }
    )

    val scope = rememberCoroutineScope()

    var userScrollable by remember { mutableStateOf(true) }

//    LaunchedEffect(pagerState.currentPageOffsetFraction) {
//        launch {
//            Log.d(
//                "CustomCalendar",
//                "currentPageOffestFraction : ${pagerState.currentPageOffsetFraction.toString()}"
//            )
//            if (pagerState.currentPageOffsetFraction == 0f && pagerState.currentPage != 1) {
//                Log.d("CustomCalendar", "reset pager, current page : ${pagerState.currentPage}")
//                if (pagerState.currentPage == 2) {
//                    Log.d("CustomCalendar", "reset pager, scroll to page 1 시작")
//                    userScrollable = false
//                    calendarState.selectedMonth = calendarState.nextMonth
//                    if (calendarState.selectedMonth == 1) {
//                        calendarState.selectedYear = calendarState.selectedYear + 1
//                    }
//                    Log.d("CustomCalendar", "reset pager, scroll to page 1 중간 (스크롤 전)")
//                    pagerState.scrollToPage(1)
//                    userScrollable = true
//                    Log.d("CustomCalendar", "reset pager, scroll to page 1 완료")
//                } else if (pagerState.currentPage == 0) {
//                    Log.d("CustomCalendar", "reset pager, scroll to page 1 시작")
//                    userScrollable = false
//                    calendarState.selectedMonth = calendarState.prevMonth
//                    if (calendarState.selectedMonth == 12) {
//                        calendarState.selectedYear = calendarState.selectedYear - 1
//                    }
//                    Log.d("CustomCalendar", "reset pager, scroll to page 1 중간 (스크롤 전)")
//                    pagerState.scrollToPage(1)
//                    userScrollable = true
//                    Log.d("CustomCalendar", "reset pager, scroll to page 1 완료")
//                }
//            }
//        }
//    }

//    LaunchedEffect(pagerState.isScrollInProgress) {
//        Log.d("CustomCalendar", "isScrollInProgress : ${pagerState.isScrollInProgress}")
//        if (!pagerState.isScrollInProgress) {
//            if(pagerState.currentPage == 2) {
//                calendarState.selectedMonth = calendarState.nextMonth
//                pagerState.scrollToPage(1)
//                if (calendarState.selectedMonth == 1) {
//                    calendarState.selectedYear = calendarState.selectedYear + 1
//                }
//            }
//            else if (pagerState.currentPage == 0) {
//                calendarState.selectedMonth = calendarState.prevMonth
//                pagerState.scrollToPage(1)
//                if (calendarState.selectedMonth == 12) {
//                    calendarState.selectedYear = calendarState.selectedYear - 1
//                }
//            }
//        }
//    }
    Log.d("CustomCalendar", "calendarState changed : $calendarState")
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp)
            .animateContentSize()
    ) {
        // 임시 버튼 (페이지 확인용)
//        Button(onClick = {
//            Log.d("CustomCalendar", "currentPage : ${pagerState.currentPage.toString()}")
//        }) {
//
//        }
        if (calendarState.calendarMode == CalendarMode.INTERVAL) {
            DateSelector(
                calendarState = calendarState,
                currentFocus = currentFocus,
                onChangedFocus = { currentFocus = it }
            )
        }
        CalendarHeader(
            calendarState = calendarState,
            onChangedCalendarSelectState = { calendarSelectState ->
                calendarState.selectState = calendarSelectState
            },
            pagerState = pagerState
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
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        Log.d("CustomCalendar", "pagerState Changed Logic Start")
//                        Log.d("CustomCalendar", pagerState.currentPageOffsetFraction.toString())
                        // 이때 currentPageOffestFraction이 음수면 다음페이지, 양수면 이전페이지로 이동된거임

//                        scope.launch {
////                            pagerState.scrollToPage(page)
//                            // 이 코드는 실행되지 않음. currentPage로 animateScrollToPage를 하려고 할때 그냥 return하도록 내부적으로 구현되어있음
//                            pagerState.animateScrollToPage(page)
//                        }
//                        if (pagerState.currentPageOffsetFraction < 0) {
//                        }
//                        if (page > beforePage) {
//                            if (calendarState.nextMonth == 1) {
//                                calendarState.selectedYear = calendarState.selectedYear + 1
//                            }
//                            calendarState.selectedMonth = calendarState.nextMonth
//                            beforePage = page
//                        } else if (page < beforePage) {
//                            if (calendarState.prevMonth == 12) {
//                                calendarState.selectedYear = calendarState.selectedYear - 1
//                            }
//                            calendarState.selectedMonth = calendarState.prevMonth
//                            beforePage = page
//                        }
                        Log.d("CustomCalendar", "pagerState Changed Logic End")
                    }
                }
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.settledPage }.collect { page ->
                        Log.d("CustomCalendar", "settled page : $page")
                        if (!pagerState.isScrollInProgress) {
                            if(pagerState.currentPage == 2) {
                                calendarState.selectedMonth = calendarState.nextMonth
                                if (calendarState.selectedMonth == 1) {
                                    calendarState.selectedYear = calendarState.selectedYear + 1
                                }
                                pagerState.scrollToPage(1)
                            }
                            else if (pagerState.currentPage == 0) {
                                calendarState.selectedMonth = calendarState.prevMonth
                                if (calendarState.selectedMonth == 12) {
                                    calendarState.selectedYear = calendarState.selectedYear - 1
                                }
                                pagerState.scrollToPage(1)
                            }
                        }
                    }
                }

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.targetPage }.collect { page ->
                        Log.d("CustomCalendar", "target page : $page")
                    }
                }
                DateCalendar(
                    modifier = modifier,
                    calendarState = calendarState,
                    currentFocus = currentFocus,
                    onChangedFocus = { currentFocus = it },
                    pagerState = pagerState,
                    onClickLocalDate = onClickLocalDate
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
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "delete-start-date"
                            )
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
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "delete-end-date"
                            )
                        }
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarHeader(
    calendarState: CalendarState,
    onChangedCalendarSelectState: (CalendarSelectState) -> Unit,
    pagerState: PagerState,
) {
    val scope = rememberCoroutineScope()
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                Row {
                    IconButton(onClick = {
                        scope.launch {
                            if (pagerState.currentPage > 0) {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage - 1
                                )
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "left",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        if (pagerState.currentPage < pagerState.pageCount) {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage + 1
                                )
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "right",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

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
    for (i in LocalDate.now().year - 11..LocalDate.now().year) {
        years.add(i)
    }
    LazyVerticalGrid(
        modifier = Modifier.heightIn(max = 400.dp),
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
        modifier = Modifier.heightIn(max = 400.dp),
        columns = GridCells.Fixed(4),
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DateCalendar(
    modifier: Modifier = Modifier,
    calendarState: CalendarState,
    currentFocus: CurrentFocus,
    onChangedFocus: (CurrentFocus) -> Unit,
    pagerState: PagerState,
    onClickLocalDate: (LocalDate) -> Unit = {},
    userScrollable: Boolean = true
) {
    Log.d("CustomCalendar", "DateCalendar Rendered")
    HorizontalPager(
        state = pagerState,
        userScrollEnabled = userScrollable
    ) { page ->
        Log.d("CustomCalendar", "page : $page")
        when (page) {
            0 -> {
                // 전날 달력
                val prevMonth = calendarState.prevMonth
                val year =
                    if (prevMonth == 12) calendarState.selectedYear - 1 else calendarState.selectedYear
                DateCalendarContent(
                    calendarState = calendarState,
                    selectedYear = year,
                    selectedMonth = prevMonth,
                    currentFocus = currentFocus,
                    onChangedFocus = onChangedFocus,
                    onClickLocalDate = onClickLocalDate
                )
            }

            1 -> {
                DateCalendarContent(
                    calendarState = calendarState,
                    selectedYear = calendarState.selectedYear,
                    selectedMonth = calendarState.selectedMonth,
                    currentFocus = currentFocus,
                    onChangedFocus = onChangedFocus,
                    onClickLocalDate = onClickLocalDate
                )
            }

            2 -> {
                val nextMonth = calendarState.nextMonth
                val year =
                    if (nextMonth == 1) calendarState.selectedYear + 1 else calendarState.selectedYear
                DateCalendarContent(
                    calendarState = calendarState,
                    selectedYear = year,
                    selectedMonth = nextMonth,
                    currentFocus = currentFocus,
                    onChangedFocus = onChangedFocus,
                    onClickLocalDate = onClickLocalDate
                )
            }
        }
    }
}

@Composable
fun DateCalendarContent(
    calendarState: CalendarState,
    selectedYear: Int,
    selectedMonth: Int,
    currentFocus: CurrentFocus,
    onChangedFocus: (CurrentFocus) -> Unit,
    onClickLocalDate: (LocalDate) -> Unit = {}
) {
    Log.d("CustomCalendar", "DateCalendarContent Rendered")
    val currentMonth = LocalDate.of(selectedYear, selectedMonth, 1)
    val dayLengthOfCurrentMonth = currentMonth.lengthOfMonth()
    val beforeMonth = currentMonth.minusMonths(1)
    val dayLengthOfBeforeMonth = beforeMonth.lengthOfMonth()
    val nextMonth = currentMonth.plusMonths(1)

    val firstDayOfWeek = currentMonth.dayOfWeek.value
    val lastDayOfWeek =
        LocalDate.of(selectedYear, selectedMonth, dayLengthOfCurrentMonth).dayOfWeek.value

    Log.d("CustomCalendar", "첫번째 날의 요일 : $firstDayOfWeek")
    Log.d("CustomCalendar", "마지막 날의 요일 : $lastDayOfWeek")

    val localDates = mutableListOf<LocalDate>()
    for (i in 1..dayLengthOfCurrentMonth) {
        localDates.add(LocalDate.of(selectedYear, selectedMonth, i))
    }

    val beforeMonthLocalDates = mutableListOf<LocalDate>()
    for (i in dayLengthOfBeforeMonth - (firstDayOfWeek - 1) + 1..dayLengthOfBeforeMonth) {
        beforeMonthLocalDates.add(LocalDate.of(beforeMonth.year, beforeMonth.monthValue, i))
    }
    localDates.addAll(0, beforeMonthLocalDates)

    for (i in 1..7 - lastDayOfWeek) {
        localDates.add(LocalDate.of(nextMonth.year, nextMonth.monthValue, i))
    }

    val dayOfWeeks = listOf("월", "화", "수", "목", "금", "토", "일")
    LazyVerticalGrid(
        modifier = Modifier
            .background(Color.Transparent)
            .heightIn(max = 400.dp),
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dayOfWeeks) {
            Box {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    color = if (it == "토") MaterialTheme.colorScheme.primary else if (it == "일") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        when (calendarState.calendarMode) {
            CalendarMode.INTERVAL -> {
                items(
                    localDates
                ) { localDate ->
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
                                color = if ((isSelected && !isOneSelected) || isBetween) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
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
                        val textColor =
                            if (isSelected) MaterialTheme.colorScheme.onPrimary else if (localDate.month.value == selectedMonth) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                        Text(
                            text = localDate.dayOfMonth.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor,
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .wrapContentHeight()
                                .width(40.dp)
                                .padding(8.dp)
                                .align(Alignment.Center),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            CalendarMode.MULTISELECT -> {
                items(
                    localDates
                ) { localDate ->
                    val isSelected = calendarState.selectedDates.contains(localDate)
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape

                            )
                            .clickable {
                                if (isSelected) {
                                    onClickLocalDate(localDate)
                                }
                            }
                            .wrapContentHeight()
                            .wrapContentWidth()
                    ) {
                        val textColor =
                            if (isSelected) MaterialTheme.colorScheme.onPrimary else if (localDate.month.value == selectedMonth) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                        Text(
                            text = localDate.dayOfMonth.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor,
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .wrapContentHeight()
                                .width(40.dp)
                                .padding(8.dp)
                                .align(Alignment.Center),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}