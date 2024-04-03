package com.jae464.presentation.tasks

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jae464.presentation.navigation.getSlideEnterTransition
import com.jae464.presentation.navigation.getSlideExitTransition

const val addTaskRoute = "add_task"

fun NavController.navigateToAddTask(taskId: String? = null, navOptions: NavOptionsBuilder.() -> Unit = {}) {
    if (taskId == null) {
        this.navigate(addTaskRoute) {
            launchSingleTop = true
            navOptions()
        }
    }
    else {
        this.navigate("$addTaskRoute?taskId=$taskId") {
            launchSingleTop = true
            navOptions()
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
            onBackClick = onBackClick
        )
    }
}