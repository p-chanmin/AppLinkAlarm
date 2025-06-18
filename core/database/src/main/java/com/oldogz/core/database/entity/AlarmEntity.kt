package com.oldogz.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
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
    val alarmVolume: Int = 80,
    val active: Boolean = true,
)