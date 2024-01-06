package com.jae464.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.jae464.presentation.detail.detailScreen
import com.jae464.presentation.detail.navigateToDetail
import com.jae464.presentation.home.homeRoute
import com.jae464.presentation.home.homeScreen
import com.jae464.presentation.setting.settingScreen
import com.jae464.presentation.statistic.navigateToStatisticDetail
import com.jae464.presentation.statistic.statisticDetailScreen
import com.jae464.presentation.statistic.statisticScreen
import com.jae464.presentation.tasks.addTaskScreen
import com.jae464.presentation.tasks.navigateToAddTask
import com.jae464.presentation.tasks.taskListScreen
import com.jae464.presentation.ui.DailyTaskAppState

@Composable
fun DailyTaskNavHost(
    appState: DailyTaskAppState,
    startDestination: String = homeRoute
) {

    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        homeScreen(
            appState = appState,
            onClickItem = {
                navController.navigateToDetail(progressTaskId = it)
            }
        )
        taskListScreen(
            onClickAddTask = {
                navController.navigateToAddTask()
            },
            onClickTask = { taskId ->
                navController.navigateToAddTask(taskId)
            }
        )
        addTaskScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
        statisticScreen(
            onClickProgressTask = {
                navController.navigateToStatisticDetail(it)
            }
        )
        settingScreen()
        detailScreen(
            onBackClick = {
                navController.navigateUp()
            }
        )
        statisticDetailScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}