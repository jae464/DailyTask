package com.jae464.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.RoundedCorner
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.yml.charts.common.extensions.isNotNull
import com.jae464.presentation.detail.navigateToDetail
import com.jae464.presentation.home.HomeScreen
import com.jae464.presentation.navigation.DailyTaskNavHost
import com.jae464.presentation.navigation.TopLevelDestination
import com.jae464.presentation.navigation.isTopLevelDestinationInHierarchy
import com.jae464.presentation.navigation.navigateToTopLevelDestination
import com.jae464.presentation.setting.SettingScreen
import com.jae464.presentation.statistic.StatisticScreen
import com.jae464.presentation.tasks.AddTaskScreen
import com.jae464.presentation.tasks.TaskListScreen
import com.jae464.presentation.tasks.addTaskScreenRoute
import com.jae464.presentation.ui.DailyTaskAppState
import com.jae464.presentation.ui.rememberDailyTaskAppState
import com.jae464.presentation.ui.theme.DailyTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val deniedPermissions = permissions.filter { !it.value }.map { it.key }

            if (deniedPermissions.isNotEmpty()) {
                Log.d(TAG, deniedPermissions.toString())
            } else {
                Log.d(TAG, "모든 권한이 허용")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
        }

        setContent {
            DailyTaskTheme {
                // A surface container using the 'background' color from the theme
                val appState = rememberDailyTaskAppState()
                val navController = appState.navController
                val currentDest = appState.currentDestination
                val isShowBottomNavigation = TopLevelDestination.values().map { it.route }.contains(
                    appState.currentDestination?.route
                )

//                var currentDestination by rememberSaveable { mutableStateOf(navController.currentDestination) }
//                val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
//                    Log.d(TAG, destination.route.toString())
//                    currentDestination = destination
//                }
//                navController.addOnDestinationChangedListener(listener)

                Scaffold(
                    containerColor = Color.Transparent,
                    bottomBar = {
                        if (isShowBottomNavigation) {
                            BottomNavBar(navController = navController, currentDest = currentDest)
                        }
                    }
                ) { padding ->
                    Row(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                    ) {
                        DailyTaskNavHost(navController = navController)

                        // Notification 클릭해서 들어온 경우
                        val progressTaskId = intent.getStringExtra("progressTaskId") ?: ""
                        if (progressTaskId.isNotEmpty()) {
                            navController.navigateToDetail(progressTaskId)
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun BottomNavBar(navController: NavHostController, currentDest: NavDestination?) {
    val topDestinations = TopLevelDestination.values().asList()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Log.d("MainActivity", "BottomNavBar currentDest : ${currentDest}")
    NavigationBar(
        tonalElevation = 16.dp,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        topDestinations.forEach { destination ->
            NavigationBarItem(
//                selected = navBackStackEntry?.destination?.route == destination.route,
                selected = currentDest.isTopLevelDestinationInHierarchy(destination),
                onClick = {
                    navController.navigateToTopLevelDestination(destination)
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

//            val selected = currentRoute.isTopLevelDestinationInHierarchy(destination)

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Home.route) {
        composable(Routes.Home.route) {
            HomeScreen()
        }
        composable(
            route = Routes.TaskList.route
        ) {
            TaskListScreen(
                onClickAddTask = {
                    navController.navigate(addTaskScreenRoute) {
                        launchSingleTop = true
                    }
                },
                onClickTask = { taskId ->
                    navController.navigate("$addTaskScreenRoute?taskId=$taskId") {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.Statistic.route) {
            StatisticScreen()
        }
        composable(Routes.Setting.route) {
            SettingScreen()
        }
        composable(
            route = "$addTaskScreenRoute?taskId={taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                    defaultValue = ""
                })
        ) {
            AddTaskScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

enum class Routes(val icon: ImageVector, val route: String) {
    Home(Icons.Rounded.Home, "home"),
    TaskList(Icons.Rounded.List, "task_list"),
    Statistic(Icons.Rounded.Info, "statistic"),
    Setting(Icons.Rounded.Settings, "setting"),
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DailyTaskTheme {
        HomeScreen()
    }
}