package com.jae464.presentation.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> TaskTypeRadioButton(
    text: String,
    selected: Boolean,
    onOptionSelected: (T) -> Unit,
    item: T
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.width(4.dp))
        RadioButton(selected = selected,
            onClick = {
                onOptionSelected(item)
            }
        )
    }
}