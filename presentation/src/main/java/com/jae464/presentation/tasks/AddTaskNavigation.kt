package com.jae464.presentation.tasks

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jae464.presentation.navigation.getSlideEnterTransition
import com.jae464.presentation.navigation.getSlideExitTransition

const val ADD_TASK_ROUTE = "add_task"

fun NavController.navigateToAddTask(taskId: String? = null, navOptions: NavOptions? = null) {
    if (taskId == null) {
        this.navigate(ADD_TASK_ROUTE) {
            launchSingleTop = true
        }
    }
    else {
        this.navigate("$ADD_TASK_ROUTE?taskId=$taskId") {
            launchSingleTop = true
        }
    }
}

fun NavGraphBuilder.addTaskScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onBackClick: () -> Unit
) {
    composable(
        route = "$ADD_TASK_ROUTE?taskId={taskId}",
        arguments = listOf(
            navArgument("taskId") {
                type = NavType.StringType
                defaultValue = ""
            }),
        enterTransition = {
            getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.Start)
        },
        exitTransition = {
            getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.End)
        },
        popExitTransition = {
            getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.End)
        }
    ) {
        AddTaskScreen(
            onBackClick = onBackClick,
            onShowSnackbar =  onShowSnackbar
        )
    }
}