package com.oldogz.core.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class AppLinkAlarm(
    val id: Int = 0,
    val linkTarget: LinkTarget = LinkTarget.App(packageName = ""),
    val hour: Int = 0,
    val minute: Int = 0,
    val periodOfDay: PeriodOfDay = PeriodOfDay.AM,
    val dayOfWeek: List<DayOfWeek> = listOf(),
    val alarmName: String = "",
    val alarmMessage: String = "",
    val alarmMode: AlarmMode = AlarmMode.NOTIFICATION_ONLY,
    val vibrate: Boolean = true,
    val alarmSound: String? = null,
    val alarmVolume: Int = 80,
    val active: Boolean = true,
)

@OptIn(InternalSerializationApi::class)
@Serializable
sealed class LinkTarget {
    @Serializable
    data class App(val packageName: String) : LinkTarget()

    @Serializable
    data class Url(val urlString: String) : LinkTarget()
}