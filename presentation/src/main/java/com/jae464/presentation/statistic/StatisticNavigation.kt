package com.jae464.presentation.statistic

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val statisticRoute = "statistic"

fun NavController.navigateToStatistic(navOptions: NavOptions? = null) {
    this.navigate(statisticRoute, navOptions)
}

fun NavGraphBuilder.statisticScreen() {
    composable(
        route = statisticRoute
    ) {
        StatisticScreen()
    }
}