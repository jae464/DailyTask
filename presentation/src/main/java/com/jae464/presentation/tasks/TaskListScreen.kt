package com.jae464.presentation.tasks

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTimeFilled
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jae464.domain.model.Category
import com.jae464.presentation.common.RoundedFilterChip
import com.jae464.presentation.model.TaskUiModel
import com.jae464.presentation.sampledata.taskUiModels
import kotlin.math.roundToInt

@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    onClickAddTask: () -> Unit,
    onClickTask: (String) -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {

    val taskListUiState by viewModel.taskListUiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val filteredCategories by viewModel.filteredCategories.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf("") } // 삭제할 taskId 저장

    Surface(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            when (taskListUiState) {
                is TaskListUiState.Loading -> {
                    Text(text = "로딩중")
                }

                is TaskListUiState.Success -> {
                    TaskList(
                        modifier = modifier,
                        taskListUiState = taskListUiState,
                        categories = categories,
                        filteredCategories = filteredCategories,
                        onClickTask = onClickTask,
                        onClickDelete = { showDeleteDialog = it },
                        onChangedFilteredCategories = viewModel::filterCategories
                    )
                }

                is TaskListUiState.Empty -> {
                    Text(
                        text = "새로운 일정을 추가해주세요.",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {}
            }
            FloatingActionButton(
                onClick = onClickAddTask,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(vertical = 16.dp),
                backgroundColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "add_task",
                    modifier = Modifier
                        .wrapContentSize(),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            if (showDeleteDialog.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = ""
                    },
                    title = {
                        Text(text = "일정을 삭제하시겠습니까?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteTask(showDeleteDialog)
                            showDeleteDialog = ""
                        }) {
                            Text(text = "삭제")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TaskList(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    filteredCategories: List<Category>,
    taskListUiState: TaskListUiState,
    onClickTask: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    onChangedFilteredCategories: (List<Category>) -> Unit
) {
    if (taskListUiState is TaskListUiState.Success) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                CategoryFilterChips(
                    categories = categories,
                    filteredCategories = filteredCategories,
                    onChangedFilteredCategories = onChangedFilteredCategories
                )
            }
            items(
                taskListUiState.taskUiModels,
                key = { it.id }) { taskUiModel ->
                TaskItem(
                    taskUIModel = taskUiModel,
                    onClickTask = onClickTask,
                    onClickDelete = onClickDelete
                )
            }

        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterChips(
    categories: List<Category>,
    filteredCategories: List<Category>,
    onChangedFilteredCategories: (List<Category>) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp, top = 24.dp)
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
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Color.White,
                    disabledBorderColor = MaterialTheme.colorScheme.background
                )

            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeDismissItem(
    modifier: Modifier = Modifier,
    directions: Set<DismissDirection> = setOf(DismissDirection.EndToStart),
    enter: EnterTransition = expandVertically(),
    exit: ExitTransition = shrinkVertically(),
    background: @Composable (offset: Dp) -> Unit,
    content: @Composable (isDismissed: Boolean) -> Unit
) {
    val dismissState = rememberDismissState()
    val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)
    val offset = with(LocalDensity.current) { dismissState.offset.value.toDp() }

    AnimatedVisibility(
        modifier = modifier,
        visible = !isDismissed,
        enter = enter,
        exit = exit
    ) {
        SwipeToDismiss(
            modifier = modifier,
            state = dismissState,
            directions = directions,
            background = { background(offset) },
            dismissContent = { content(isDismissed) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TaskItem(
    taskUIModel: TaskUiModel,
    modifier: Modifier = Modifier,
    onClickTask: (String) -> Unit,
    onClickDelete: (String) -> Unit,
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .wrapContentHeight()
            .swipeable(
                anchors = mapOf(
                    0f to 0,
                    (-200).dp.value to 1
                ),
                thresholds = { _, _ ->
                    FractionalThreshold(0.3f)
                },
                state = swipeableState,
                orientation = Orientation.Horizontal
            )
            .background(if (swipeableState.offset.value < 0) MaterialTheme.colorScheme.errorContainer else Color.White)

    ) {

        IconButton(
            onClick = {
                Log.d("TaskListScreen", "onClick delete button")
                // TODO show alert dialog to confirm delete
                onClickDelete(taskUIModel.id)
            },
            modifier =
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp)
                .wrapContentWidth()
        ) {
            Image(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "delete_task"
            )
        }

        Card(
            modifier = modifier
                .offset {
                    IntOffset(swipeableState.offset.value.roundToInt(), 0)
                }
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            onClick = { onClickTask(taskUIModel.id) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = taskUIModel.header,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.bodySmall

                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    taskUIModel.dayOfWeek?.forEach { it ->
                        RoundedBackgroundText(text = it.day)
//                        Text(text = it.day, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = taskUIModel.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccessTimeFilled,
                        contentDescription = "progress_time",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        text = taskUIModel.progressTimeStr,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = "progress_time",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        text = if (taskUIModel.useAlarm) taskUIModel.alarmTime else "없음",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun RoundedBackgroundText(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .size(24.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
            .padding(1.dp),
        contentAlignment = Alignment.Center // 내용을 가운데로 정렬
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}



