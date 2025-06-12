package com.oldogz.applinkalarm.feature.alarm.util

import android.content.Context
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.DayOfWeek

internal fun alarmModeToString(
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

internal fun dayOfWeekToString(context: Context, dayOfWeek: List<DayOfWeek>): String {
    return if (dayOfWeek.size == 7) {
        "Every Day"
    } else {
        dayOfWeek.sortedBy { it.ordinal }.joinToString(",") {
            when (it) {
                DayOfWeek.SUNDAY -> "Sun"
                DayOfWeek.MONDAY -> "Mon"
                DayOfWeek.TUESDAY -> "Tue"
                DayOfWeek.WEDNESDAY -> "Wed"
                DayOfWeek.THURSDAY -> "Thu"
                DayOfWeek.FRIDAY -> "Fri"
                DayOfWeek.SATURDAY -> "Sat"
            }
        }
    }
}