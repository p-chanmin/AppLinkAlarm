package com.oldogz.core.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.workDataOf
import com.google.firebase.analytics.logEvent
import com.oldogz.core.alarm.manager.AppLinkAlarmScheduleManager.Companion.INTENT_ACTION_APP_LINK_ALARM
import com.oldogz.core.alarm.manager.AppLinkAlarmScheduleManager.Companion.INTENT_EXTRA_APP_LINK_ALARM_ID
import com.oldogz.core.alarm.manager.AppLinkAlarmScheduleManager.Companion.INTENT_EXTRA_APP_LINK_ALARM_MODE
import com.oldogz.core.alarm.manager.AppLinkAlarmScheduleManager.Companion.INTENT_EXTRA_APP_LINK_ALARM_PACKAGE
import com.oldogz.core.alarm.manager.AppLinkAlarmStateManager
import com.oldogz.core.alarm.manager.WorkRequestManager
import com.oldogz.core.alarm.worker.NOTIFICATION_ALARM_DATA_ID
import com.oldogz.core.alarm.worker.NOTIFICATION_ALARM_TAG
import com.oldogz.core.alarm.worker.NOTIFICATION_NOT_FOUND_TAG
import com.oldogz.core.alarm.worker.NotificationAlarmWorker
import com.oldogz.core.alarm.worker.RESCHEDULE_ALARM_ALL_TAG
import com.oldogz.core.alarm.worker.RESCHEDULE_ALARM_DATA_ID
import com.oldogz.core.alarm.worker.RESCHEDULE_ALARM_TAG
import com.oldogz.core.alarm.worker.RescheduleAlarmWorker
import com.oldogz.core.firebase.FirebaseManager
import com.oldogz.core.firebase.model.FA
import com.oldogz.core.model.AlarmMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppLinkAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workRequestManager: WorkRequestManager

    @Inject
    lateinit var appLinkAlarmStateManager: AppLinkAlarmStateManager

    @Inject
    lateinit var firebaseManager: FirebaseManager

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_MY_PACKAGE_REPLACED -> {
                workRequestManager.enqueueWorker<RescheduleAlarmWorker>(RESCHEDULE_ALARM_ALL_TAG)
            }

            INTENT_ACTION_APP_LINK_ALARM -> {
                val alarmId = intent.getIntExtra(INTENT_EXTRA_APP_LINK_ALARM_ID, -1)
                val alarmMode = AlarmMode.fromString(
                    intent.getStringExtra(INTENT_EXTRA_APP_LINK_ALARM_MODE)
                )
                val linkedAppPackage = intent.getStringExtra(INTENT_EXTRA_APP_LINK_ALARM_PACKAGE)
                if (alarmId == -1 || alarmMode == null || linkedAppPackage == null) return

                firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_TRIGGERED) {
                    param(FA.Param.Key.ALARM_MODE, alarmMode.name)
                }

                if (isAppInstalled(context, linkedAppPackage)) {
                    when (alarmMode) {
                        AlarmMode.NOTIFICATION_ONLY -> {
                            workRequestManager.enqueueWorker<NotificationAlarmWorker>(
                                NOTIFICATION_ALARM_TAG,
                                workDataOf(NOTIFICATION_ALARM_DATA_ID to alarmId)
                            )
                        }

                        AlarmMode.STANDARD -> {
                            appLinkAlarmStateManager.startService(alarmId)
                        }
                    }
                    workRequestManager.enqueueWorker<RescheduleAlarmWorker>(
                        RESCHEDULE_ALARM_TAG,
                        workDataOf(RESCHEDULE_ALARM_DATA_ID to alarmId)
                    )
                } else {
                    firebaseManager.firebaseAnalytics.logEvent(FA.Event.LINKED_APP_NOT_FOUND) {}
                    workRequestManager.enqueueWorker<NotificationAlarmWorker>(
                        NOTIFICATION_NOT_FOUND_TAG,
                        workDataOf(NOTIFICATION_ALARM_DATA_ID to alarmId)
                    )
                }
            }
        }
    }

    private fun isAppInstalled(context: Context, linkedAppPackage: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(linkedAppPackage, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}
