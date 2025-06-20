package com.oldogz.applinkalarm.feature.alarm.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.oldogz.applinkalarm.feature.alarm.edit.AlarmEditScreen
import com.oldogz.applinkalarm.feature.alarm.home.AlarmHomeScreen
import com.oldogz.applinkalarm.feature.alarm.open.OpenAppScreen
import com.oldogz.core.navigation.DEEP_LINK_BASE_PATH
import com.oldogz.core.navigation.Route

fun NavController.navigationToHome() {
    navigate(Route.AlarmHome)
}

fun NavController.navigationToAlarmEdit(id: Int?) {
    navigate(Route.AlarmEdit(id))
}

fun NavGraphBuilder.alarmNavGraph(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToAlarmEdit: (Int?) -> Unit,
    navigateToSetting: () -> Unit,
    popBackStack: () -> Unit,
) {
    composable<Route.AlarmHome>(
        deepLinks = listOf(navDeepLink<Route.AlarmHome>(basePath = "$DEEP_LINK_BASE_PATH/home"))
    ) {
        AlarmHomeScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToAlarmEdit = navigateToAlarmEdit,
            navigateToSetting = navigateToSetting,
        )
    }

    composable<Route.AlarmEdit>(
        enterTransition = {
            slideInHorizontally { fullWidth -> fullWidth }
        },
        exitTransition = {
            slideOutHorizontally { fullWidth -> fullWidth }
        },
    ) {
        AlarmEditScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            popBackStack = popBackStack
        )
    }

    composable<Route.OpenApp>(
        deepLinks = listOf(navDeepLink<Route.OpenApp>(basePath = "$DEEP_LINK_BASE_PATH/open"))
    ) {
        OpenAppScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            popBackStack = popBackStack,
        )
    }
}