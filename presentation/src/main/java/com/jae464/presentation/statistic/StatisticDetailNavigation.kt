package com.jae464.presentation.statistic

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jae464.presentation.navigation.getSlideEnterTransition
import com.jae464.presentation.navigation.getSlideExitTransition

const val STATISTIC_DETAIL_ROUTE = "statistic_detail"

fun NavController.navigateToStatisticDetail(args: StatisticDetailNavigationArgument,navOptions: NavOptions? = null) {
    this.navigate("${STATISTIC_DETAIL_ROUTE}?taskId=${args.taskId}&startDate=${args.startDate}&endDate=${args.endDate}", navOptions)
}

fun NavGraphBuilder.statisticDetailScreen(onBackClick: () -> Unit) {
    composable(
        route = "$STATISTIC_DETAIL_ROUTE?taskId={taskId}&startDate={startDate}&endDate={endDate}",
        arguments = listOf(
            navArgument("taskId") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("startDate") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("endDate") {
                type = NavType.StringType
                defaultValue = ""
            }
        ),
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
        StatisticDetailScreen(
            taskId = it.arguments?.getString("taskId") ?: "",
            onBackClick = onBackClick
        )
    }
}

data class StatisticDetailNavigationArgument(
    val taskId: String,
    val startDate: String,
    val endDate: String
)