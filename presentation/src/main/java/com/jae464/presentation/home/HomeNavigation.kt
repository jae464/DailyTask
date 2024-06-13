package com.jae464.presentation.home

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jae464.presentation.navigation.TopLevelDestination
import com.jae464.presentation.navigation.getSlideEnterTransition
import com.jae464.presentation.navigation.getSlideExitTransition
import com.jae464.presentation.ui.DailyTaskAppState

const val homeRoute = "home"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(homeRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    appState: DailyTaskAppState,
    onClickItem: (String) -> Unit = {}
) {
    composable(
        route = homeRoute,
        enterTransition = {
            when(initialState.destination.route) {
                TopLevelDestination.TaskList.route, TopLevelDestination.Statistic.route, TopLevelDestination.Setting.route -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                else -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
            }
        },
        exitTransition = {
            when(targetState.destination.route) {
                TopLevelDestination.TaskList.route, TopLevelDestination.Statistic.route, TopLevelDestination.Setting.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
                else -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
            }
        },
        popExitTransition = {
            when(targetState.destination.route) {
                TopLevelDestination.TaskList.route, TopLevelDestination.Statistic.route, TopLevelDestination.Setting.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
                else -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
            }
        }
    ) {
        HomeScreen(
            appState = appState,
            onClickItem = onClickItem
        )
    }
}