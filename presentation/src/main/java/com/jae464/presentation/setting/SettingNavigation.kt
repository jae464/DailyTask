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
//        enterTransition = {
//            fadeIn(
//                animationSpec = tween(
//                    300, easing = LinearEasing
//                )
//            ) +
//                    slideIntoContainer(
//                animationSpec = tween(300, easing = EaseIn),
//                towards = AnimatedContentTransitionScope.SlideDirection.Start)
//        },
//        exitTransition = {
//            fadeOut(
//                animationSpec = tween(
//                    300, easing = LinearEasing
//                )
//            ) +
//            slideOutOfContainer(
//                animationSpec = tween(300, easing = EaseOut),
//                towards = AnimatedContentTransitionScope.SlideDirection.End)
//        }
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