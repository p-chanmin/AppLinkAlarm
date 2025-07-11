package com.oldogz.core.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import com.google.firebase.analytics.logEvent
import com.oldogz.core.alarm.manager.AppLinkAlarmManager
import com.oldogz.core.alarm.manager.AppLinkAlarmManager.Companion.INTENT_ACTION_APP_LINK_ALARM
import com.oldogz.core.alarm.manager.AppLinkAlarmManager.Companion.INTENT_EXTRA_APP_LINK_ALARM_ID
import com.oldogz.core.alarm.manager.AppLinkAlarmManager.Companion.INTENT_EXTRA_APP_LINK_ALARM_MODE
import com.oldogz.core.alarm.manager.AppLinkAlarmManager.Companion.INTENT_EXTRA_APP_LINK_ALARM_PACKAGE
import com.oldogz.core.alarm.manager.AppLinkAlarmNotificationManager
import com.oldogz.core.alarm.manager.AppLinkAlarmStateManager
import com.oldogz.core.alarm.manager.WorkRequestManager
import com.oldogz.core.alarm.workermanager.worker.NOTIFICATION_ALARM_DATA_ID
import com.oldogz.core.alarm.workermanager.worker.NOTIFICATION_ALARM_TAG
import com.oldogz.core.alarm.workermanager.worker.NOTIFICATION_NOT_FOUND_TAG
import com.oldogz.core.alarm.workermanager.worker.NotificationAlarmWorker
import com.oldogz.core.alarm.workermanager.worker.RESCHEDULE_ALARM_DATA_ID
import com.oldogz.core.alarm.workermanager.worker.RESCHEDULE_ALARM_TAG
import com.oldogz.core.alarm.workermanager.worker.RescheduleAlarmWorker
import com.oldogz.core.billing.SubscriptionManager
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
    lateinit var appLinkAlarmManager: AppLinkAlarmManager

    @Inject
    lateinit var appLinkAlarmStateManager: AppLinkAlarmStateManager

    @Inject
    lateinit var appLinkAlarmNotificationManager: AppLinkAlarmNotificationManager

    @Inject
    lateinit var subscriptionManager: SubscriptionManager

    @Inject
    lateinit var firebaseManager: FirebaseManager

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_TIMEZONE_CHANGED -> {
                workRequestManager.enqueueWorker<RescheduleAlarmWorker>(RESCHEDULE_ALARM_TAG)
            }

            INTENT_ACTION_APP_LINK_ALARM -> {
                val alarmId = intent.getIntExtra(INTENT_EXTRA_APP_LINK_ALARM_ID, -1)
                val alarmModeString = intent.getStringExtra(INTENT_EXTRA_APP_LINK_ALARM_MODE)
                val linkedAppPackage = intent.getStringExtra(INTENT_EXTRA_APP_LINK_ALARM_PACKAGE)
                if (alarmId == -1 || alarmModeString == null || linkedAppPackage == null) return

                val alarmMode = AlarmMode.fromString(alarmModeString)

                if (isAppInstalled(context, linkedAppPackage)) {
                    when (alarmMode) {
                        AlarmMode.NOTIFICATION_ONLY -> {
                            workRequestManager.enqueueWorker<NotificationAlarmWorker>(
                                NOTIFICATION_ALARM_TAG,
                                Data.Builder().putInt(NOTIFICATION_ALARM_DATA_ID, alarmId).build()
                            )
                        }

                        AlarmMode.STANDARD -> {
                            appLinkAlarmStateManager.startService(alarmId, alarmMode)
                        }
                    }
                    workRequestManager.enqueueWorker<RescheduleAlarmWorker>(
                        RESCHEDULE_ALARM_TAG,
                        Data.Builder().putInt(RESCHEDULE_ALARM_DATA_ID, alarmId).build()
                    )
                } else {
                    firebaseManager.firebaseAnalytics.logEvent(FA.Event.LINKED_APP_NOT_FOUND) {}
                    workRequestManager.enqueueWorker<NotificationAlarmWorker>(
                        NOTIFICATION_NOT_FOUND_TAG,
                        Data.Builder().putInt(NOTIFICATION_ALARM_DATA_ID, alarmId).build()
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
