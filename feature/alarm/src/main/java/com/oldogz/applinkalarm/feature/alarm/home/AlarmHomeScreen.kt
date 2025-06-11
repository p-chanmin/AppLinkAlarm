package com.oldogz.applinkalarm.feature.alarm.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oldogz.applinkalarm.feature.alarm.model.AlarmHomeUiState
import com.oldogz.core.designsystem.component.AppLinkAlarmIconButton
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@Composable
internal fun AlarmHomeScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToAlarmEdit: () -> Unit,
    alarmHomeViewModel: AlarmHomeViewModel = hiltViewModel()
) {

    val homeUiState by alarmHomeViewModel.homeUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        alarmHomeViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    AlarmHomeContent(
        homeUiState = homeUiState,
        paddingValues = paddingValues,
        navigateToAlarmEdit = navigateToAlarmEdit,
    )
}

@Composable
private fun AlarmHomeContent(
    homeUiState: AlarmHomeUiState,
    paddingValues: PaddingValues,
    navigateToAlarmEdit: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppLinkAlarmTopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                title = "AppLink Alarms",
                navigationIcon = {
                    AppLinkAlarmIconButton(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        onClick = {}
                    )
                },
                actions = {
                    AppLinkAlarmIconButton(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Alarm",
                        onClick = navigateToAlarmEdit
                    )
                }
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeContentPreview() {
    AppLinkAlarmTheme {
        AlarmHomeContent(
            homeUiState = AlarmHomeUiState(),
            paddingValues = PaddingValues(),
            navigateToAlarmEdit = {}
        )
    }
}