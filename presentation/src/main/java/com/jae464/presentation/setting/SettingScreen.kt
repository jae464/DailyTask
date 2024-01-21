package com.jae464.presentation.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jae464.presentation.common.calendar.CalendarMode
import com.jae464.presentation.common.calendar.CustomCalendar
import com.jae464.presentation.common.calendar.rememberCalendarState

@Composable
fun SettingScreen(
    onClickTestScreen: () -> Unit
) {
    val calendarState = rememberCalendarState(
        calendarMode = CalendarMode.MULTISELECT
    )
    Surface(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            )
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Column {
                CustomCalendar(
                    calendarState = calendarState
                )
                CircularProgressIndicator()
                Button(onClick = onClickTestScreen) {
                    Text(text = "테스트 화면으로 이동하기")
                }
            }

        }
    }
}