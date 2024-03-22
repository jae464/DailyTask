package com.jae464.presentation.statistic

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jae464.presentation.navigation.TopLevelDestination
import com.jae464.presentation.navigation.getSlideEnterTransition
import com.jae464.presentation.navigation.getSlideExitTransition

const val statisticRoute = "statistic"

fun NavController.navigateToStatistic(navOptions: NavOptions? = null) {
    this.navigate(statisticRoute, navOptions)
}

fun NavGraphBuilder.statisticScreen(
    onClickProgressTask: (StatisticDetailNavigationArgument) -> Unit
) {
    composable(
        route = statisticRoute,
        enterTransition = {
            when(initialState.destination.route) {
                TopLevelDestination.Home.route, TopLevelDestination.TaskList.route -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
                TopLevelDestination.Setting.route -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                else -> {null}
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
                else -> {null}
            }
        }
    ) {
        StatisticScreen(
            onClickProgressTask = onClickProgressTask
        )
    }
}