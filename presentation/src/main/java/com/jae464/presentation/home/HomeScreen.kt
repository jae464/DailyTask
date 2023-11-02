package com.jae464.presentation.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val progressTaskState by viewModel.progressTaskState.collectAsStateWithLifecycle()

    Log.d("HomeScreen", progressTaskState.toString())
    Surface(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            )
            .fillMaxSize(),
    ) {
//        Box(modifier = modifier.fillMaxSize()) {
//            when (progressTaskState) {
//                is ProgressTaskState.Success -> {
//                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                        items(
//                            (progressTaskState as ProgressTaskState.Success).progressTasks,
//                            key = null
//                        ) {progressTaskUiModel ->
//                            ProgressTaskItem(progressTaskUiModel = progressTaskUiModel)
//                        }
//
//                    }
//                }
//
//                else -> {}
//            }
//        }
        ProgressTaskList(progressTaskState = progressTaskState)

    }
}

@Composable
fun ProgressTaskList(
    modifier: Modifier = Modifier,
    progressTaskState: ProgressTaskState
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (progressTaskState) {
            is ProgressTaskState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        progressTaskState.progressTasks,
                        key = null
                    ) {progressTaskUiModel ->
                        ProgressTaskItem(progressTaskUiModel = progressTaskUiModel)
                    }

                }
            }

            else -> {}
        }
    }
}

@Composable
fun ProgressTaskItem(
    modifier: Modifier = Modifier,
    progressTaskUiModel: ProgressTaskUiModel,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = progressTaskUiModel.categoryName)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = progressTaskUiModel.title)
            }
        }
    }
}