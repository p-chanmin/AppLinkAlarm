package com.oldogz.applinkalarm.feature.setting.model

import androidx.compose.runtime.Immutable

@Immutable
data class SettingUiState(
    val text: String = "sample",
    val notificationPermission: Boolean? = null,
    val exactAlarmPermission: Boolean? = null,
)
