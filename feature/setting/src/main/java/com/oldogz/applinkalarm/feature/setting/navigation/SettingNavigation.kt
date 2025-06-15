package com.oldogz.applinkalarm.feature.setting.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.oldogz.applinkalarm.feature.setting.SettingScreen
import com.oldogz.core.navigation.Route

fun NavController.navigationToSetting() {
    navigate(Route.Setting)
}

fun NavGraphBuilder.settingNavGraph(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
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
            popBackStack = popBackStack
        )
    }
}