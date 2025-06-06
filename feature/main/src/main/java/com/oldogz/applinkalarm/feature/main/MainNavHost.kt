package com.oldogz.applinkalarm.feature.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.oldogz.applinkalarm.feature.home.navigation.homeNavGraph

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
        homeNavGraph(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
        )
    }
}