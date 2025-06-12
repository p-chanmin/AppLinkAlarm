package com.oldogz.applinkalarm.feature.setting

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oldogz.applinkalarm.feature.setting.model.SettingUiState
import com.oldogz.core.designsystem.component.AppLinkAlarmIconButton
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@Composable
internal fun SettingScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    popBackStack: () -> Unit,
    settingViewModel: SettingViewModel = hiltViewModel(),
) {

    val settingUiState by settingViewModel.settingUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        settingViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    SettingContent(
        settingUiState = settingUiState,
        paddingValues = paddingValues,
        popBackStack = popBackStack,
    )
}

@Composable
private fun SettingContent(
    settingUiState: SettingUiState,
    paddingValues: PaddingValues,
    popBackStack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppLinkAlarmTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = "Settings",
                navigationIcon = {
                    AppLinkAlarmIconButton(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        onClick = popBackStack
                    )
                },
                actions = {}
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingContentPreview() {
    AppLinkAlarmTheme {
        SettingContent(
            settingUiState = SettingUiState(),
            paddingValues = PaddingValues(),
            popBackStack = {}
        )
    }
}