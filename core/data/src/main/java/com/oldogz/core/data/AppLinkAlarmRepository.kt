package com.oldogz.core.data

import com.oldogz.core.data.mapper.toData
import com.oldogz.core.database.datasource.AppLinkAlarmDataSource
import com.oldogz.core.model.AppLinkAlarm
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AppLinkAlarmRepository @Inject constructor(
    private val appLinkAlarmDataSource: AppLinkAlarmDataSource
) {

    val alarms = appLinkAlarmDataSource.alarms.map { entities ->
        entities.map { it.toData() }.sortedWith(
            compareBy<AppLinkAlarm> { it.periodOfDay.ordinal }
                .thenBy { it.hour % 12 }
                .thenBy { it.minute }
        )
    }

    fun getAlarmById(id: Int) = appLinkAlarmDataSource.getAlarmById(id).map { it.toData() }

    suspend fun addAlarm(appLinkAlarm: AppLinkAlarm): Int {
        return appLinkAlarmDataSource.addAlarm(
            linkTarget = Json.encodeToString(appLinkAlarm.linkTarget),
            hour = appLinkAlarm.hour,
            minute = appLinkAlarm.minute,
            periodOfDay = Json.encodeToString(appLinkAlarm.periodOfDay),
            dayOfWeek = Json.encodeToString(appLinkAlarm.dayOfWeek),
            alarmName = appLinkAlarm.alarmName,
            alarmMessage = appLinkAlarm.alarmMessage,
            alarmMode = Json.encodeToString(appLinkAlarm.alarmMode),
            vibrate = appLinkAlarm.vibrate,
            alarmSound = appLinkAlarm.alarmSound,
            alarmVolume = appLinkAlarm.alarmVolume,
            active = appLinkAlarm.active,
        )
    }

    suspend fun updateAlarm(appLinkAlarm: AppLinkAlarm) {
        appLinkAlarmDataSource.updateAlarm(
            id = appLinkAlarm.id,
            linkTarget = Json.encodeToString(appLinkAlarm.linkTarget),
            hour = appLinkAlarm.hour,
            minute = appLinkAlarm.minute,
            periodOfDay = Json.encodeToString(appLinkAlarm.periodOfDay),
            dayOfWeek = Json.encodeToString(appLinkAlarm.dayOfWeek),
            alarmName = appLinkAlarm.alarmName,
            alarmMessage = appLinkAlarm.alarmMessage,
            alarmMode = Json.encodeToString(appLinkAlarm.alarmMode),
            vibrate = appLinkAlarm.vibrate,
            alarmSound = appLinkAlarm.alarmSound,
            alarmVolume = appLinkAlarm.alarmVolume,
            active = appLinkAlarm.active,
        )
    }

    suspend fun deleteAlarmById(id: Int) {
        appLinkAlarmDataSource.deleteAlarmById(id)
    }
}