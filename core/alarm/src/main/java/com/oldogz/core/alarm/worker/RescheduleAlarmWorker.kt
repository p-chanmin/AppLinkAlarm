package com.oldogz.core.alarm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oldogz.core.alarm.manager.AppLinkAlarmNotificationManager
import com.oldogz.core.alarm.manager.AppLinkAlarmScheduleManager
import com.oldogz.core.data.AppLinkAlarmRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class RescheduleAlarmWorker @AssistedInject constructor(
    private val appLinkAlarmRepository: AppLinkAlarmRepository,
    private val appLinkAlarmScheduleManager: AppLinkAlarmScheduleManager,
    private val appLinkAlarmNotificationManager: AppLinkAlarmNotificationManager,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            when {
                tags.contains(RESCHEDULE_ALARM_ALL_TAG) -> {
                    val appLinkAlarms = appLinkAlarmRepository.alarms.first()
                    appLinkAlarms.forEach { appLinkAlarm ->
                        if (appLinkAlarmScheduleManager.checkScheduleExactAlarms()) {
                            appLinkAlarmScheduleManager.scheduleAlarm(appLinkAlarm)
                        }
                    }
                }

                tags.contains(RESCHEDULE_ALARM_TAG) -> {
                    val alarmId = inputData.getInt(RESCHEDULE_ALARM_DATA_ID, -1)
                    val appLinkAlarm = appLinkAlarmRepository.getAlarmById(alarmId).first()
                    appLinkAlarmScheduleManager.scheduleAlarm(
                        alarm = appLinkAlarm,
                        isRescheduling = true
                    )
                }

                tags.contains(RESCHEDULE_ALARM_SKIP_TAG) -> {
                    val alarmId = inputData.getInt(RESCHEDULE_ALARM_DATA_ID, -1)
                    val appLinkAlarm = appLinkAlarmRepository.getAlarmById(alarmId).first()
                    appLinkAlarmScheduleManager.scheduleAlarm(
                        alarm = appLinkAlarm, isRescheduling = true, isSkip = true
                    )
                    appLinkAlarmNotificationManager.cancel(alarmId)
                }
            }
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}

const val RESCHEDULE_ALARM_TAG = "rescheduleAlarmTag"
const val RESCHEDULE_ALARM_SKIP_TAG = "rescheduleAlarmSkipTag"
const val RESCHEDULE_ALARM_ALL_TAG = "rescheduleAlarmAllTag"
const val RESCHEDULE_ALARM_DATA_ID = "rescheduleAlarmDataId"