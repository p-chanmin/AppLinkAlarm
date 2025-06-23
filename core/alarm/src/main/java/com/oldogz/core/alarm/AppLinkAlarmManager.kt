package com.oldogz.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.PeriodOfDay
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLinkAlarmManager @Inject constructor(
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

    fun scheduleAlarm(alarm: AppLinkAlarm) {
        if (!alarm.active || alarm.dayOfWeek.isEmpty()) return

        val nextAlarmTime = calculateNextAlarmTime(alarm)

        val pendingIntent = createPendingIntent(alarm.id)

        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            nextAlarmTime,
            pendingIntent
        )
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    fun cancelAlarm(alarmId: Int) {
        alarmManager.cancel(createPendingIntent(alarmId))
    }

    private fun calculateNextAlarmTime(alarm: AppLinkAlarm): Long {
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault()
            timeInMillis = System.currentTimeMillis()

            val hour24 = (alarm.hour % 12) + if (alarm.periodOfDay == PeriodOfDay.PM) 12 else 0

            set(Calendar.HOUR_OF_DAY, hour24)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val todayDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val nextDayOfWeek =
            findNextDayOfWeek(alarm.dayOfWeek, todayDayOfWeek, calendar.timeInMillis)
        calendar.set(Calendar.DAY_OF_WEEK, nextDayOfWeek)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }

        return calendar.timeInMillis
    }

    private fun findNextDayOfWeek(
        dayOfWeekList: List<DayOfWeek>,
        todayDayOfWeek: Int,
        alarmTime: Long
    ): Int {
        val sortedDayOfWeek = dayOfWeekList.map { it.value }.sorted()
        val dayOfWeek = sortedDayOfWeek.find {
            it > todayDayOfWeek || (it == todayDayOfWeek && alarmTime > System.currentTimeMillis())
        }
        return dayOfWeek ?: sortedDayOfWeek.first()
    }

    private fun createPendingIntent(alarmId: Int): PendingIntent {
        val intent = Intent(context, AppLinkAlarmReceiver::class.java).apply {
            action = INTENT_ACTION_APP_LINK_ALARM
            putExtra(INTENT_EXTRA_APP_LINK_ALARM_ID, alarmId)
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
    }
}