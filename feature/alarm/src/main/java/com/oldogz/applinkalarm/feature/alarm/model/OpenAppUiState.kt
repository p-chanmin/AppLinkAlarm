package com.oldogz.applinkalarm.feature.alarm.model

import androidx.compose.runtime.Immutable
import com.oldogz.core.model.PeriodOfDay

@Immutable
data class OpenAppUiState(
    val id: Int = 0,
    val alarmName: String = "",
    val alarmMessage: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    val periodOfDay: PeriodOfDay = PeriodOfDay.AM,
    val linkedAppPackage: String = "",
)