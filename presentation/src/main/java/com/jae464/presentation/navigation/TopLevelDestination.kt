package com.jae464.presentation.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions
import com.jae464.presentation.home.navigateToHome
import com.jae464.presentation.setting.navigateToSetting
import com.jae464.presentation.statistic.navigateToStatistic
import com.jae464.presentation.tasks.navigateToTaskList

enum class TopLevelDestination(val icon: ImageVector, val route: String) {
    Home(Icons.Rounded.Home, "home"),
    TaskList(Icons.Rounded.List, "task_list"),
    Statistic(Icons.Rounded.Info, "statistic"),
    Setting(Icons.Rounded.Settings, "setting"),
}


fun NavController.navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
    val topLevelNavOptions = navOptions {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
    when (topLevelDestination) {
        TopLevelDestination.Home -> {
            navigateToHome(navOptions = topLevelNavOptions)
        }

        TopLevelDestination.TaskList -> {
            navigateToTaskList(navOptions = topLevelNavOptions)
        }

        TopLevelDestination.Statistic -> {
            navigateToStatistic(navOptions = topLevelNavOptions)
        }

        TopLevelDestination.Setting -> {
            navigateToSetting(navOptions = topLevelNavOptions)
        }
    }
}

fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination): Boolean {
    return this?.hierarchy?.any {
        Log.d("TopLevelDestination", it.toString())
        it.route?.contains(destination.route, true) ?: false
    } ?: false
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.getSlideEnterTransition(direction: AnimatedContentTransitionScope.SlideDirection): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            100, easing = LinearEasing
        )
    ) +
            slideIntoContainer(
                animationSpec = tween(200, easing = EaseIn),
                towards = direction
            )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.getSlideExitTransition(direction: AnimatedContentTransitionScope.SlideDirection): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            100, easing = LinearEasing
        )
    ) +
            slideOutOfContainer(
                animationSpec = tween(200, easing = EaseOut),
                towards = direction
            )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.getSlideInHorizontally(direction: Int): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(200, easing = EaseIn),
        initialOffsetX = { fullWidth ->  fullWidth * direction }
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.getSlideOutHorizontally(direction: Int): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(200, easing = EaseOut),
        targetOffsetX = { fullWidth ->  fullWidth * direction }
    )
}


