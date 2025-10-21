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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.applinkalarm.feature.alarm.component.OpenAppInfo
import com.oldogz.applinkalarm.feature.alarm.model.OpenAppUiState
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.designsystem.theme.Paddings
import com.oldogz.core.firebase.LocalFirebaseManager
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.LinkTarget
import com.oldogz.core.model.PeriodOfDay

@Composable
fun DismissAlarmScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    dismissAlarm: (LinkTarget) -> Unit,
    dismissAlarmViewModel: DismissAlarmViewModel = hiltViewModel()
) {
    val firebaseManager = LocalFirebaseManager.current
    val configuration = LocalConfiguration.current

    val openAppUiState by dismissAlarmViewModel.openAppUiState.collectAsStateWithLifecycle()
    val hasPremium by dismissAlarmViewModel.hasPremium.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        firebaseManager.screenLogEvent("DismissAlarmScreen", configuration.orientation)
        dismissAlarmViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    DismissAlarmContent(
        openAppUiState = openAppUiState,
        hasPremium = hasPremium,
        onShowErrorSnackBar = onShowErrorSnackBar,
        dismissAlarm = dismissAlarm
    )
}

@Composable
private fun DismissAlarmContent(
    openAppUiState: OpenAppUiState,
    hasPremium: Boolean?,
    dismissAlarm: (LinkTarget) -> Unit,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
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
                title = stringResource(R.string.feature_alarm_top_app_bar_dismiss_alarm),
                navigationIcon = {},
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
                    alarmMode = AlarmMode.STANDARD,
                    periodOfDay = openAppUiState.periodOfDay,
                    linkTarget = openAppUiState.linkTarget,
                    onClick = { dismissAlarm(openAppUiState.linkTarget) },
                )

                if (hasPremium == false) {
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
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DismissAlarmContentPreview() {
    AppLinkAlarmTheme {
        DismissAlarmContent(
            openAppUiState = OpenAppUiState(
                alarmName = "Alarm Name",
                alarmMessage = "Alarm Message",
                hour = 12,
                minute = 30,
                periodOfDay = PeriodOfDay.AM,
                linkTarget = LinkTarget.App(packageName = "com.example.app")
            ),
            hasPremium = false,
            onShowErrorSnackBar = {},
            dismissAlarm = {}
        )
    }
}