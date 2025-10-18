package com.oldogz.core.alarm.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.oldogz.core.alarm.receiver.AppLinkAlarmReceiver
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.LinkTarget
import com.oldogz.core.model.PeriodOfDay
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLinkAlarmScheduleManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun checkScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun scheduleAlarm(alarm: AppLinkAlarm, isRescheduling: Boolean = false) {
        if (!alarm.active || alarm.dayOfWeek.isEmpty()) return

        val nextAlarmTime = calculateNextAlarmTime(alarm, isRescheduling)

        val pendingIntent =
            createPendingIntent(
                alarm.id,
                alarm.alarmMode.name,
                Json.encodeToString(alarm.linkTarget)
            )

        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            nextAlarmTime,
            pendingIntent
        )
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    fun cancelAlarm(alarmId: Int, alarmMode: String, linkTarget: LinkTarget) {
        alarmManager.cancel(
            createPendingIntent(
                alarmId,
                alarmMode,
                Json.encodeToString(linkTarget)
            )
        )
    }

    private fun calculateNextAlarmTime(alarm: AppLinkAlarm, isRescheduling: Boolean): Long {
        val marginMs = if (isRescheduling) 2000L else 0L
        val now = System.currentTimeMillis() + marginMs

        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault()
            timeInMillis = now

            val hour24 = (alarm.hour % 12) + if (alarm.periodOfDay == PeriodOfDay.PM) 12 else 0

            set(Calendar.HOUR_OF_DAY, hour24)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val todayDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val nextDayOfWeek =
            findNextDayOfWeek(alarm.dayOfWeek, todayDayOfWeek, calendar.timeInMillis, now)
        calendar.set(Calendar.DAY_OF_WEEK, nextDayOfWeek)


        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }

        return calendar.timeInMillis
    }

    private fun findNextDayOfWeek(
        dayOfWeekList: List<DayOfWeek>,
        todayDayOfWeek: Int,
        alarmTime: Long,
        now: Long,
    ): Int {
        val sortedDayOfWeek = dayOfWeekList.map { it.value }.sorted()
        val dayOfWeek = sortedDayOfWeek.find {
            it > todayDayOfWeek || (it == todayDayOfWeek && alarmTime > now)
        }
        return dayOfWeek ?: sortedDayOfWeek.first()
    }

    private fun createPendingIntent(
        alarmId: Int,
        alarmMode: String,
        linkTarget: String
    ): PendingIntent {
        val intent = Intent(context, AppLinkAlarmReceiver::class.java).apply {
            action = INTENT_ACTION_APP_LINK_ALARM
            putExtra(INTENT_EXTRA_APP_LINK_ALARM_ID, alarmId)
            putExtra(INTENT_EXTRA_APP_LINK_ALARM_MODE, alarmMode)
            putExtra(INTENT_EXTRA_APP_LINK_TARGET, linkTarget)
        }

        return PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    companion object {
        const val INTENT_ACTION_APP_LINK_ALARM = "intentActionAppLinkAlarm"
        const val INTENT_EXTRA_APP_LINK_ALARM_ID = "intentExtraAppLinkAlarmId"
        const val INTENT_EXTRA_APP_LINK_ALARM_MODE = "intentExtraAppLinkAlarmMode"
        const val INTENT_EXTRA_APP_LINK_TARGET = "intentExtraAppLinkTarget"
    }
}