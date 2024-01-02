package com.jae464.presentation.home

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
import androidx.navigation.compose.composable

const val homeRoute = "home"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    Log.d("HomeNavigation", "navigateToHome")
    this.navigate(homeRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onClickItem: (String) -> Unit = {}
) {
    composable(
        route = homeRoute,
    ) {
        HomeScreen(
            onClickItem = onClickItem
        )
    }
}