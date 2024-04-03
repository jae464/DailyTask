package com.jae464.presentation.tasks

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val taskListDetailRoute = "task_list_detail"

fun NavController.navigateToTaskListDetail(navOptions: NavOptions? = null) {
    this.navigate(taskListDetailRoute, navOptions)
}

fun NavGraphBuilder.taskListDetailScreen() {
    composable(
        route = taskListDetailRoute
    ) {
        TaskListDetailScreen()
    }
}