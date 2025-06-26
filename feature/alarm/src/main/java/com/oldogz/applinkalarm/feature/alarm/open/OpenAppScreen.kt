package com.oldogz.applinkalarm.feature.alarm.open

import SmallNativeAd
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.applinkalarm.feature.alarm.component.OpenAppInfo
import com.oldogz.applinkalarm.feature.alarm.model.OpenAppUiState
import com.oldogz.core.designsystem.component.AppLinkAlarmIconButton
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.designsystem.theme.Paddings
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.PeriodOfDay

@Composable
internal fun OpenAppScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    popBackStack: () -> Unit,
    openAppViewModel: OpenAppViewModel = hiltViewModel()
) {
    val openAppUiState by openAppViewModel.openAppUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        openAppViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    OpenAppContent(
        openAppUiState = openAppUiState,
        onShowErrorSnackBar = onShowErrorSnackBar,
        popBackStack = popBackStack,
    )
}

@Composable
private fun OpenAppContent(
    openAppUiState: OpenAppUiState,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    popBackStack: () -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            AppLinkAlarmTopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                title = stringResource(R.string.feature_alarm_top_app_bar_title_default),
                navigationIcon = {
                    AppLinkAlarmIconButton(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.feature_alarm_icon_description_close),
                        onClick = popBackStack
                    )
                },
                actions = {}
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OpenAppInfo(
                    alarmName = openAppUiState.alarmName,
                    alarmMessage = openAppUiState.alarmMessage,
                    hour = openAppUiState.hour,
                    minute = openAppUiState.minute,
                    alarmMode = AlarmMode.NOTIFICATION_ONLY,
                    periodOfDay = openAppUiState.periodOfDay,
                    linkedAppPackage = openAppUiState.linkedAppPackage,
                    onClick = {
                        val launchIntent =
                            context.packageManager.getLaunchIntentForPackage(openAppUiState.linkedAppPackage)
                        if (launchIntent != null) {
                            context.startActivity(launchIntent)
                        } else {
                            onShowErrorSnackBar(
                                Throwable(
                                    context.getString(
                                        R.string.feature_alarm_error_text_app_not_found,
                                        openAppUiState.linkedAppPackage
                                    )
                                )
                            )
                        }
                        popBackStack()
                    }
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Paddings.large)
                        .padding(Paddings.small),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    SmallNativeAd(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OpenAppContentPreview() {
    AppLinkAlarmTheme {
        OpenAppContent(
            openAppUiState = OpenAppUiState(
                alarmName = "Alarm Name",
                alarmMessage = "Alarm Message",
                hour = 12,
                minute = 30,
                alarmMode = AlarmMode.NOTIFICATION_ONLY,
                periodOfDay = PeriodOfDay.AM,
                linkedAppPackage = "com.example.app"
            ),
            popBackStack = {},
            onShowErrorSnackBar = {}
        )
    }
}