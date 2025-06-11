package com.oldogz.core.database.datasource

import com.oldogz.core.database.dao.AlarmEntityDao
import javax.inject.Inject

class AppLinkAlarmDataSource @Inject constructor(
    private val alarmEntityDao: AlarmEntityDao,
) {

    val alarms = alarmEntityDao.getAllAlarm()

    fun getAlarmById(id: Int) = alarmEntityDao.getAlarmById(id)
}