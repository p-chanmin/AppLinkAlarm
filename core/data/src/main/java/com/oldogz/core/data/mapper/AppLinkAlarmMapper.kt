package com.oldogz.core.data.mapper

import com.oldogz.core.database.entity.AlarmEntity
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.PeriodOfDay
import kotlinx.serialization.json.Json

internal fun AlarmEntity.toData(): AppLinkAlarm {
    return AppLinkAlarm(
        id = this.id,
        linkedAppPackage = this.linkedAppPackage,
        hour = this.hour,
        minute = this.minute,
        periodOfDay = Json.decodeFromString<PeriodOfDay>(this.periodOfDay),
        dayOfWeek = Json.decodeFromString<List<DayOfWeek>>(this.dayOfWeek).sortedBy { it.ordinal },
        alarmName = this.alarmName,
        alarmMessage = this.alarmMessage,
        alarmMode = Json.decodeFromString<AlarmMode>(this.alarmMode),
        vibrate = this.vibrate,
        alarmSound = this.alarmSound,
        alarmVolume = this.alarmVolume,
        directAppLaunch = this.directAppLaunch,
        active = this.active
    )
}