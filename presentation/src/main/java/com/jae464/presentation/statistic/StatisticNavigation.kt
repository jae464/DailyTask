package com.jae464.presentation.statistic

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jae464.presentation.navigation.TopLevelDestination
import com.jae464.presentation.navigation.getSlideEnterTransition
import com.jae464.presentation.navigation.getSlideExitTransition

const val STATISTIC_ROUTE = "statistic"

fun NavController.navigateToStatistic(navOptions: NavOptions? = null) {
    this.navigate(STATISTIC_ROUTE, navOptions)
}

fun NavGraphBuilder.statisticScreen(
    onClickProgressTask: (StatisticDetailNavigationArgument) -> Unit
) {
    composable(
        route = STATISTIC_ROUTE,
        enterTransition = {
            when(initialState.destination.route) {
                TopLevelDestination.Home.route, TopLevelDestination.TaskList.route -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
                TopLevelDestination.Setting.route -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                else -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
            }
        },
        exitTransition = {
            when(targetState.destination.route) {
                TopLevelDestination.Home.route, TopLevelDestination.TaskList.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                TopLevelDestination.Setting.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
                else -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
            }
        },
        popExitTransition = {
            when(targetState.destination.route) {
                TopLevelDestination.Home.route, TopLevelDestination.TaskList.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                TopLevelDestination.Setting.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
                else -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
            }
        }
    ) {
        StatisticScreen(
            onClickProgressTask = onClickProgressTask
        )
    }
}