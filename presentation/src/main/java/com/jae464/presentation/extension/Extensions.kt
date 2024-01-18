package com.jae464.presentation.extension

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.addFocusCleaner(focusManager: FocusManager, doOnClear: () -> Unit = {} ): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures {
            Log.d("Extensions", "onPress")
            doOnClear()
            focusManager.clearFocus()
        }
    }
}