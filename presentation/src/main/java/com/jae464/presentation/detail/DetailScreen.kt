package com.jae464.presentation.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jae464.presentation.extension.addFocusCleaner
import com.jae464.presentation.model.getRemainTimeString
import com.jae464.presentation.model.isOverTime

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiEffect = viewModel.uiEffect
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiEffect) {
        uiEffect.collect {
            when (it) {
                is DetailUiEffect.UpdateTodayMemoCompleted -> {
                    onShowSnackbar("오늘의 메모 저장이 완료되었습니다.", null)
                }
            }
        }
    }

    DetailScreen(
        uiState = uiState,
        event = viewModel::handleEvent,
        onBackClick = onBackClick,
        focusManager = focusManager
    )

}

@Composable
fun DetailScreen(
    uiState: DetailUiState,
    event: (DetailUiEvent) -> Unit,
    onBackClick: () -> Unit,
    focusManager: FocusManager
) {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            DetailTopAppBar(onBackClick = onBackClick)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .addFocusCleaner(focusManager)
                .fillMaxSize()
        ) {
            DetailProgressTask(
                uiState = uiState,
                onClickStart = { event(DetailUiEvent.StartProgressTask) },
                onClickStop = { event(DetailUiEvent.StopProgressTask) },
                onClickSaveTodayMemo = {
                    event(DetailUiEvent.UpdateTodayMemo(it))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopAppBar(
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "")
        },
        navigationIcon = {
            IconButton(onClick = {
                onBackClick()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back",
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
        ),

        )
}

@Composable
fun DetailProgressTask(
    modifier: Modifier = Modifier,
    uiState: DetailUiState,
    onClickStart: () -> Unit,
    onClickStop: () -> Unit,
    onClickSaveTodayMemo: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    when (uiState.progressTaskState) {
        is ProgressTaskState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        is ProgressTaskState.Success -> {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = uiState.progressTaskState.progressTask.category.name,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text =  uiState.progressTaskState.progressTask.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(16.dp))
                ProgressTaskContent(content =  uiState.progressTaskState.progressTask.memo)
                Spacer(modifier = Modifier.height(16.dp))
                TodayMemoField(
                    onClickSaveTodayMemo = onClickSaveTodayMemo,
                    savedTodayMemo =  uiState.progressTaskState.progressTask.todayMemo
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text =  uiState.progressTaskState.progressTask.getRemainTimeString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = if ( uiState.progressTaskState.progressTask.isOverTime()) MaterialTheme.colorScheme.tertiary else Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(32.dp))
                if ( uiState.progressTaskState.isProgressing) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onClickStop() }
                    ) {
                        Text(text = "중지", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onClickStart() }
                    ) {
                        Text(text = "시작", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressTaskContent(
    modifier: Modifier = Modifier,
    content: String
) {
    Text(
        text = "설명",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun TodayMemoField(
    savedTodayMemo: String = "",
    onClickSaveTodayMemo: (String) -> Unit
) {
    var todayMemo by remember {
        mutableStateOf(
            savedTodayMemo
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "메모",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(onClick = {
            onClickSaveTodayMemo(todayMemo)
        }) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "save_today_memo",
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    BasicTextField(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
            .fillMaxWidth()
            .height(200.dp),
        value = todayMemo,
        onValueChange = {
            todayMemo = it
        },
    )

}