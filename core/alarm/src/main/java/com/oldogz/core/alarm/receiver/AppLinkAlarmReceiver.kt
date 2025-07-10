package com.oldogz.core.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.oldogz.core.alarm.manager.AppLinkAlarmManager.Companion.INTENT_ACTION_APP_LINK_ALARM
import com.oldogz.core.alarm.manager.AppLinkAlarmManager.Companion.INTENT_EXTRA_APP_LINK_ALARM_ID
import com.oldogz.core.alarm.manager.AppLinkAlarmManager.Companion.INTENT_EXTRA_APP_LINK_ALARM_MODE
import com.oldogz.core.alarm.manager.AppLinkAlarmStateManager
import com.oldogz.core.alarm.manager.WorkRequestManager
import com.oldogz.core.alarm.workermanager.worker.RESCHEDULE_ALARM_TAG
import com.oldogz.core.alarm.workermanager.worker.RescheduleAlarmWorker
import com.oldogz.core.model.AlarmMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppLinkAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workRequestManager: WorkRequestManager

    @Inject
    lateinit var appLinkAlarmStateManager: AppLinkAlarmStateManager

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_TIMEZONE_CHANGED -> {
                workRequestManager.enqueueWorker<RescheduleAlarmWorker>(RESCHEDULE_ALARM_TAG)
            }

            INTENT_ACTION_APP_LINK_ALARM -> {
                val alarmId = intent.getIntExtra(INTENT_EXTRA_APP_LINK_ALARM_ID, -1)
                val alarmMode = intent.getStringExtra(INTENT_EXTRA_APP_LINK_ALARM_MODE)
                if (alarmId == -1 || alarmMode == null) return

                appLinkAlarmStateManager.startService(alarmId, AlarmMode.fromString(alarmMode))
            }
        }
    }
}
