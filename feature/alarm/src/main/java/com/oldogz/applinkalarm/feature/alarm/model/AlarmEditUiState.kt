package com.oldogz.applinkalarm.feature.alarm.model

import androidx.compose.runtime.Immutable
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.PeriodOfDay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class AlarmEditUiState(
    val id: Int? = null,
    val linkedAppPackage: String? = null,
    val hour: Int = 6,
    val minute: Int = 30,
    val periodOfDay: PeriodOfDay = PeriodOfDay.AM,
    val dayOfWeek: ImmutableList<DayOfWeek> = persistentListOf(),
    val alarmName: String = "",
    val message: String = "",
    val alarmMode: AlarmMode = AlarmMode.ONLY_NOTIFICATION,
    val vibrate: Boolean = true,
    val alarmSound: String? = null,
    val alarmVolume: Int = 80,
    val active: Boolean = true,
    val selectAppDialog: Boolean = false,
)

sealed interface AlarmEditUiEvent {

    data object AlarmEditComplete : AlarmEditUiEvent

    data class AlarmLoad(
        val hour: Int,
        val minute: Int,
        val periodOfDay: PeriodOfDay
    ) : AlarmEditUiEvent
}