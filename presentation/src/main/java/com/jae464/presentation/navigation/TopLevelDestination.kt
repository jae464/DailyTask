package com.jae464.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
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
//            saveState = true
        }
        launchSingleTop = true
//        restoreState = true
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