package com.jae464.presentation.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jae464.domain.model.ProgressTask
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

    Scaffold(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            ),
        topBar = {
            DetailTopAppBar(
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            DetailProgressTask(
                uiState = uiState,
                onClickStart = {viewModel.startProgressTask(context)},
                onClickStop = {viewModel.stopProgressTask()}
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
        }

    )
}

@Composable
fun DetailProgressTask(
    uiState: DetailUiState,
    onClickStart: () -> Unit,
    onClickStop: () -> Unit
) {
    when (uiState) {
        is DetailUiState.Loading -> {

        }

        is DetailUiState.Success -> {
            Column {
                Text(text = uiState.progressTaskUiModel.title)
                Text(text = uiState.progressTaskUiModel.remainTime.toString())
                if (uiState.progressTaskUiModel.isProgressing) {
                    Button(onClick = { onClickStop() }) {
                        Text(text = "STOP")
                    }
                } else {
                    Button(onClick = { onClickStart() }) {
                        Text(text = "START")
                    }
                }
            }
        }
    }
}