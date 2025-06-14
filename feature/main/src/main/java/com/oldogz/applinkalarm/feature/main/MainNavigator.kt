package com.oldogz.applinkalarm.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.oldogz.applinkalarm.feature.alarm.navigation.navigationToAlarmEdit
import com.oldogz.applinkalarm.feature.alarm.navigation.navigationToHome
import com.oldogz.applinkalarm.feature.setting.navigation.navigationToSetting
import com.oldogz.core.navigation.Route

internal class MainNavigator(
    val navController: NavHostController,
) {
    val startDestination = Route.AlarmHome

    fun navigateToHome() {
        navController.navigationToHome()
    }

    fun navigateToAlarmEdit(id: Int?) {
        navController.navigationToAlarmEdit(id)
    }

    fun navigateToSetting() {
        navController.navigationToSetting()
    }

    fun navigateToOpenApp(id: Int) {
        navController.navigationToAlarmEdit(id)
    }

    private fun popBackStack() {
        navController.popBackStack()
    }

    fun popBackStackIfNotStartDestination() {
        if (!isSameCurrentDestination<Route.AlarmHome>()) {
            popBackStack()
        }
    }

    private inline fun <reified T : Route> isSameCurrentDestination(): Boolean {
        val currentRoute = navController.currentDestination?.route
        return currentRoute == T::class.simpleName
    }
}

@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
