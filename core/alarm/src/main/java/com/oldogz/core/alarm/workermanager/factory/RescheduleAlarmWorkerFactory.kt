package com.oldogz.core.alarm.workermanager.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.oldogz.core.alarm.manager.AppLinkAlarmScheduleManager
import com.oldogz.core.alarm.workermanager.worker.RescheduleAlarmWorker
import com.oldogz.core.data.AppLinkAlarmRepository
import javax.inject.Inject

class RescheduleAlarmWorkerFactory @Inject constructor(
    private val appLinkAlarmRepository: AppLinkAlarmRepository,
    private val appLinkAlarmScheduleManager: AppLinkAlarmScheduleManager,
) : ChildWorkerFactory {

    override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
        return RescheduleAlarmWorker(
            appLinkAlarmRepository,
            appLinkAlarmScheduleManager,
            appContext,
            params
        )
    }
}