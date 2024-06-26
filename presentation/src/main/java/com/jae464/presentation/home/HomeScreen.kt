package com.jae464.presentation.home

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jae464.domain.model.ProgressTask
import com.jae464.presentation.model.getRemainTimeString
import com.jae464.presentation.model.isOverTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onClickItem: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        event = viewModel::handleEvent,
        onClickItem = onClickItem
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    event: (HomeUiEvent) -> Unit,
    onClickItem: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            )
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        ProgressTaskList(
            progressUiTaskState = uiState.progressTaskState,
            onClickStart = {
                event(HomeUiEvent.StartProgressTask(it))
            },
            onClickItem = onClickItem,
        )
    }
}

@Composable
fun ProgressTaskList(
    modifier: Modifier = Modifier,
    progressUiTaskState: ProgressTaskState,
    onClickStart: (String) -> Unit,
    onClickItem: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        when (progressUiTaskState) {
            is ProgressTaskState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .padding(top = 24.dp)
                ) {
                    items(
                        progressUiTaskState.progressTasks,
                        key = null
                    ) { progressTaskUiModel ->
                        ProgressTaskItem(
                            progressTaskUiModel = progressTaskUiModel,
                            onClickStart = onClickStart,
                            onClickItem = onClickItem,
                            isProgressing = progressUiTaskState.progressingTaskId == progressTaskUiModel.id
                        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressTaskItem(
    modifier: Modifier = Modifier,
    progressTaskUiModel: ProgressTask,
    isProgressing: Boolean,
    onClickStart: (String) -> Unit,
    onClickItem: (String) -> Unit,
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            onClick = { onClickItem(progressTaskUiModel.id) }
        ) {
            Row(
                modifier = modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoundedTimer(
                    time = progressTaskUiModel.getRemainTimeString(),
                    isProgressing = isProgressing,
                    isOverTime = progressTaskUiModel.isOverTime(),
                )
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = "${progressTaskUiModel.task.taskType}/${progressTaskUiModel.category.name}",
                        color = MaterialTheme.colorScheme.onSecondary,
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    shape = RoundedCornerShape(32.dp),
                ) {
                    Text(fontWeight = FontWeight.Bold, text = if (isProgressing) "중지" else "시작")
                }
            }
        }
    }
}

@Composable
fun RoundedTimer(
    modifier: Modifier = Modifier,
    time: String,
    isProgressing: Boolean,
    isOverTime: Boolean,
) {
    Box(
        modifier
            .size(64.dp)
            .background(
                color = if (isOverTime) MaterialTheme.colorScheme.tertiary else if (isProgressing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isProgressing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary,
        )
    }
}