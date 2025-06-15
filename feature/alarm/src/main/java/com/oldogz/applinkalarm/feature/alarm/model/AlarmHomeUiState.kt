package com.oldogz.applinkalarm.feature.alarm.model

import androidx.compose.runtime.Immutable
import com.oldogz.core.model.AppLinkAlarm
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class AlarmHomeUiState(
    val isSelectMode: Boolean = false,
    val alarms: ImmutableList<AppLinkAlarmUiState> = persistentListOf(),
    val notificationPermissionState: PermissionState = PermissionState.GRANTED,
    val deniedNotificationDialog: Boolean = false,
)

@Immutable
data class AppLinkAlarmUiState(
    val selected: Boolean = false,
    val appLinkAlarm: AppLinkAlarm
)