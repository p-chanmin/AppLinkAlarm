package com.oldogz.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.oldogz.core.alarm.AppLinkAlarmManager.Companion.INTENT_ACTION_APP_LINK_ALARM
import com.oldogz.core.alarm.AppLinkAlarmManager.Companion.INTENT_EXTRA_APP_LINK_ALARM_ID
import com.oldogz.core.alarm.AppLinkAlarmPlayingService.Companion.INTENT_ACTION_SERVICE_APP_LINK_ALARM_ON
import com.oldogz.core.alarm.AppLinkAlarmPlayingService.Companion.INTENT_EXTRA_SERVICE_APP_LINK_ALARM_ID
import com.oldogz.core.data.AppLinkAlarmRepository
import com.oldogz.core.model.AlarmMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppLinkAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var appLinkAlarmRepository: AppLinkAlarmRepository

    @Inject
    lateinit var appLinkAlarmManager: AppLinkAlarmManager

    @Inject
    lateinit var appLinkAlarmNotificationManager: AppLinkAlarmNotificationManager

    private val includeAds = false // 광고

    override fun onReceive(context: Context, intent: Intent) {

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_TIMEZONE_CHANGED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val appLinkAlarms = appLinkAlarmRepository.alarms.first()
                    appLinkAlarms.forEach { appLinkAlarm ->
                        if (appLinkAlarmManager.checkScheduleExactAlarms()) {
                            appLinkAlarmManager.scheduleAlarm(appLinkAlarm)
                        }
                    }
                }
            }

            INTENT_ACTION_APP_LINK_ALARM -> {
                appLinkAlarmNotificationManager.registerNotificationChannels()
                val id = intent.getIntExtra(INTENT_EXTRA_APP_LINK_ALARM_ID, -1)
                if (id == -1) return

                CoroutineScope(Dispatchers.IO).launch {
                    val appLinkAlarm = appLinkAlarmRepository.getAlarmById(id).first()

                    if (isAppInstalled(context, appLinkAlarm.linkedAppPackage)) {
                        when (appLinkAlarm.alarmMode) {
                            AlarmMode.NOTIFICATION_ONLY -> {
                                appLinkAlarmNotificationManager.notify(
                                    appLinkAlarm.id,
                                    appLinkAlarmNotificationManager.createNotification(
                                        appLinkAlarm,
                                        includeAds
                                    )
                                )
                            }

                            AlarmMode.STANDARD -> {
                                val serviceIntent =
                                    Intent(context, AppLinkAlarmPlayingService::class.java).apply {
                                        action = INTENT_ACTION_SERVICE_APP_LINK_ALARM_ON
                                        putExtra(
                                            INTENT_EXTRA_SERVICE_APP_LINK_ALARM_ID,
                                            appLinkAlarm.id
                                        )
                                    }
                                context.startForegroundService(serviceIntent)
                            }
                        }
                        appLinkAlarmManager.scheduleAlarm(appLinkAlarm)
                    } else {
                        appLinkAlarmRepository.updateAlarm(appLinkAlarm.copy(active = false))
                        appLinkAlarmNotificationManager.notify(
                            appLinkAlarm.id,
                            appLinkAlarmNotificationManager.createNoLinkedAppNotification(
                                appLinkAlarm
                            )
                        )
                    }
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
