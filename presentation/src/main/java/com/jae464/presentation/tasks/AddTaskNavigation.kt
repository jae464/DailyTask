package com.jae464.presentation.tasks

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val addTaskRoute = "add_task"

fun NavController.navigateToAddTask(taskId: String? = null, navOptions: NavOptions? = null) {
    if (taskId == null) {
        this.navigate(addTaskRoute) {
            launchSingleTop = true
        }
    }
    else {
        this.navigate("$addTaskRoute?taskId=$taskId") {
            launchSingleTop = true
        }
    }
}

fun NavGraphBuilder.addTaskScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = "$addTaskScreenRoute?taskId={taskId}",
        arguments = listOf(
            navArgument("taskId") {
                type = NavType.StringType
                defaultValue = ""
            })
    ) {
        AddTaskScreen(
            onBackClick = onBackClick
        )
    }
}