package com.oldogz.core.model

data class AppLinkAlarm(
    val id: Int = 0,
    val linkedAppPackage: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    val periodOfDay: String = "",
    val dayOfWeek: String = "",
    val alarmName: String = "",
    val alarmMessage: String = "",
    val alarmMode: String = "",
    val vibrate: Boolean = true,
    val alarmSound: String? = null,
    val directAppLaunch: Boolean = false,
)
