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
import androidx.navigation.compose.composable
import com.jae464.domain.model.Task
import com.jae464.presentation.home.HomeScreen
import com.jae464.presentation.home.homeRoute

const val taskListRoute = "task_list"

fun NavController.navigateToTaskList(navOptions: NavOptions? = null) {
    this.navigate(taskListRoute, navOptions)
}

fun NavGraphBuilder.taskListScreen(
    onClickAddTask: () -> Unit,
    onClickTask: (Task) -> Unit
) {
    composable(
        route = taskListRoute,
//        enterTransition = {
//            fadeIn(
//                animationSpec = tween(
//                    300, easing = LinearEasing
//                )
//            ) +
//            slideIntoContainer(
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
        TaskListScreen(
            onClickAddTask = onClickAddTask, onClickTask = onClickTask
        )
    }
}