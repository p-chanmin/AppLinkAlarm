package com.oldogz.applinkalarm.feature.alarm.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldogz.applinkalarm.feature.alarm.model.AlarmEditUiState
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.DayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AlarmEditViewModel @Inject constructor() : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _alarmEditUiState = MutableStateFlow(AlarmEditUiState())
    val alarmEditUiState = _alarmEditUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        _alarmEditUiState.value
    )

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

    fun updatePeriodOfDay(periodOfDay: String) {
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
                alarmMode = if (it.alarmMode == AlarmMode.FLEXIBLE) {
                    AlarmMode.INSTANT
                } else {
                    AlarmMode.FLEXIBLE
                }
            )
        }
    }

    fun updateDirectAppLaunch(value: Boolean) {
        _alarmEditUiState.update {
            it.copy(directAppLaunch = value)
        }
    }

    fun updateVibrate(value: Boolean) {
        _alarmEditUiState.update {
            it.copy(vibrate = value)
        }
    }

    fun selectAppDialog() {
        _alarmEditUiState.update {
            it.copy(selectAppDialog = !it.selectAppDialog)
        }
    }

    fun saveAlarm() {
        println("alarmEditUiState : ${_alarmEditUiState.value}")
    }
}