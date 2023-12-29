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
import com.jae464.presentation.statistic.statisticScreen
import com.jae464.presentation.tasks.addTaskScreen
import com.jae464.presentation.tasks.navigateToAddTask
import com.jae464.presentation.tasks.taskListScreen

@Composable
fun DailyTaskNavHost(
    navController: NavHostController,
    startDestination: String = homeRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        homeScreen(
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
        statisticScreen()
        settingScreen()
        detailScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}