package com.jae464.presentation.setting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val settingRoute = "setting"

fun NavController.navigateToSetting(navOptions: NavOptions? = null) {
    this.navigate(settingRoute, navOptions)
}

fun NavGraphBuilder.settingScreen() {
    composable(
        route = settingRoute
    ) {
        SettingScreen()
    }
}