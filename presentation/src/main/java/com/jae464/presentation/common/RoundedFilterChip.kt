package com.jae464.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedFilterChip(
    text: String,
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(),
    border: BorderStroke? = FilterChipDefaults.filterChipBorder(true, true),
) {
    FilterChip(
        modifier = Modifier.wrapContentSize(),
        selected = checked,
        onClick = {
            onCheckedChanged(!checked)
        },
        label = {
            Text(text = text, style = MaterialTheme.typography.labelSmall)
        },
        shape = CircleShape,
        colors = colors,
        border = border
    )
}