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
import com.jae464.presentation.navigation.TopLevelDestination
import com.jae464.presentation.navigation.getSlideEnterTransition
import com.jae464.presentation.navigation.getSlideExitTransition

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
        enterTransition = {
            when(initialState.destination.route) {
                TopLevelDestination.Home.route -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
                TopLevelDestination.Statistic.route, TopLevelDestination.Setting.route -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                else -> {null}
            }
        },
        popExitTransition = {
            when(targetState.destination.route) {
                TopLevelDestination.Home.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                TopLevelDestination.Statistic.route, TopLevelDestination.Setting.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
                else -> {null}
            }
        }
    ) {
        TaskListScreen(
            onClickAddTask = onClickAddTask, onClickTask = onClickTask
        )
    }
}