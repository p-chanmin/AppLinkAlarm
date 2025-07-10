package com.oldogz.core.alarm.manager

import android.content.Context
import android.content.Intent
import com.google.firebase.analytics.logEvent
import com.oldogz.core.alarm.service.AppLinkAlarmPlayingService
import com.oldogz.core.alarm.service.AppLinkAlarmPlayingService.Companion.INTENT_ACTION_SERVICE_APP_LINK_ALARM_ON
import com.oldogz.core.alarm.service.AppLinkAlarmPlayingService.Companion.INTENT_EXTRA_SERVICE_APP_LINK_ALARM_ID
import com.oldogz.core.billing.BuildConfig
import com.oldogz.core.billing.SubscriptionManager
import com.oldogz.core.data.AppLinkAlarmRepository
import com.oldogz.core.firebase.FirebaseManager
import com.oldogz.core.firebase.model.FA
import com.oldogz.core.model.AlarmMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLinkAlarmStateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appLinkAlarmRepository: AppLinkAlarmRepository,
    private val appLinkAlarmManager: AppLinkAlarmManager,
    private val appLinkAlarmNotificationManager: AppLinkAlarmNotificationManager,
    private val firebaseManager: FirebaseManager,
    private val subscriptionManager: SubscriptionManager
) {
    private val _currentAppLinkAlarmId: MutableStateFlow<Int?> = MutableStateFlow(null)
    val currentAppLinkAlarmId = _currentAppLinkAlarmId.asStateFlow()

    fun updateCurrentAppLinkAlarmId(id: Int?) {
        _currentAppLinkAlarmId.value = id
    }

    fun startService(alarmId: Int, alarmMode: AlarmMode) {
        CoroutineScope(Dispatchers.IO).launch {
            val appLinkAlarm = appLinkAlarmRepository.getAlarmById(alarmId).first()

            if (isAppInstalled(context, appLinkAlarm.linkedAppPackage)) {
                firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_TRIGGERED) {
                    param(FA.Param.Key.ALARM_MODE, appLinkAlarm.alarmMode.name)
                }
                when (appLinkAlarm.alarmMode) {
                    AlarmMode.NOTIFICATION_ONLY -> {
                        subscriptionManager.initialize()
                        subscriptionManager.queryPurchases(BuildConfig.PREMIUM_MEMBERSHIP_PRODUCT_ID) { hasPremium ->
                            appLinkAlarmNotificationManager.notify(
                                appLinkAlarm.id,
                                appLinkAlarmNotificationManager.createNotification(
                                    appLinkAlarm,
                                    !hasPremium
                                )
                            )
                            subscriptionManager.endConnection()
                        }
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
                firebaseManager.firebaseAnalytics.logEvent(FA.Event.LINKED_APP_NOT_FOUND) {}
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

    fun stopService() {
        val serviceIntent = Intent(context, AppLinkAlarmPlayingService::class.java).apply {
            action =
                AppLinkAlarmPlayingService.INTENT_ACTION_SERVICE_APP_LINK_ALARM_OFF
        }
        context.startForegroundService(serviceIntent)
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