package com.oldogz.applinkalarm.feature.alarm.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.oldogz.applinkalarm.feature.alarm.model.AlarmEditUiEvent
import com.oldogz.applinkalarm.feature.alarm.model.AlarmEditUiState
import com.oldogz.core.alarm.AppLinkAlarmManager
import com.oldogz.core.data.AppLinkAlarmRepository
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.PeriodOfDay
import com.oldogz.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val appLinkAlarmRepository: AppLinkAlarmRepository,
    private val appLinkAlarmManager: AppLinkAlarmManager,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _event = MutableSharedFlow<AlarmEditUiEvent>()
    val event get() = _event.asSharedFlow()

    private val _alarmEditUiState = MutableStateFlow(AlarmEditUiState())
    val alarmEditUiState = _alarmEditUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        _alarmEditUiState.value
    )

    init {
        val id = savedStateHandle.toRoute<Route.AlarmEdit>().id
        if (id != null) {
            viewModelScope.launch {
                try {
                    val appLinkAlarm = appLinkAlarmRepository.getAlarmById(id).first()
                    _alarmEditUiState.update {
                        it.copy(
                            id = id,
                            linkedAppPackage = appLinkAlarm.linkedAppPackage,
                            hour = appLinkAlarm.hour,
                            minute = appLinkAlarm.minute,
                            periodOfDay = appLinkAlarm.periodOfDay,
                            dayOfWeek = appLinkAlarm.dayOfWeek.toPersistentList(),
                            alarmName = appLinkAlarm.alarmName,
                            message = appLinkAlarm.alarmMessage,
                            alarmMode = appLinkAlarm.alarmMode,
                            vibrate = appLinkAlarm.vibrate,
                            alarmSound = appLinkAlarm.alarmSound,
                            alarmVolume = appLinkAlarm.alarmVolume,
                            active = appLinkAlarm.active
                        )
                    }
                    _event.emit(
                        AlarmEditUiEvent.AlarmLoad(
                            appLinkAlarm.hour,
                            appLinkAlarm.minute,
                            appLinkAlarm.periodOfDay
                        )
                    )
                } catch (e: Exception) {
                    _errorFlow.emit(e)
                }
            }
        }
    }

    fun updateLinkedAppPackage(linkedAppPackage: String) {
        _alarmEditUiState.update {
            it.copy(linkedAppPackage = linkedAppPackage)
        }
    }

    fun updateHour(hour: Int) {
        _alarmEditUiState.update {
            it.copy(hour = hour)
        }
    }

    fun updateMinute(minute: Int) {
        _alarmEditUiState.update {
            it.copy(minute = minute)
        }
    }

    fun updatePeriodOfDay(periodOfDay: PeriodOfDay) {
        _alarmEditUiState.update {
            it.copy(periodOfDay = periodOfDay)
        }
    }

    fun updateDayOfWeek(dayOfWeek: DayOfWeek) {
        _alarmEditUiState.update {
            if (dayOfWeek in it.dayOfWeek) {
                it.copy(dayOfWeek = it.dayOfWeek.toPersistentList().remove(dayOfWeek))
            } else {
                it.copy(dayOfWeek = it.dayOfWeek.toPersistentList().add(dayOfWeek))
            }
        }
    }

    fun updateAlarmName(value: String) {
        _alarmEditUiState.update {
            it.copy(alarmName = value)
        }
    }

    fun updateMessage(value: String) {
        _alarmEditUiState.update {
            it.copy(message = value)
        }
    }

    fun updateAlarmMode() {
        _alarmEditUiState.update {
            it.copy(
                alarmMode = if (it.alarmMode == AlarmMode.NOTIFICATION_ONLY) {
                    AlarmMode.STANDARD
                } else {
                    AlarmMode.NOTIFICATION_ONLY
                }
            )
        }
    }

    fun updateAlarmSound(uri: String) {
        _alarmEditUiState.update {
            it.copy(alarmSound = uri)
        }
    }

    fun updateVibrate(value: Boolean) {
        _alarmEditUiState.update {
            it.copy(vibrate = value)
        }
    }

    fun updateAlarmVolume(value: Float) {
        _alarmEditUiState.update {
            it.copy(alarmVolume = (value * 100).toInt())
        }
    }

    fun updateVisibleSelectAppDialog() {
        _alarmEditUiState.update {
            it.copy(visibleSelectAppDialog = !it.visibleSelectAppDialog)
        }
    }

    fun cancelExactAlarmPermissionDialog() {
        _alarmEditUiState.update {
            it.copy(visibleExactAlarmPermissionDialog = false)
        }
    }

    fun saveAlarm() {
        viewModelScope.launch {
            try {
                val id = _alarmEditUiState.value.id
                val linkedAppPackage = _alarmEditUiState.value.linkedAppPackage
                val hour = _alarmEditUiState.value.hour
                val minute = _alarmEditUiState.value.minute
                val periodOfDay = _alarmEditUiState.value.periodOfDay
                val dayOfWeek = _alarmEditUiState.value.dayOfWeek
                val alarmName = _alarmEditUiState.value.alarmName
                val message = _alarmEditUiState.value.message
                val alarmMode = _alarmEditUiState.value.alarmMode
                val vibrate = _alarmEditUiState.value.vibrate
                val alarmSound = _alarmEditUiState.value.alarmSound
                val alarmVolume = _alarmEditUiState.value.alarmVolume
                val active = _alarmEditUiState.value.active

                if (linkedAppPackage != null && dayOfWeek.isNotEmpty() && alarmName.isNotEmpty() && message.isNotEmpty()) {
                    val appLinkAlarm = AppLinkAlarm(
                        id = id ?: 0,
                        linkedAppPackage = linkedAppPackage,
                        hour = hour,
                        minute = minute,
                        periodOfDay = periodOfDay,
                        dayOfWeek = dayOfWeek,
                        alarmName = alarmName,
                        alarmMessage = message,
                        alarmMode = alarmMode,
                        vibrate = vibrate,
                        alarmSound = alarmSound,
                        alarmVolume = alarmVolume,
                        active = active
                    )
                    if (appLinkAlarmManager.checkScheduleExactAlarms()) {
                        if (id != null) {
                            appLinkAlarmRepository.updateAlarm(appLinkAlarm)
                            appLinkAlarmManager.scheduleAlarm(appLinkAlarm)
                        } else {
                            val alarmId = appLinkAlarmRepository.addAlarm(appLinkAlarm)
                            appLinkAlarmManager.scheduleAlarm(appLinkAlarm.copy(id = alarmId))
                        }
                        _event.emit(AlarmEditUiEvent.AlarmEditComplete)
                    } else {
                        _alarmEditUiState.update {
                            it.copy(visibleExactAlarmPermissionDialog = true)
                        }
                    }
                }
            } catch (e: Exception) {
                _errorFlow.emit(e)
            }
        }
    }
}