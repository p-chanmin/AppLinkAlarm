package com.oldogz.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.oldogz.core.database.dao.AlarmEntityDao
import com.oldogz.core.database.entity.AlarmEntity

@Database(entities = [AlarmEntity::class], version = 2)
abstract class AppLinkAlarmDatabase : RoomDatabase() {
    abstract fun alarmEntityDao(): AlarmEntityDao
}