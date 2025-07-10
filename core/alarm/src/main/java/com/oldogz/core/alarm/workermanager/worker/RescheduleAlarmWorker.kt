package com.oldogz.core.alarm.workermanager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oldogz.core.alarm.manager.AppLinkAlarmManager
import com.oldogz.core.alarm.manager.WorkRequestManager
import com.oldogz.core.data.AppLinkAlarmRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class RescheduleAlarmWorker @AssistedInject constructor(
    private val workRequestManager: WorkRequestManager,
    private val appLinkAlarmRepository: AppLinkAlarmRepository,
    private val appLinkAlarmManager: AppLinkAlarmManager,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val appLinkAlarms = appLinkAlarmRepository.alarms.first()
            appLinkAlarms.forEach { appLinkAlarm ->
                if (appLinkAlarmManager.checkScheduleExactAlarms()) {
                    appLinkAlarmManager.scheduleAlarm(appLinkAlarm)
                }
            }
            workRequestManager.cancelWorker(RESCHEDULE_ALARM_TAG)
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}

const val RESCHEDULE_ALARM_TAG = "rescheduleAlarmTag"