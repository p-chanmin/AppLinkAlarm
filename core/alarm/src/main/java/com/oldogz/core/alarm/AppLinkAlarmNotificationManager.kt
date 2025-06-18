package com.oldogz.core.alarm

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
import androidx.core.graphics.createBitmap
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
        println("registerNotificationChannels")
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
            AlarmMode.ONLY_NOTIFICATION -> createNotificationOnly(appLinkAlarm, includeAds)
            AlarmMode.STANDARD -> createNotificationStandard(appLinkAlarm, includeAds)
        }
    }

    private fun createNotificationOnly(
        appLinkAlarm: AppLinkAlarm,
        includeAds: Boolean
    ): Notification {
        return Notification.Builder(context, CHANNEL_ID_APP_LINK_ALARM)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(createAppIconBitmap(appLinkAlarm.linkedAppPackage))
            .setContentTitle(appLinkAlarm.alarmName)
            .setContentText(appLinkAlarm.alarmMessage)
            .setStyle(Notification.BigTextStyle().bigText(appLinkAlarm.alarmMessage))
            .setContentIntent(createPendingIntent(appLinkAlarm, includeAds))
            .setDeleteIntent(createPendingIntent(appLinkAlarm, includeAds))
            .setAutoCancel(true)
            .build()
    }

    private fun createNotificationStandard(
        appLinkAlarm: AppLinkAlarm,
        includeAds: Boolean
    ): Notification {
        return Notification.Builder(context, CHANNEL_ID_APP_LINK_ALARM)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(createAppIconBitmap(appLinkAlarm.linkedAppPackage))
            .setContentTitle(appLinkAlarm.alarmName)
            .setContentText(appLinkAlarm.alarmMessage)
            .setStyle(Notification.BigTextStyle().bigText(appLinkAlarm.alarmMessage))
            .setContentIntent(createPendingIntent(appLinkAlarm, includeAds))
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

    private fun createShowIntent(appLinkAlarm: AppLinkAlarm, includeAds: Boolean): Intent? {
        return when (includeAds) {
            true -> Intent(Intent.ACTION_VIEW, getDeepLinkOf("open/${appLinkAlarm.id}"))
            false -> context.packageManager.getLaunchIntentForPackage(appLinkAlarm.linkedAppPackage)
        }
    }

    private fun createPendingIntent(
        appLinkAlarm: AppLinkAlarm,
        includeAds: Boolean
    ): PendingIntent {

        val intent = createShowIntent(appLinkAlarm, includeAds)

        return when (includeAds) {
            true -> {
                TaskStackBuilder.create(context).run {
                    addNextIntentWithParentStack(intent)
                    getPendingIntent(
                        appLinkAlarm.id,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }
            }

            false -> {
                PendingIntent.getActivity(
                    context,
                    appLinkAlarm.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        }
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