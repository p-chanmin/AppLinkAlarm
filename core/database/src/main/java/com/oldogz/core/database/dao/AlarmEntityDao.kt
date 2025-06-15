package com.oldogz.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.oldogz.core.database.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmEntityDao {
    @Insert
    suspend fun insert(alarmEntity: AlarmEntity): Long

    @Query("SELECT * FROM alarm")
    fun getAllAlarm(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarm WHERE id = :id LIMIT 1")
    fun getAlarmById(id: Int): Flow<AlarmEntity>

    @Update
    suspend fun update(alarmEntity: AlarmEntity)

    @Query("DELETE FROM alarm WHERE id = :id")
    suspend fun deleteAlarmById(id: Int)
}