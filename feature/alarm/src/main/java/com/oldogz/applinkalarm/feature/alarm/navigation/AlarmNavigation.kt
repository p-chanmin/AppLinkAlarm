package com.oldogz.applinkalarm.feature.alarm.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.oldogz.applinkalarm.feature.alarm.edit.AlarmEditScreen
import com.oldogz.applinkalarm.feature.alarm.home.AlarmHomeScreen
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
    popBackStack: () -> Unit,
) {
    composable<Route.AlarmHome> {
        AlarmHomeScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToAlarmEdit = navigateToAlarmEdit
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
}