package com.oldogz.applinkalarm.feature.alarm

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
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
    )
}

@Composable
private fun AlarmHomeContent(
    homeUiState: AlarmHomeUiState,
    paddingValues: PaddingValues,
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
                        onClick = {}
                    )
                }
            )
            val context = LocalContext.current
            val packageManager = context.packageManager

            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            // 실행 가능한 앱(런처에 표시되는 앱) 목록을 가져옴
            val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

            for (resolveInfo in resolveInfoList) {
                val app = resolveInfo.activityInfo.applicationInfo
                println("appName : ${app.loadLabel(packageManager)}")
                println("packageName : ${app.packageName}")
                println("icon  : ${app.loadIcon(packageManager)}")
                println("---")
            }

            val appInfo = packageManager.getApplicationInfo("net.orizinal.subway", 0)
            println("appName : ${appInfo.loadLabel(packageManager)}")
            println("packageName : ${appInfo.packageName}")
            println("icon  : ${appInfo.loadIcon(packageManager)}")
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
            paddingValues = PaddingValues()
        )
    }
}