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