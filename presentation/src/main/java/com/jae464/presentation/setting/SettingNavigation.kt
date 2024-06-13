package com.jae464.presentation.setting

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jae464.presentation.navigation.TopLevelDestination
import com.jae464.presentation.navigation.getSlideEnterTransition
import com.jae464.presentation.navigation.getSlideExitTransition


const val SETTING_ROUTE = "setting"
const val themeSettingRoute = "theme_setting"
const val categorySettingRoute = "category_setting"

fun NavController.navigateToSetting(navOptions: NavOptions? = null) {
    this.navigate(SETTING_ROUTE, navOptions)
}

fun NavController.navigateToThemeSetting(navOptions: NavOptions? = null) {
    this.navigate(themeSettingRoute, navOptions)
}

fun NavController.navigateToCategorySetting(navOptions: NavOptions? = null) {
    this.navigate(categorySettingRoute, navOptions = null)
}

fun NavGraphBuilder.settingScreen(
    onClickTestScreen: () -> Unit = {},
    onClickPreference: (String) -> Unit = {}
) {
    composable(
        route = SETTING_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                TopLevelDestination.Home.route, TopLevelDestination.TaskList.route, TopLevelDestination.Statistic.route -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }

                else -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
            }
        },
        exitTransition = {
            when(targetState.destination.route) {
                TopLevelDestination.Home.route, TopLevelDestination.TaskList.route, TopLevelDestination.Statistic.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                else -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
            }
        },
        popExitTransition = {
            when (targetState.destination.route) {
                TopLevelDestination.Home.route, TopLevelDestination.TaskList.route, TopLevelDestination.Statistic.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                else -> {
                    null
                }
            }
        }
    ) {
        SettingScreen(
            onClickTestScreen = onClickTestScreen,
            onClickPreference = onClickPreference
        )
    }
}

fun NavGraphBuilder.themeSettingScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = themeSettingRoute,
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
        ThemeSettingScreen(onBackClick = onBackClick)
    }
}

fun NavGraphBuilder.categorySettingScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = categorySettingRoute,
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
        CategorySettingScreen(onBackClick = onBackClick)
    }
}