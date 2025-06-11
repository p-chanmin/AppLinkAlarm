package com.oldogz.applinkalarm.feature.alarm.home

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oldogz.applinkalarm.feature.alarm.component.AppIconImage
import com.oldogz.applinkalarm.feature.alarm.model.AlarmHomeUiState
import com.oldogz.core.designsystem.component.AppLinkAlarmIconButton
import com.oldogz.core.designsystem.component.AppLinkAlarmSwitch
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.designsystem.theme.Paddings
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.PeriodOfDay
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun AlarmHomeScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToAlarmEdit: (Int?) -> Unit,
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
        updateAlarmActive = alarmHomeViewModel::updateAlarmActive
    )
}

@Composable
private fun AlarmHomeContent(
    homeUiState: AlarmHomeUiState,
    paddingValues: PaddingValues,
    navigateToAlarmEdit: (Int?) -> Unit,
    updateAlarmActive: (AppLinkAlarm, Boolean) -> Unit,
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
                        onClick = { navigateToAlarmEdit(null) }
                    )
                }
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(homeUiState.alarms, key = { it.id }) { alarm ->
                    AppLinkAlarmItem(
                        modifier = Modifier,
                        onClick = { navigateToAlarmEdit(alarm.id) },
                        appLinkAlarm = alarm,
                        updateAlarmActive = { updateAlarmActive(alarm, it) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun AppLinkAlarmItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    appLinkAlarm: AppLinkAlarm,
    updateAlarmActive: (Boolean) -> Unit,
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Paddings.small),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .clickable { onClick() }
                .padding(Paddings.medium)
                .padding(vertical = Paddings.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            Row(
                modifier = Modifier.weight(1f)
            ) {
                AppIconImage(
                    linkedAppPackage = appLinkAlarm.linkedAppPackage
                )
                Column(
                    modifier = Modifier.padding(start = Paddings.xlarge),
                ) {
                    Text(
                        text = appLinkAlarm.alarmName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        modifier = Modifier.padding(top = Paddings.small),
                        text = alarmModeToString(
                            context,
                            appLinkAlarm.alarmMode,
                            appLinkAlarm.directAppLaunch
                        ),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                    Text(
                        modifier = Modifier.padding(top = Paddings.small),
                        text = dayOfWeekToString(context, appLinkAlarm.dayOfWeek),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                    val hour = appLinkAlarm.hour.toString().padStart(2, '0')
                    val minute = appLinkAlarm.minute.toString().padStart(2, '0')
                    Text(
                        modifier = Modifier.padding(top = Paddings.small),
                        text = "$hour:$minute ${appLinkAlarm.periodOfDay}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                }
            }
            AppLinkAlarmSwitch(
                modifier = Modifier,
                checked = appLinkAlarm.active,
                onCheckedChange = updateAlarmActive
            )
        }
    }
}

private fun alarmModeToString(
    context: Context,
    alarmMode: AlarmMode,
    directAppLaunch: Boolean
): String {
    val alarmModeText = when (alarmMode) {
        AlarmMode.INSTANT -> "Instant Alarm"
        AlarmMode.FLEXIBLE -> "Flexible Alarm"
    }
    return if (directAppLaunch) {
        val directAppLaunchText = "Direct App Launch"
        "$alarmModeText, $directAppLaunchText"
    } else {
        alarmModeText
    }
}

private fun dayOfWeekToString(context: Context, dayOfWeek: List<DayOfWeek>): String {
    return if (dayOfWeek.size == 7) {
        "Every Day"
    } else {
        dayOfWeek.map {
            when (it) {
                DayOfWeek.SUNDAY -> "Son"
                DayOfWeek.MONDAY -> "Mon"
                DayOfWeek.TUESDAY -> "Tue"
                DayOfWeek.WEDNESDAY -> "Wed"
                DayOfWeek.THURSDAY -> "Thu"
                DayOfWeek.FRIDAY -> "Fri"
                DayOfWeek.SATURDAY -> "Sat"
            }
        }.joinToString(",")
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeContentPreview() {
    AppLinkAlarmTheme {
        AlarmHomeContent(
            homeUiState = AlarmHomeUiState(
                alarms = persistentListOf(
                    AppLinkAlarm(
                        id = 1,
                        hour = 10,
                        minute = 3,
                        periodOfDay = PeriodOfDay.AM,
                        alarmName = "알람 테스트 1 입니다.",
                        dayOfWeek = listOf(
                            DayOfWeek.MONDAY,
                            DayOfWeek.TUESDAY,
                            DayOfWeek.WEDNESDAY,
                            DayOfWeek.THURSDAY,
                            DayOfWeek.FRIDAY,
                            DayOfWeek.SATURDAY,
                            DayOfWeek.SUNDAY
                        ),
                        active = true
                    ),
                    AppLinkAlarm(
                        id = 2,
                        hour = 8,
                        minute = 30,
                        periodOfDay = PeriodOfDay.PM,
                        alarmName = "알람 테스트 2 입니다.",
                        dayOfWeek = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                        directAppLaunch = true,
                        active = true
                    ),
                )
            ),
            paddingValues = PaddingValues(),
            navigateToAlarmEdit = {},
            updateAlarmActive = { _, _ -> }
        )
    }
}