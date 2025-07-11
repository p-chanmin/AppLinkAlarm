package com.oldogz.core.alarm.workermanager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oldogz.core.alarm.manager.AppLinkAlarmManager
import com.oldogz.core.data.AppLinkAlarmRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class RescheduleAlarmWorker @AssistedInject constructor(
    private val appLinkAlarmRepository: AppLinkAlarmRepository,
    private val appLinkAlarmManager: AppLinkAlarmManager,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            println("doWork RescheduleAlarmWorker")
            val alarmId = inputData.getInt(RESCHEDULE_ALARM_DATA_ID, -1)
            if (alarmId == -1) {
                println("schedule all alarms")
                val appLinkAlarms = appLinkAlarmRepository.alarms.first()
                appLinkAlarms.forEach { appLinkAlarm ->
                    if (appLinkAlarmManager.checkScheduleExactAlarms()) {
                        appLinkAlarmManager.scheduleAlarm(appLinkAlarm)
                    }
                }
            } else {
                println("schedule alarm with id $alarmId")
                val appLinkAlarm = appLinkAlarmRepository.getAlarmById(alarmId).first()
                appLinkAlarmManager.scheduleAlarm(appLinkAlarm)
            }
            println("RescheduleAlarmWorker success")
            Result.success()
        } catch (throwable: Throwable) {
            println("RescheduleAlarmWorker failure")
            Result.failure()
        }
    }
}

const val RESCHEDULE_ALARM_TAG = "rescheduleAlarmTag"
const val RESCHEDULE_ALARM_DATA_ID = "rescheduleAlarmDataId"