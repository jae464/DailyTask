package com.jae464.presentation.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import java.time.LocalDate

inline fun Modifier.noRippleClickable(crossinline onClick: ()->Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

fun localDatesToString(localDates: List<LocalDate>): String {
    return if (localDates.isEmpty()) ""
    else {
        localDates.joinToString(",")
    }
}

fun stringToLocalDates(value: String): List<LocalDate> {
    if (value.isBlank()) {
        return emptyList()
    }
    else {
        val list = value.split(",")
        return list.map {
            LocalDate.parse(it)
        }
    }
}

fun intToProgressTimeFormat(time: Int): String {
    return "%2d시간 %02d분".format(time / 3600, time % 3600 / 60)
}

fun LocalDate.toKrFormat(): String {
    return "${this.year}년 ${this.monthValue}월 ${this.dayOfMonth}일"
}
