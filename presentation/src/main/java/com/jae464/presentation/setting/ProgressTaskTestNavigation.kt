package com.jae464.presentation.setting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val progressTaskTestRoute = "progressTask_test"
fun NavController.navigateToTest(navOptions: NavOptions? = null) {
    this.navigate(progressTaskTestRoute, navOptions)
}

fun NavGraphBuilder.progressTaskTestScreen(
    onBackClick : () -> Unit
) {
    composable(
        route = progressTaskTestRoute
    ) {
        ProgressTaskTestScreen(
            onBackClick = onBackClick
        )
    }
}