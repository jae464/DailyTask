package com.jae464.presentation.setting

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jae464.domain.model.Category
import com.jae464.presentation.tasks.AddCategoryDialog
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CategorySettingScreen(
    onBackClick: () -> Unit,
    viewModel: CategorySettingViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showCategoryAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val event = viewModel.event

    LaunchedEffect(event) {
        event.collectLatest {
            when (it) {
                CategorySettingEvent.DuplicateCategoryName -> {
                    Toast.makeText(context, "중복된 이름입니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            )
            .fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            CategorySettingTopAppBar(onBackClick = onBackClick, onClickAddCategory = {
                showCategoryAddDialog = true
            })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            CategoryList(categoryUiState = uiState)

            if (showCategoryAddDialog) {
                // AddTaskScreen에 있는 AddCategoryDialog
                AddCategoryDialog(
                    onSaveCategory = viewModel::addCategory,
                    onChangedShowDialog = {
                        showCategoryAddDialog = it
                    }
                )
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
            LazyColumn {
                items(
                    categoryUiState.categories,
                    key = { it.id }
                ) {
                    CategoryItem(category = it)
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: Category
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = category.name)
        Row {
            IconButton(onClick = {  }) {
                Icon(imageVector = Icons.Rounded.Edit,
                    contentDescription = "edit_category",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            IconButton(onClick = {  }) {
                Icon(imageVector = Icons.Rounded.Delete,
                    contentDescription = "delete_category",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySettingTopAppBar(
    onBackClick: () -> Unit,
    onClickAddCategory: () -> Unit
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
        actions = {
            IconButton(onClick = onClickAddCategory) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add_category")
            }
        }

        )
}

