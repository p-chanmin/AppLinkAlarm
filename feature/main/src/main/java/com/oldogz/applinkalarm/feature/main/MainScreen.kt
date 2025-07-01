package com.oldogz.applinkalarm.feature.main

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oldogz.applinkalarm.feature.main.inappupdate.InAppUpdate
import com.oldogz.applinkalarm.feature.main.model.MainUiState
import com.oldogz.core.designsystem.theme.BrunswickGreen
import com.oldogz.core.designsystem.theme.Emerald
import com.oldogz.core.designsystem.theme.MintCream
import kotlinx.coroutines.launch

@Composable
internal fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val mainUiState by mainViewModel.mainUiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val onShowErrorSnackBar: (throwable: Throwable?) -> Unit = { throwable ->
        coroutineScope.launch {
            val unknownErrorMessage = context.getString(R.string.feature_main_error_message_unknown)

            snackBarHostState.showSnackbar(throwable?.message ?: unknownErrorMessage)
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    MainScreenContent(
        mainUiState = mainUiState,
        navigator = navigator,
        onShowErrorSnackBar = onShowErrorSnackBar,
        snackBarHostState = snackBarHostState,
        setRejectFlexibleUpdateDate = mainViewModel::setRejectFlexibleUpdateDate,
    )
}

@Composable
private fun MainScreenContent(
    modifier: Modifier = Modifier,
    mainUiState: MainUiState,
    navigator: MainNavigator,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    snackBarHostState: SnackbarHostState,
    setRejectFlexibleUpdateDate: () -> Unit,
) {

    val flexibleSnackBarHostState = remember { SnackbarHostState() }

    InAppUpdate(
        snackBarHostState = flexibleSnackBarHostState,
        rejectFlexibleUpdateDate = mainUiState.rejectFlexibleUpdateDate,
        setRejectFlexibleUpdateDate = setRejectFlexibleUpdateDate
    )

    Scaffold(
        modifier = modifier,
        content = { paddingValues ->
            MainNavHost(
                navigator = navigator,
                paddingValues = paddingValues,
                onShowErrorSnackBar = onShowErrorSnackBar,
            )
        },
        bottomBar = {},
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    contentColor = MintCream,
                    containerColor = BrunswickGreen,
                    actionColor = Emerald
                )
            }
        }
    )

    SnackbarHost(
        hostState = flexibleSnackBarHostState,
        modifier = Modifier.systemBarsPadding(),
    ) { data ->
        Snackbar(
            snackbarData = data,
            contentColor = MintCream,
            containerColor = BrunswickGreen,
            actionColor = Emerald
        )
    }
}