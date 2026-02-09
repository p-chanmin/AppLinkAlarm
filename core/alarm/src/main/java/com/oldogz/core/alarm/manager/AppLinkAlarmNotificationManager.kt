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
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.oldogz.core.alarm.R
import com.oldogz.core.alarm.manager.AppLinkAlarmScheduleManager.Companion.INTENT_ACTION_APP_LINK_ALARM_SKIP_CONFIRM
import com.oldogz.core.alarm.manager.AppLinkAlarmScheduleManager.Companion.INTENT_EXTRA_APP_LINK_ALARM_ID
import com.oldogz.core.alarm.receiver.AppLinkAlarmReceiver
import com.oldogz.core.alarm.service.AppLinkAlarmPlayingService
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.model.LinkTarget
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
            .setLargeIcon(createLargeIconBitmap(appLinkAlarm.linkTarget))
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

        val alarmStopIntent = Intent(context, AppLinkAlarmPlayingService::class.java).apply {
            action =
                AppLinkAlarmPlayingService.INTENT_ACTION_SERVICE_APP_LINK_ALARM_OFF
        }

        val deletePendingIntent = PendingIntent.getService(
            context,
            appLinkAlarm.id,
            alarmStopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val pendingIntent = createDefaultPendingIntent(appLinkAlarm, "home")

        return NotificationCompat.Builder(context, CHANNEL_ID_APP_LINK_ALARM)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(createLargeIconBitmap(appLinkAlarm.linkTarget))
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
            .setLargeIcon(createLargeIconBitmap(appLinkAlarm.linkTarget))
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

    fun createAlarmSkipNotification(
        appLinkAlarm: AppLinkAlarm
    ): Notification {

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(),
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmSkipIntent = Intent(context, AppLinkAlarmReceiver::class.java).apply {
            action = INTENT_ACTION_APP_LINK_ALARM_SKIP_CONFIRM
            putExtra(INTENT_EXTRA_APP_LINK_ALARM_ID, appLinkAlarm.id)
        }

        val skipPendingIntent = PendingIntent.getBroadcast(
            context,
            appLinkAlarm.id,
            alarmSkipIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val hourText = appLinkAlarm.hour.toString().padStart(2, '0')
        val minuteText = appLinkAlarm.minute.toString().padStart(2, '0')

        return NotificationCompat.Builder(context, CHANNEL_ID_APP_LINK_ALARM)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(createLargeIconBitmap(appLinkAlarm.linkTarget))
            .setContentTitle(context.getString(R.string.core_alarm_text_skip_title))
            .setContentText("$hourText:$minuteText ${appLinkAlarm.periodOfDay}, ${appLinkAlarm.alarmName}")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                R.drawable.alarm_off_24,
                context.getString(R.string.core_alarm_text_skip_action),
                skipPendingIntent
            )
            .setSilent(true)
            .build()
    }

    private fun createLargeIconBitmap(linkTarget: LinkTarget): Bitmap {
        val drawable = when (val target = linkTarget) {
            is LinkTarget.App -> {
                val packageManager = context.packageManager
                val appInfo = packageManager.getApplicationInfo(target.packageName, 0)
                packageManager.getApplicationIcon(appInfo)
            }

            is LinkTarget.Url -> {
                ContextCompat.getDrawable(context, R.drawable.outline_link_24)!!
            }
        }

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
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    private fun createLinkedAppPendingIntent(appLinkAlarm: AppLinkAlarm): PendingIntent {
        return when (val linkTarget = appLinkAlarm.linkTarget) {
            is LinkTarget.App -> {
                val intent =
                    context.packageManager.getLaunchIntentForPackage(linkTarget.packageName)
                PendingIntent.getActivity(
                    context,
                    appLinkAlarm.id,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            is LinkTarget.Url -> {
                val normalizeUrl = normalizeUrl(linkTarget.urlString)
                val intent = Intent(Intent.ACTION_VIEW, normalizeUrl.toUri())
                PendingIntent.getActivity(
                    context,
                    appLinkAlarm.id,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        }
    }

    private fun normalizeUrl(input: String): String {
        return if (!input.startsWith("http://") && !input.startsWith("https://")) {
            "https://$input"
        } else input
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