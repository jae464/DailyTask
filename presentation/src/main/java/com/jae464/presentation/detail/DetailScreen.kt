package com.jae464.presentation.detail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jae464.domain.model.ProgressTask
import com.jae464.presentation.extension.addFocusCleaner
import com.jae464.presentation.home.ProgressingState
import com.jae464.presentation.home.toProgressTaskUiModel

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            )
            .fillMaxSize(),
        topBar = {
            DetailTopAppBar(onBackClick = onBackClick)
        }
    ) { padding ->
        Box(
            modifier = modifier
                .padding(padding)
                .addFocusCleaner(focusManager)
                .fillMaxSize()
        ) {
            DetailProgressTask(
                uiState = uiState,
                onClickStart = { viewModel.startProgressTask(context) },
                onClickStop = { viewModel.stopProgressTask() },
                onClickSaveTodayMemo = { viewModel.updateTodayMemo(it) }
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

    when (uiState) {
        is DetailUiState.Loading -> {

        }

        is DetailUiState.Success -> {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = uiState.progressTaskUiModel.categoryName,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.progressTaskUiModel.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(16.dp))
                ProgressTaskContent(content = uiState.progressTaskUiModel.memo)
                Spacer(modifier = Modifier.height(16.dp))
                TodayMemoField(
                    onClickSaveTodayMemo = onClickSaveTodayMemo,
                    savedTodayMemo = uiState.progressTaskUiModel.todayMemo
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.progressTaskUiModel.getRemainTimeString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.progressTaskUiModel.isProgressing) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onClickStop() }
                    ) {
                        Text(text = "STOP", color = Color.White)
                    }
                } else {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onClickStart() }
                    ) {
                        Text(text = "START", color = Color.White)
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
            .background(Color.Black.copy(alpha = 0.05f))
            .padding(8.dp)
            .fillMaxWidth()
            .height(200.dp),
        value = todayMemo,
        onValueChange = {
            Log.d("DetailScreen", "onValueChange : $it")
            todayMemo = it
        },

        )

}