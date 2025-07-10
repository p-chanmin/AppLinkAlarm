package com.oldogz.core.alarm.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.graphics.createBitmap
import com.oldogz.core.alarm.service.AppLinkAlarmPlayingService
import com.oldogz.core.alarm.R
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.navigation.getDeepLinkOf
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLinkAlarmNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        registerNotificationChannels()
    }

    fun registerNotificationChannels() {
        createNotificationChannel(
            R.string.core_alarm_notification_channel_name,
            R.string.core_alarm_notification_channel_description,
            CHANNEL_ID_APP_LINK_ALARM,
            NotificationManager.IMPORTANCE_HIGH
        )
    }

    fun notify(id: Int, notification: Notification) {
        notificationManager.notify(id, notification)
    }

    fun cancel(id: Int) {
        notificationManager.cancel(id)
    }

    fun createNotification(appLinkAlarm: AppLinkAlarm, includeAds: Boolean): Notification {
        return when (appLinkAlarm.alarmMode) {
            AlarmMode.NOTIFICATION_ONLY -> createNotificationOnly(appLinkAlarm, includeAds)
            AlarmMode.STANDARD -> createNotificationStandard(appLinkAlarm)
        }
    }

    private fun createNotificationOnly(
        appLinkAlarm: AppLinkAlarm,
        includeAds: Boolean
    ): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID_APP_LINK_ALARM)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(createAppIconBitmap(appLinkAlarm.linkedAppPackage))
            .setContentTitle(appLinkAlarm.alarmName)
            .setContentText(appLinkAlarm.alarmMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(appLinkAlarm.alarmMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(createPendingIntent(appLinkAlarm, includeAds))
            .setAutoCancel(true)
            .build()
    }

    private fun createNotificationStandard(
        appLinkAlarm: AppLinkAlarm,
    ): Notification {

        val alarmStopIntent =
            Intent(context, AppLinkAlarmPlayingService::class.java).apply {
                action =
                    AppLinkAlarmPlayingService.INTENT_ACTION_SERVICE_APP_LINK_ALARM_OFF
            }

        val deletePendingIntent = PendingIntent.getService(
            context,
            appLinkAlarm.id,
            alarmStopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        val pendingIntent = createDefaultPendingIntent(appLinkAlarm, "home")

        return NotificationCompat.Builder(context, CHANNEL_ID_APP_LINK_ALARM)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(createAppIconBitmap(appLinkAlarm.linkedAppPackage))
            .setContentTitle(appLinkAlarm.alarmName)
            .setContentText(context.getString(R.string.core_alarm_text_click_to_dismiss_alarm, ""))
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    context.getString(
                        R.string.core_alarm_text_click_to_dismiss_alarm,
                        appLinkAlarm.alarmMessage
                    )
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(deletePendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .setOngoing(true)
            .build()
    }

    fun createMissedAlarmNotification(
        appLinkAlarm: AppLinkAlarm,
        includeAds: Boolean
    ): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID_APP_LINK_ALARM)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(createAppIconBitmap(appLinkAlarm.linkedAppPackage))
            .setContentTitle(
                context.getString(
                    R.string.core_alarm_text_missed_alarm,
                    appLinkAlarm.alarmName
                )
            )
            .setContentText(appLinkAlarm.alarmMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(appLinkAlarm.alarmMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(createPendingIntent(appLinkAlarm, includeAds))
            .setAutoCancel(true)
            .build()
    }

    fun createNoLinkedAppNotification(
        appLinkAlarm: AppLinkAlarm
    ): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID_APP_LINK_ALARM)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(
                context.getString(
                    R.string.core_alarm_text_no_linked_apps_found,
                    appLinkAlarm.alarmName
                )
            )
            .setContentText(context.getString(R.string.core_alarm_text_no_linked_apps_content_text))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.core_alarm_text_no_linked_apps_content_text))
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(createDefaultPendingIntent(appLinkAlarm, "home"))
            .setAutoCancel(true)
            .build()
    }

    private fun createAppIconBitmap(linkedAppPackage: String): Bitmap {
        val packageManager = context.packageManager
        val appInfo = packageManager.getApplicationInfo(linkedAppPackage, 0)
        val drawable = packageManager.getApplicationIcon(appInfo)

        val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
        val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun createPendingIntent(
        appLinkAlarm: AppLinkAlarm,
        includeAds: Boolean
    ): PendingIntent {
        return when (includeAds) {
            true -> createDefaultPendingIntent(appLinkAlarm, "open/${appLinkAlarm.id}")

            false -> createLinkedAppPendingIntent(appLinkAlarm)
        }
    }

    private fun createDefaultPendingIntent(
        appLinkAlarm: AppLinkAlarm,
        path: String
    ): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, getDeepLinkOf(path))
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(
                appLinkAlarm.id,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
    }

    private fun createLinkedAppPendingIntent(appLinkAlarm: AppLinkAlarm): PendingIntent {
        val intent = context.packageManager.getLaunchIntentForPackage(appLinkAlarm.linkedAppPackage)
        return PendingIntent.getActivity(
            context,
            appLinkAlarm.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private fun createNotificationChannel(
        @StringRes notificationNameResId: Int,
        @StringRes notificationDescriptionResId: Int,
        channelId: String,
        importance: Int
    ) {
        val name = context.getString(notificationNameResId)
        val descriptionText = context.getString(notificationDescriptionResId)
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID_APP_LINK_ALARM = "channelIdAppLinkAlarm"
    }
}