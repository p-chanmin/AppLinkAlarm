package com.oldogz.applinkalarm.feature.setting.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.oldogz.applinkalarm.feature.setting.home.SettingScreen
import com.oldogz.applinkalarm.feature.setting.opensource.OpenSourceScreen
import com.oldogz.core.navigation.Route

fun NavController.navigationToSetting() {
    navigate(Route.Setting)
}

fun NavController.navigationToOpenSource() {
    navigate(Route.OpenSource)
}

fun NavGraphBuilder.settingNavGraph(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToOpenSource: () -> Unit,
    popBackStack: () -> Unit,
) {
    composable<Route.Setting>(
        enterTransition = {
            slideInHorizontally { fullWidth -> -fullWidth }
        },
        exitTransition = {
            slideOutHorizontally { fullWidth -> -fullWidth }
        },
    ) {
        SettingScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToOpenSource = navigateToOpenSource,
            popBackStack = popBackStack
        )
    }

    composable<Route.OpenSource>(
        enterTransition = {
            slideInVertically { fullHeight -> fullHeight }
        },
        exitTransition = {
            slideOutVertically { fullHeight -> fullHeight }
        }
    ) {
        OpenSourceScreen(
            paddingValues = paddingValues,
            popBackStack = popBackStack
        )
    }
}