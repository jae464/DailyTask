package com.jae464.presentation.detail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val detailRoute = "detail"

fun NavController.navigateToDetail(progressTaskId: String, navOptions: NavOptions? = null) {
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