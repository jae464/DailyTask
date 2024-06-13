package com.jae464.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jae464.presentation.navigation.DailyTaskNavHost
import com.jae464.presentation.navigation.TopLevelDestination
import com.jae464.presentation.navigation.isTopLevelDestinationInHierarchy
import com.jae464.presentation.navigation.navigateToTopLevelDestination
import com.jae464.presentation.ui.rememberDailyTaskAppState
import com.jae464.presentation.ui.theme.DailyTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

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
                val appState = rememberDailyTaskAppState()
                val navController = appState.navController
                val currentDest = appState.currentDestination
                val isShowBottomNavigation = TopLevelDestination.values().map { it.route }.contains(
                    appState.currentDestination?.route
                )

                Scaffold(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
                        ),
                    containerColor = Color.Transparent,
                    bottomBar = {
                        if (isShowBottomNavigation) {
                            BottomNavBar(navController = navController, currentDest = currentDest)
                        }
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                    ) {
                        DailyTaskNavHost(appState = appState)
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DailyTaskTheme {
    }
}