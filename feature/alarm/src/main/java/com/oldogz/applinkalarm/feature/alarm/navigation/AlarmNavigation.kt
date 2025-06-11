package com.oldogz.applinkalarm.feature.alarm.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.oldogz.core.navigation.Route
import com.oldogz.applinkalarm.feature.alarm.edit.AlarmEditScreen
import com.oldogz.applinkalarm.feature.alarm.home.AlarmHomeScreen

fun NavController.navigationToHome() {
    navigate(Route.AlarmHome)
}

fun NavController.navigationToAlarmEdit() {
    navigate(Route.AlarmEdit)
}

fun NavGraphBuilder.alarmNavGraph(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToAlarmEdit: () -> Unit,
    popBackStack: () -> Unit,
) {
    composable<Route.AlarmHome> {
        AlarmHomeScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToAlarmEdit = navigateToAlarmEdit
        )
    }

    composable<Route.AlarmEdit> {
        AlarmEditScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            popBackStack = popBackStack
        )
    }
}