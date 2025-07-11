package com.oldogz.core.alarm.manager

import android.content.Context
import android.content.Intent
import com.google.firebase.analytics.logEvent
import com.oldogz.core.alarm.service.AppLinkAlarmPlayingService
import com.oldogz.core.alarm.service.AppLinkAlarmPlayingService.Companion.INTENT_ACTION_SERVICE_APP_LINK_ALARM_ON
import com.oldogz.core.alarm.service.AppLinkAlarmPlayingService.Companion.INTENT_EXTRA_SERVICE_APP_LINK_ALARM_ID
import com.oldogz.core.firebase.FirebaseManager
import com.oldogz.core.firebase.model.FA
import com.oldogz.core.model.AlarmMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLinkAlarmStateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseManager: FirebaseManager,
) {
    private val _currentAppLinkAlarmId: MutableStateFlow<Int?> = MutableStateFlow(null)
    val currentAppLinkAlarmId = _currentAppLinkAlarmId.asStateFlow()

    fun updateCurrentAppLinkAlarmId(id: Int?) {
        _currentAppLinkAlarmId.value = id
    }

    fun startService(alarmId: Int, alarmMode: AlarmMode) {
        firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_TRIGGERED) {
            param(FA.Param.Key.ALARM_MODE, alarmMode.name)
        }

        val serviceIntent =
            Intent(context, AppLinkAlarmPlayingService::class.java).apply {
                action = INTENT_ACTION_SERVICE_APP_LINK_ALARM_ON
                putExtra(INTENT_EXTRA_SERVICE_APP_LINK_ALARM_ID, alarmId)
            }
        context.startForegroundService(serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(context, AppLinkAlarmPlayingService::class.java).apply {
            action =
                AppLinkAlarmPlayingService.INTENT_ACTION_SERVICE_APP_LINK_ALARM_OFF
        }
        context.startForegroundService(serviceIntent)
    }
}