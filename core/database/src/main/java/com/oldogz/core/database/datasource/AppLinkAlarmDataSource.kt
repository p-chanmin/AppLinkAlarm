package com.oldogz.core.database.datasource

import com.oldogz.core.database.dao.AlarmEntityDao
import com.oldogz.core.database.entity.AlarmEntity
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AppLinkAlarmDataSource @Inject constructor(
    private val alarmEntityDao: AlarmEntityDao,
) {

    val alarms = alarmEntityDao.getAllAlarm()

    fun getAlarmById(id: Int) = alarmEntityDao.getAlarmById(id)

    suspend fun addAlarm(
        linkedAppPackage: String = "",
        hour: Int = 0,
        minute: Int = 0,
        periodOfDay: String = "",
        dayOfWeek: String = "",
        alarmName: String = "",
        alarmMessage: String = "",
        alarmMode: String = "",
        vibrate: Boolean = true,
        alarmSound: String? = null,
        directAppLaunch: Boolean = false,
        active: Boolean = true,
    ): Int {
        return alarmEntityDao.insert(
            AlarmEntity(
                linkedAppPackage = linkedAppPackage,
                hour = hour,
                minute = minute,
                periodOfDay = periodOfDay,
                dayOfWeek = dayOfWeek,
                alarmName = alarmName,
                alarmMessage = alarmMessage,
                alarmMode = alarmMode,
                vibrate = vibrate,
                alarmSound = alarmSound,
                directAppLaunch = directAppLaunch,
                active = active,
            )
        ).toInt()
    }

    suspend fun updateAlarm(
        id: Int,
        linkedAppPackage: String = "",
        hour: Int = 0,
        minute: Int = 0,
        periodOfDay: String = "",
        dayOfWeek: String = "",
        alarmName: String = "",
        alarmMessage: String = "",
        alarmMode: String = "",
        vibrate: Boolean = true,
        alarmSound: String? = null,
        directAppLaunch: Boolean = false,
        active: Boolean = true,
    ) {
        val alarmEntity = getAlarmById(id).first()
        alarmEntityDao.update(
            alarmEntity.copy(
                id = id,
                linkedAppPackage = linkedAppPackage,
                hour = hour,
                minute = minute,
                periodOfDay = periodOfDay,
                dayOfWeek = dayOfWeek,
                alarmName = alarmName,
                alarmMessage = alarmMessage,
                alarmMode = alarmMode,
                vibrate = vibrate,
                alarmSound = alarmSound,
                directAppLaunch = directAppLaunch,
                active = active,
            )
        )
    }

    suspend fun deleteAlarmById(id: Int) {
        alarmEntityDao.deleteAlarmById(id)
    }
}