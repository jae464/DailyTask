package com.jae464.presentation.detail

import android.util.Log
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
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val detailRoute = "detail"

fun NavController.navigateToDetail(progressTaskId: String, navOptions: NavOptions? = null) {
    Log.d("DetailNavigation", "navigateToDetail")
    this.navigate("$detailRoute/$progressTaskId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.detailScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = "$detailRoute/{progressTaskId}",
        arguments = listOf(
            navArgument("progressTaskId") {
                type = NavType.StringType
                defaultValue = ""
            }
        )
    ) {
        DetailScreen(
            onBackClick = onBackClick
        )
    }
}