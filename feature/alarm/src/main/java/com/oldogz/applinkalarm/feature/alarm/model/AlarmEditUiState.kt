package com.oldogz.applinkalarm.feature.alarm.model

import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.DayOfWeek
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AlarmEditUiState(
    val linkedAppPackage: String? = null,
    val hour: Int = 0,
    val minute: Int = 0,
    val periodOfDay: String = "AM",
    val dayOfWeek: ImmutableList<DayOfWeek> = persistentListOf(),
    val alarmName: String = "",
    val message: String = "",
    val alarmMode: AlarmMode = AlarmMode.FLEXIBLE,
    val directAppLaunch: Boolean = false,
    val vibrate: Boolean = true,
    val alarmSound: String? = null,
    val selectAppDialog: Boolean = false,
)
