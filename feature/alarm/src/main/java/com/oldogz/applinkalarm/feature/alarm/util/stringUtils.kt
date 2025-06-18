package com.oldogz.applinkalarm.feature.alarm.util

import android.content.Context
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.core.model.DayOfWeek

internal fun dayOfWeekToString(context: Context, dayOfWeek: List<DayOfWeek>): String {
    return if (dayOfWeek.size == 7) {
        context.getString(R.string.feature_alarm_text_every_day)
    } else {
        dayOfWeek.sortedBy { it.ordinal }.joinToString(",") {
            when (it) {
                DayOfWeek.SUNDAY -> context.getString(R.string.feature_alarm_text_sunday)
                DayOfWeek.MONDAY -> context.getString(R.string.feature_alarm_text_monday)
                DayOfWeek.TUESDAY -> context.getString(R.string.feature_alarm_text_tuesday)
                DayOfWeek.WEDNESDAY -> context.getString(R.string.feature_alarm_text_wednesday)
                DayOfWeek.THURSDAY -> context.getString(R.string.feature_alarm_text_thursday)
                DayOfWeek.FRIDAY -> context.getString(R.string.feature_alarm_text_friday)
                DayOfWeek.SATURDAY -> context.getString(R.string.feature_alarm_text_saturday)
            }
        }
    }
}