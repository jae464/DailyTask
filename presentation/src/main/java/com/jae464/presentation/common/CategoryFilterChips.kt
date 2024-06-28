package com.jae464.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jae464.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterChips(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    filteredCategories: List<Category>,
    onChangedFilteredCategories: (List<Category>) -> Unit
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            categories,
            key = { it.id }
        ) {
            RoundedFilterChip(
                text = it.name,
                checked = filteredCategories.contains(it),
                onCheckedChanged = { checked ->
                    if (checked) {
                        onChangedFilteredCategories(filteredCategories + listOf(it))
                    } else {
                        onChangedFilteredCategories(filteredCategories.filter { category -> category != it })
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = MaterialTheme.colorScheme.onSecondary,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
                    disabledLabelColor = MaterialTheme.colorScheme.secondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = MaterialTheme.colorScheme.primary,
                    disabledBorderColor = MaterialTheme.colorScheme.background,
                    disabledSelectedBorderColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    }
}