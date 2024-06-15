package com.jae464.presentation.tasks

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jae464.domain.model.Task
import com.jae464.presentation.navigation.TopLevelDestination
import com.jae464.presentation.navigation.getSlideEnterTransition
import com.jae464.presentation.navigation.getSlideExitTransition

const val TASK_LIST_ROUTE = "task_list"

fun NavController.navigateToTaskList(navOptions: NavOptions? = null) {
    this.navigate(TASK_LIST_ROUTE, navOptions)
}

fun NavGraphBuilder.taskListScreen(
    onClickAddTask: () -> Unit,
    onClickTask: (Task) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(
        route = TASK_LIST_ROUTE,
        enterTransition = {
            when(initialState.destination.route) {
                TopLevelDestination.Home.route -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
                TopLevelDestination.Statistic.route, TopLevelDestination.Setting.route -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                else -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
            }
        },
        exitTransition = {
            when(targetState.destination.route) {
                TopLevelDestination.Home.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                TopLevelDestination.Statistic.route, TopLevelDestination.Setting.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
                else -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
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
                else -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
            }
        }
    ) {
        TaskListScreen(
            onClickAddTask = onClickAddTask, onClickTask = onClickTask, onShowSnackbar = onShowSnackbar
        )
    }
}