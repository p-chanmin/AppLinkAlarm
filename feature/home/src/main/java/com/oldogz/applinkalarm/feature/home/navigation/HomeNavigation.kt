package com.oldogz.applinkalarm.feature.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.oldogz.core.navigation.Route
import com.oldogz.applinkalarm.feature.home.HomeScreen

fun NavController.navigationToHome() {
    navigate(Route.Home)
}

fun NavGraphBuilder.homeNavGraph(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    composable<Route.Home> {
        HomeScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
        )
    }
}