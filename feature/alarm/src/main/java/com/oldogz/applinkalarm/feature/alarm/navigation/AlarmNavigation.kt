package com.oldogz.applinkalarm.feature.alarm.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.oldogz.core.navigation.Route
import com.oldogz.applinkalarm.feature.alarm.AlarmHomeScreen

fun NavController.navigationToHome() {
    navigate(Route.AlarmHome)
}

fun NavGraphBuilder.alarmNavGraph(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    composable<Route.AlarmHome> {
        AlarmHomeScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
        )
    }
}