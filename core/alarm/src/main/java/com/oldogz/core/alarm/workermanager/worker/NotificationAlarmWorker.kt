package com.oldogz.core.alarm.workermanager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oldogz.core.alarm.manager.AppLinkAlarmNotificationManager
import com.oldogz.core.billing.BuildConfig
import com.oldogz.core.billing.SubscriptionManager
import com.oldogz.core.data.AppLinkAlarmRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class NotificationAlarmWorker @AssistedInject constructor(
    private val appLinkAlarmRepository: AppLinkAlarmRepository,
    private val subscriptionManager: SubscriptionManager,
    private val appLinkAlarmNotificationManager: AppLinkAlarmNotificationManager,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val alarmId = inputData.getInt(RESCHEDULE_ALARM_DATA_ID, -1)
            val appLinkAlarm = appLinkAlarmRepository.getAlarmById(alarmId).first()

            when {
                tags.contains(NOTIFICATION_ALARM_TAG) -> {
                    subscriptionManager.initialize {
                        subscriptionManager.queryPurchases(BuildConfig.PREMIUM_MEMBERSHIP_PRODUCT_ID) { hasPremium ->
                            appLinkAlarmNotificationManager.notify(
                                appLinkAlarm.id,
                                appLinkAlarmNotificationManager.createNotification(
                                    appLinkAlarm,
                                    !hasPremium
                                )
                            )
                        }
                    }
                }

                tags.contains(NOTIFICATION_NOT_FOUND_TAG) -> {
                    appLinkAlarmRepository.updateAlarm(appLinkAlarm.copy(active = false))
                    appLinkAlarmNotificationManager.notify(
                        appLinkAlarm.id,
                        appLinkAlarmNotificationManager.createNoLinkedAppNotification(
                            appLinkAlarm
                        )
                    )
                }
            }
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}

const val NOTIFICATION_ALARM_TAG = "notificationAlarmTag"
const val NOTIFICATION_NOT_FOUND_TAG = "notificationNotFoundTag"
const val NOTIFICATION_ALARM_DATA_ID = "rescheduleAlarmDataId"
