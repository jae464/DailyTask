package com.jae464.presentation.ui

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jae464.presentation.navigation.TopLevelDestination


@Composable
fun rememberDailyTaskAppState(
    intentData: String = "",
    navController: NavHostController = rememberNavController()
): DailyTaskAppState {
    return remember(
        navController
    ) {
        DailyTaskAppState(
            intentData,
            navController
        )
    }
}
@Stable
class DailyTaskAppState(
    private val progressIntentData: String,
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    private var _intentData by mutableStateOf(progressIntentData)
    var intentData: String
        get() = _intentData
        set(value) {
            _intentData = value
        }

}