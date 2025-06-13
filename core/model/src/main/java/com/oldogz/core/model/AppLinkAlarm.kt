package com.oldogz.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AppLinkAlarm(
    val id: Int = 0,
    val linkedAppPackage: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    val periodOfDay: PeriodOfDay = PeriodOfDay.AM,
    val dayOfWeek: List<DayOfWeek> = listOf(),
    val alarmName: String = "",
    val alarmMessage: String = "",
    val alarmMode: AlarmMode = AlarmMode.ONLY_NOTIFICATION,
    val vibrate: Boolean = true,
    val alarmSound: String? = null,
    val directAppLaunch: Boolean = false,
    val active: Boolean = true,
)