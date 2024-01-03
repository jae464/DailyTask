package com.jae464.presentation.statistic

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

const val statisticRoute = "statistic"

fun NavController.navigateToStatistic(navOptions: NavOptions? = null) {
    this.navigate(statisticRoute, navOptions)
}

fun NavGraphBuilder.statisticScreen(
    onClickProgressTask: (StatisticDetailNavigationArgument) -> Unit
) {
    composable(
        route = statisticRoute,
    ) {
        StatisticScreen(
            onClickProgressTask = onClickProgressTask
        )
    }
}