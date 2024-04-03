package com.jae464.presentation.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.material.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jae464.domain.model.Task

private const val DETAIL_PANE_NAVHOST_ROUTE = "detail_pane_route"

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun TaskListDetailScreen(
    onTaskClick: (Task) -> Unit = {}
) {

    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    val nestedNavController = rememberNavController()

    BackHandler(listDetailNavigator.canNavigateBack()) {
        listDetailNavigator.navigateBack()
    }

    fun onTaskClick(task: Task) {
        nestedNavController.navigateToAddTask(task.id) {
            popUpTo(DETAIL_PANE_NAVHOST_ROUTE)
        }
        listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
    }

    ListDetailPaneScaffold(
        value = listDetailNavigator.scaffoldValue,
        directive = listDetailNavigator.scaffoldDirective,
        listPane = {
            TaskListScreen(
                onClickAddTask = {
                    nestedNavController.navigateToAddTask() {
                        popUpTo(DETAIL_PANE_NAVHOST_ROUTE)
                    }
                    listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                },
                onClickTask = ::onTaskClick
            )
        },
        detailPane = {
            NavHost(
                navController = nestedNavController,
                startDestination = "empty_screen",
                route = DETAIL_PANE_NAVHOST_ROUTE
            ) {
                addTaskScreen(onBackClick = listDetailNavigator::navigateBack)
                composable(
                    route = "empty_screen"
                ) {
                    EmptyScreen()
                }
            }
        }
    )
}

@Composable
fun EmptyScreen() {
    Text(text = "empty")
}

