package com.jae464.presentation.detail

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.jae464.presentation.navigation.getSlideEnterTransition
import com.jae464.presentation.navigation.getSlideExitTransition

const val DETAIL_ROUTE = "detail"
const val PROGRESS_TASK_ID = "progressTaskId"
const val DEEP_LINK_URI_PATTERN = "https://www.jae464.com"
const val DETAIL_DEEP_LINK_URI_PATTERN = "$DEEP_LINK_URI_PATTERN/$DETAIL_ROUTE/{progressTaskId}"

fun NavController.navigateToDetail(progressTaskId: String, navOptions: NavOptions? = null) {
    this.navigate("$DETAIL_ROUTE/$progressTaskId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.detailScreen(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable(
        route = "$DETAIL_ROUTE/{$PROGRESS_TASK_ID}",
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DETAIL_DEEP_LINK_URI_PATTERN
            }
        ),
        arguments = listOf(
            navArgument(PROGRESS_TASK_ID) {
                type = NavType.StringType
                defaultValue = ""
            }
        ),
        enterTransition = {
              getSlideEnterTransition(AnimatedContentTransitionScope.SlideDirection.Start)
        },
        exitTransition = {
            getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.End)
        },
        popExitTransition = {
            getSlideExitTransition(AnimatedContentTransitionScope.SlideDirection.End)
        }
    ) {
        DetailScreen(
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar
        )
    }
}