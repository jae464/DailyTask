package com.jae464.presentation.setting

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
import com.jae464.presentation.navigation.TopLevelDestination
import com.jae464.presentation.navigation.getSlideEnterTransition
import com.jae464.presentation.navigation.getSlideExitTransition

const val settingRoute = "setting"
const val themeSettingRoute = "theme_setting"
const val categorySettingRoute = "category_setting"

fun NavController.navigateToSetting(navOptions: NavOptions? = null) {
    this.navigate(settingRoute, navOptions)
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
        route = settingRoute,
        enterTransition = {
            when(initialState.destination.route) {
                TopLevelDestination.Home.route, TopLevelDestination.TaskList.route, TopLevelDestination.Statistic.route -> {
                    getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.Start)
                }
                else -> {null}
            }
        },
        popExitTransition = {
            when(targetState.destination.route) {
                TopLevelDestination.Home.route, TopLevelDestination.TaskList.route, TopLevelDestination.Statistic.route -> {
                    getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.End)
                }
                else -> {null}
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
    ) {
        ThemeSettingScreen(onBackClick = onBackClick)
    }
}

fun NavGraphBuilder.categorySettingScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = categorySettingRoute,
    ) {
        CategorySettingScreen(onBackClick = onBackClick)
    }
}