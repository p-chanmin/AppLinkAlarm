package com.oldogz.applinkalarm.feature.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.oldogz.applinkalarm.feature.alarm.navigation.alarmNavGraph
import com.oldogz.applinkalarm.feature.setting.navigation.settingNavGraph

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        alarmNavGraph(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToAlarmEdit = navigator::navigateToAlarmEdit,
            navigateToSetting = navigator::navigateToSetting,
            popBackStack = navigator::popBackStackIfNotStartDestination,
        )

        settingNavGraph(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            popBackStack = navigator::popBackStackIfNotStartDestination,
        )
    }
}