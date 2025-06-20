package com.oldogz.applinkalarm.feature.alarm.open

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.applinkalarm.feature.alarm.component.AppIconImage
import com.oldogz.applinkalarm.feature.alarm.model.OpenAppUiState
import com.oldogz.core.alarm.AppLinkAlarmPlayingService
import com.oldogz.core.designsystem.component.AppLinkAlarmButton
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
                title = stringResource(R.string.feature_alarm_top_app_bar_open_alarm),
                navigationIcon = {
                    AppLinkAlarmIconButton(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.feature_alarm_icon_description_close),
                        onClick = {
                            if (openAppUiState.alarmMode == AlarmMode.STANDARD) {
                                alarmStop(context)
                            }
                            popBackStack()
                        }
                    )
                },
                actions = {}
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                OpenAppInfo(
                    onShowErrorSnackBar = onShowErrorSnackBar,
                    popBackStack = popBackStack,
                    alarmName = openAppUiState.alarmName,
                    alarmMessage = openAppUiState.alarmMessage,
                    hour = openAppUiState.hour,
                    minute = openAppUiState.minute,
                    alarmMode = openAppUiState.alarmMode,
                    periodOfDay = openAppUiState.periodOfDay,
                    linkedAppPackage = openAppUiState.linkedAppPackage
                )
            }
        }
    }
}

@Composable
private fun OpenAppInfo(
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    popBackStack: () -> Unit,
    alarmName: String,
    alarmMessage: String,
    hour: Int,
    minute: Int,
    alarmMode: AlarmMode,
    periodOfDay: PeriodOfDay,
    linkedAppPackage: String,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(horizontal = Paddings.xlarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppIconImage(
            linkedAppPackage = linkedAppPackage,
            size = 64.dp
        )

        val hourText = hour.toString().padStart(2, '0')
        val minuteText = minute.toString().padStart(2, '0')
        Text(
            modifier = Modifier.padding(vertical = Paddings.large),
            text = "$hourText:$minuteText $periodOfDay",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = alarmName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            modifier = Modifier.padding(vertical = Paddings.large),
            text = alarmMessage,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        AppLinkAlarmButton(
            modifier = Modifier
                .fillMaxWidth(),
            content = when (alarmMode) {
                AlarmMode.STANDARD -> stringResource(R.string.feature_alarm_text_dismiss_and_open_app)
                AlarmMode.ONLY_NOTIFICATION -> stringResource(R.string.feature_alarm_text_open_app)
            },
            onClick = {
                if (alarmMode == AlarmMode.STANDARD) {
                    alarmStop(context)
                }
                val launchIntent =
                    context.packageManager.getLaunchIntentForPackage(linkedAppPackage)
                if (launchIntent != null) {
                    context.startActivity(launchIntent)
                } else {
                    onShowErrorSnackBar(
                        Throwable(
                            context.getString(
                                R.string.feature_alarm_error_text_app_not_found,
                                linkedAppPackage
                            )
                        )
                    )
                }
                popBackStack()
            }
        )
    }
}

private fun alarmStop(context: Context) {
    val alarmStopIntent =
        Intent(context, AppLinkAlarmPlayingService::class.java).apply {
            action =
                AppLinkAlarmPlayingService.INTENT_ACTION_SERVICE_APP_LINK_ALARM_OFF
        }
    context.startService(alarmStopIntent)
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
                periodOfDay = PeriodOfDay.AM,
                linkedAppPackage = "com.example.app"
            ),
            popBackStack = {},
            onShowErrorSnackBar = {}
        )
    }
}