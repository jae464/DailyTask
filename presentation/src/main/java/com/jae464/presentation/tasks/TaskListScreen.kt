package com.jae464.presentation.tasks

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.AccessTimeFilled
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.SortBy
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType
import com.jae464.presentation.common.CategoryFilterChips
import com.jae464.presentation.extension.addFocusCleaner
import com.jae464.presentation.utils.getHeader
import com.jae464.presentation.utils.noRippleClickable
import com.jae464.presentation.utils.toHourMinuteFormat
import com.jae464.presentation.utils.toTimeFormat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import kotlin.math.roundToInt

@OptIn(ExperimentalToolbarApi::class, ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    onClickAddTask: () -> Unit,
    onClickTask: (Task) -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val toolbarState = rememberCollapsingToolbarScaffoldState()

    val taskListUiState by viewModel.taskListUiState2.collectAsStateWithLifecycle()
    val event = viewModel.event

    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val filteredCategories by viewModel.filteredCategories.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    val selectedTaskType by viewModel.filteredTaskType.collectAsStateWithLifecycle()
    val selectedDayOfWeeks by viewModel.filteredDayOfWeeks.collectAsStateWithLifecycle()

    var showDeleteDialog by remember {
        mutableStateOf<Pair<Task?, AnchoredDraggableState<DragValue>?>>(
            Pair(null, null)
        )
    } // 삭제할 taskId 저장
    var showBottomSheetDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    var isShowingKeyboard by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(event) {
        event.collectLatest { event ->
            when (event) {
                is TaskListEvent.SendToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is TaskListEvent.HideBottomSheetDialog -> showBottomSheetDialog = false

            }
        }
    }

    CollapsingToolbarScaffold(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            ),
        state = toolbarState,
        toolbar = {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .pin()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SearchTextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                text = searchText,
                                onValueChanged = viewModel::setSearchText,
                                focusManager = focusManager,
                                onChangedFocus = {isShowingKeyboard = it}
                            )
                            IconButton(onClick = {
                                viewModel.setSearchText("")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "검색",
                                tint = MaterialTheme.colorScheme.onSecondary,
                            )
                        }
                    }
                    Row {
                        IconButton(
                            onClick = {
                                showBottomSheetDialog = true
                            },
                            modifier = Modifier.padding(start = 8.dp)

                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "필터링",
                            )
                        }
                        CategoryFilterChips(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            categories = categories,
                            filteredCategories = filteredCategories,
                            onChangedFilteredCategories = viewModel::filterCategories
                        )
                    }
                }
            }
        },
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .addFocusCleaner(focusManager)
        ) {
            when (taskListUiState) {
                is TaskListUiState.Loading -> {
                    // task가 없을때 에러 발생
//                    CircularProgressIndicator(
//                        modifier = Modifier
//                            .align(Alignment.Center)
//                    )
                }

                is TaskListUiState.Success -> {
                    TaskList(
                        modifier = modifier,
                        taskListUiState = taskListUiState,
                        onClickTask = {
                            if (!isShowingKeyboard) {
                                onClickTask(it)
                            }
                            else {
                                focusManager.clearFocus()
                            }
                        },
                        onClickDelete = { task, anchoredState ->
                            showDeleteDialog = Pair(task, anchoredState)
                        },
                        onClickAddProgressTask = viewModel::insertProgressTaskToday
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

            if (showDeleteDialog.first != null) {
                // TODO !! 제거하기 위해 따로 Composable로 만들어서 빼기
                AlertDialog(
                    onDismissRequest = {
                        val anchoredDraggableState = showDeleteDialog.second
                        scope.launch {
                            anchoredDraggableState?.animateTo(DragValue.Center)
                        }
                        showDeleteDialog = Pair(null, null)
                    },
                    title = {
                        Text(text = "일정을 삭제하시겠습니까?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (showDeleteDialog.first != null) {
                                viewModel.deleteTask(showDeleteDialog.first!!)
                                showDeleteDialog = Pair(null, null)
                            }
                        }) {
                            Text(text = "삭제")
                        }
                    }
                )
            }

            if (showBottomSheetDialog) {
                FilterBottomSheetDialog(
                    onChangeShowBottomSheetDialog = {
                        showBottomSheetDialog = it
                    },
                    selectedSortBy = sortBy,
                    onChangedSortBy = viewModel::setSortBy,
                    selectedTaskType = selectedTaskType,
                    onChangedTaskType = viewModel::setTaskType,
                    selectedDayOfWeeks = selectedDayOfWeeks,
                    onChangedSelectedDayOfWeeks = viewModel::setFilteredDayOfWeeks,
                    onClickLoadButton = viewModel::getFilteredTasks
                )
            }
        }
        FloatingActionButton(
            onClick = onClickAddTask,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(vertical = 16.dp, horizontal = 8.dp),
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
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheetDialog(
    onChangeShowBottomSheetDialog: (Boolean) -> Unit,
    selectedSortBy: SortBy,
    onChangedSortBy: (SortBy) -> Unit,
    selectedTaskType: TaskType,
    onChangedTaskType: (TaskType) -> Unit,
    selectedDayOfWeeks: List<DayOfWeek>,
    onChangedSelectedDayOfWeeks: (List<DayOfWeek>) -> Unit,
    onClickLoadButton: () -> Unit
) {
    BottomSheetDialog(
        onDismissRequest = {
            onChangeShowBottomSheetDialog(false)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "정렬",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "날짜",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SortBy.values().map {
                        RectangleFilterChip(
                            title = it.title,
                            item =  it,
                            isSelected = selectedSortBy == it,
                            onClickItem = onChangedSortBy
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "필터",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "일정종류")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TaskType.values().map {
                        RectangleFilterChip(
                            title = it.taskName,
                            item =  it,
                            isSelected = selectedTaskType == it,
                            onClickItem = onChangedTaskType
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "요일")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.Center

                ) {
                    items(DayOfWeek.values()) {
                        RectangleFilterChip(
                            title = it.day + "요일",
                            item = it,
                            isSelected = selectedDayOfWeeks.contains(it),
                            onClickItem = { dayOfWeek ->
                                if (selectedDayOfWeeks.contains(dayOfWeek)) {
                                    onChangedSelectedDayOfWeeks(selectedDayOfWeeks.minus(dayOfWeek))
                                }
                                else {
                                    onChangedSelectedDayOfWeeks(selectedDayOfWeeks.plus(dayOfWeek))
                                }
                            }
                        )
                    }
//                    DayOfWeek.values().map {
//                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onClickLoadButton,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "적용하기",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    text: String,
    onValueChanged: (String) -> Unit,
    focusManager: FocusManager,
    onChangedFocus: (Boolean) -> Unit
) {
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    BasicTextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                onChangedFocus(it.isFocused)
            },
        value = text,
        onValueChange = { onValueChanged(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions {
            focusManager.clearFocus()
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskList(
    modifier: Modifier = Modifier,
    taskListUiState: TaskListUiState,
    onClickTask: (Task) -> Unit,
    onClickDelete: (Task, AnchoredDraggableState<DragValue>) -> Unit,
    onClickAddProgressTask: (Task) -> Unit,
) {
    val state = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val isScrollInProgress = state.isScrollInProgress

//    LaunchedEffect(isScrollInProgress) {
//        if (isScrollInProgress) {
//            focusManager.clearFocus()
//        }
//    }

    LaunchedEffect(taskListUiState) {
        if (taskListUiState is TaskListUiState.Success) {
            state.animateScrollToItem(0)
        }
    }

    if (taskListUiState is TaskListUiState.Success) {
        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            items(
                taskListUiState.tasks,
                key = { it.id }) { task ->
                Row(
                    Modifier.animateItemPlacement(
                        tween(durationMillis = 250)
                    )
                ) {
                    TaskItem(
                        task = task,
                        onClickTask = onClickTask,
                        onClickDelete = onClickDelete,
                        isScrolling = isScrollInProgress,
                        onClickAddProgressTask = onClickAddProgressTask,
                    )
                }
            }
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

enum class DragValue { Start, Center, End }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    task: Task,
    modifier: Modifier = Modifier,
    onClickTask: (Task) -> Unit,
    onClickDelete: (Task, AnchoredDraggableState<DragValue>) -> Unit,
    onClickAddProgressTask: (Task) -> Unit,
    isScrolling: Boolean = false,
) {
    val anchors = DraggableAnchors {
        DragValue.Start at -200.dp.value
        DragValue.Center at 0.dp.value
        DragValue.End at 200.dp.value
    }

    val scope = rememberCoroutineScope()

    var cardHeight by remember { mutableIntStateOf(0) }

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Center,
            positionalThreshold = { distance: Float ->
                distance * 0.5f
            },
            velocityThreshold = {
                50.dp.value
            },
            animationSpec = tween(),
            anchors = anchors
        )
    }

    LaunchedEffect(isScrolling) {
        if (isScrolling && !state.isAnimationRunning) {
            scope.launch {
                state.animateTo(DragValue.Center)
            }
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .wrapContentHeight()
            .anchoredDraggable(state, Orientation.Horizontal)
            .background(if (state.offset > 0) MaterialTheme.colorScheme.secondary else if (state.offset < 0) MaterialTheme.colorScheme.errorContainer else Color.White)

    ) {
        IconButton(
            onClick = {
                Log.d("TaskListScreen", "onClick add progress task button")
                onClickAddProgressTask(task)
                scope.launch {
                    state.animateTo(DragValue.Center)
                }
            },
            modifier =
            Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
//                    .wrapContentWidth()
                .fillMaxHeight()
        ) {
            Image(
                imageVector = Icons.Rounded.Today,
                contentDescription = "add_progress_task"
            )
        }

        IconButton(
            onClick = {
                Log.d("TaskListScreen", "onClick delete button")
                // TODO show alert dialog to confirm delete
                onClickDelete(task, state)
            },
            modifier =
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp)
//                    .wrapContentWidth()
                .fillMaxHeight()
        ) {
            Image(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "delete_task"
            )
        }

        Card(
            modifier = Modifier
                .offset {
                    IntOffset(
                        state
                            .requireOffset()
                            .roundToInt(), 0
                    )
                }
                .onSizeChanged {
                    Log.d("TaskListScreen", it.toString())
                    cardHeight = it.height
                }
                .noRippleClickable { onClickTask(task) }
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = task.getHeader(),
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.bodySmall

                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    task.dayOfWeeks.map {
                        RoundedBackgroundText(text = it.day)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.title,
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
                        text = task.progressTime.toTimeFormat(),
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
                        text = if (task.useAlarm) task.alarmTime.toHourMinuteFormat() else "없음",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> RectangleFilterChip(
    title: String,
    item: T,
    isSelected: Boolean,
    onClickItem: (T) -> Unit
) {
    FilterChip(
        selected = isSelected,
        shape = RoundedCornerShape(4.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedContainerColor = Color.Transparent,
            selectedLabelColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,

        ),
        border = FilterChipDefaults.filterChipBorder(
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            disabledBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            borderWidth = 1.dp,
            selectedBorderWidth = 1.dp,
            enabled = true,
            selected = true
        ),
        onClick = {
            onClickItem(item)
        },
        label = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }
    )
}
