package com.jae464.presentation.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val progressTaskState by viewModel.progressTaskState.collectAsStateWithLifecycle()
    val progressingTaskState by viewModel.progressingTask.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            )
            .fillMaxSize(),
        color = Color.Black.copy(alpha = 0.05f)
    ) {
        ProgressTaskList(progressTaskState = progressTaskState,
            progressingTaskState = progressingTaskState,
            onClickStart = {
                viewModel.startProgressTask(it, context)
            }
        )
    }
}

@Composable
fun ProgressTaskList(
    modifier: Modifier = Modifier,
    progressTaskState: ProgressTaskState,
    progressingTaskState: ProgressingState,
    onClickStart: (String) -> Unit
) {
    Log.d("ProgressTaskList", progressTaskState.toString())
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        when (progressTaskState) {
            is ProgressTaskState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    items(
                        progressTaskState.progressTasks,
                        key = null
                    ) { progressTaskUiModel ->
                        if (progressingTaskState is ProgressingState.Progressing && progressingTaskState.progressTask.id == progressTaskUiModel.id) {
                            ProgressTaskItem(
                                progressTaskUiModel = progressingTaskState.progressTask.toProgressTaskUiModel(
                                    true
                                ),
                                onClickStart = onClickStart
                            )
                        } else {
                            ProgressTaskItem(
                                progressTaskUiModel = progressTaskUiModel,
                                onClickStart = onClickStart
                            )
                        }
                    }
                }
            }
            is ProgressTaskState.Empty -> {
                Text(
                    text = "오늘 진행할 일정이 존재하지 않습니다.",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {}
        }
    }
}

@Composable
fun ProgressTaskItem(
    modifier: Modifier = Modifier,
    progressTaskUiModel: ProgressTaskUiModel,
    onClickStart: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Row(
                modifier = modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoundedTimer(
                    time = progressTaskUiModel.remainTime,
                    isProgressing = progressTaskUiModel.isProgressing
                )
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = progressTaskUiModel.categoryName,
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = progressTaskUiModel.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = progressTaskUiModel.memo,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                }
                Button(
                    onClick = {
                        onClickStart(progressTaskUiModel.id)
                    },
                    modifier = Modifier
                        .wrapContentSize(),
                    colors = ButtonDefaults.buttonColors(),

                    ) {
                    Text(text = if (progressTaskUiModel.isProgressing) "중지" else "시작")
                }
            }
        }
    }
}

@Composable
fun RoundedTimer(
    modifier: Modifier = Modifier,
    time: Int,
    isProgressing: Boolean
) {
    val hour = time / 3600
    val minute = time % 3600 / 60
    val second = time % 3600 % 60

    Box(
        modifier
            .size(64.dp)
            .background(
                if (isProgressing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                CircleShape
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "%2d:%02d:%02d".format(hour, minute, second),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}