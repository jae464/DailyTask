package com.jae464.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jae464.presentation.navigation.TopLevelDestination


@Composable
fun rememberDailyTaskAppState(
    navController: NavHostController = rememberNavController()
): DailyTaskAppState {
    return remember(
        navController
    ) {
        DailyTaskAppState(navController)
    }
}
@Stable
class DailyTaskAppState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

//    val currentTopLevelDestination: TopLevelDestination?
//        @Composable get() = when (currentDestination?.route) {
//            TopLevelDestination.Home.route -> FOR_YOU
//            TopLevelDestination.TaskList.route -> BOOKMARKS
//            TopLevelDestination.Statistic.route -> INTERESTS
//            TopLevelDestination.Setting.route ->
//            else -> null
//        }

}