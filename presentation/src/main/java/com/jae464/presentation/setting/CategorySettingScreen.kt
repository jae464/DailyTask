package com.jae464.presentation.setting

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jae464.domain.model.Category

@Composable
fun CategorySettingScreen(
    onBackClick: () -> Unit,
    viewModel: CategorySettingViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            )
            .fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(onBackClick = onBackClick)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column {
                CategoryList(uiState)
            }
        }
    }
}

@Composable
fun CategoryList(
    categoryUiState: CategoryUiState,
) {
    Log.d("CategorySettingScreen", "CategoryList Rendered")
    when(categoryUiState) {
        is CategoryUiState.Loading -> {

        }
        is CategoryUiState.Failure -> {

        }
        is CategoryUiState.Success -> {
            Column {
                categoryUiState.categories.map {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = it.name)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(onClick = {  }) {
                                Icon(imageVector = Icons.Rounded.Edit, contentDescription = "edit_category", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            IconButton(onClick = {  }) {
                                Icon(imageVector = Icons.Rounded.Delete, contentDescription = "delete_category", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestCounter(
    counter: Int,
    onChangeCounter: (Int) -> Unit
) {
    Log.d("CategorySettingScreen", "TestCounter Rendered")
    Text(text = counter.toString())
    Button(onClick = { onChangeCounter(counter + 1) }) {

    }
}
