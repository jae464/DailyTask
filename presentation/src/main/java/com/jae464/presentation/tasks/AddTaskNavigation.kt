package com.jae464.presentation.tasks

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
            }),
//        enterTransition = {
//            fadeIn(
//                animationSpec = tween(
//                    300, easing = LinearEasing
//                )
//            ) +
//                    slideIntoContainer(
//                animationSpec = tween(300, easing = EaseIn),
//                towards = AnimatedContentTransitionScope.SlideDirection.Start)
//        },
//        exitTransition = {
//            fadeOut(
//                animationSpec = tween(
//                    300, easing = LinearEasing
//                )
//            ) +
//            slideOutOfContainer(
//                animationSpec = tween(300, easing = EaseOut),
//                towards = AnimatedContentTransitionScope.SlideDirection.End)
//        }
    ) {
        AddTaskScreen(
            onBackClick = onBackClick
        )
    }
}