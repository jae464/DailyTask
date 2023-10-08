package com.jae464.presentation.tasks

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jae464.presentation.home.HomeScreen
import com.jae464.presentation.home.homeRoute

const val taskListRoute = "task_list"

fun NavController.navigateToTaskList(navOptions: NavOptions? = null) {
    this.navigate(taskListRoute, navOptions)
}

fun NavGraphBuilder.taskListScreen(
    onClickAddTask: () -> Unit,
    onClickTask: (String) -> Unit
) {
    composable(
        route = taskListRoute
    ) {
        TaskListScreen(
            onClickAddTask = onClickAddTask, onClickTask = onClickTask
        )
    }
}