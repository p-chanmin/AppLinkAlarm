package com.oldogz.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.oldogz.core.data.AppLinkAlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppLinkAlarmRegisterReceiver : BroadcastReceiver() {

    @Inject
    lateinit var appLinkAlarmRepository: AppLinkAlarmRepository

    @Inject
    lateinit var appLinkAlarmManager: AppLinkAlarmManager

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
        }
    }
}