package com.oldogz.applinkalarm.feature.alarm.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldogz.applinkalarm.feature.alarm.model.AlarmHomeUiState
import com.oldogz.applinkalarm.feature.alarm.model.AppLinkAlarmUiState
import com.oldogz.core.data.AppLinkAlarmRepository
import com.oldogz.core.model.AppLinkAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmHomeViewModel @Inject constructor(
    private val appLinkAlarmRepository: AppLinkAlarmRepository,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _homeUiState = MutableStateFlow(AlarmHomeUiState())
    val homeUiState = _homeUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        _homeUiState.value
    )

    init {
        loadAlarm()
    }

    private fun loadAlarm() {
        appLinkAlarmRepository.alarms
            .onEach { alarms ->
                _homeUiState.update {
                    it.copy(
                        alarms = alarms.map { AppLinkAlarmUiState(appLinkAlarm = it) }
                            .toPersistentList()
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun updateAlarmActive(appLinkAlarm: AppLinkAlarm, active: Boolean) {
        viewModelScope.launch {
            appLinkAlarmRepository.updateAlarm(
                appLinkAlarm.copy(
                    active = active
                )
            )
        }
    }

    fun updateSelectedAlarmActive(active: Boolean) {
        viewModelScope.launch {
            _homeUiState.value.alarms
                .filter { it.selected }
                .map { it.appLinkAlarm }
                .forEach { appLinkAlarm ->
                    appLinkAlarmRepository.updateAlarm(
                        appLinkAlarm.copy(
                            active = active
                        )
                    )
                }
            updateSelectMode(false)
        }
    }

    fun deleteSelectedAlarm() {
        viewModelScope.launch {
            _homeUiState.value.alarms
                .filter { it.selected }
                .map { it.appLinkAlarm.id }
                .forEach { id ->
                    appLinkAlarmRepository.deleteAlarmById(id)
                }
            updateSelectMode(false)
        }
    }

    fun updateSelectMode(value: Boolean, id: Int? = null) {
        if (value && id != null) {
            selectAlarm(true, id)
        } else if (!value) {
            selectAllAlarm(false)
        }
        _homeUiState.update {
            it.copy(
                isSelectMode = value
            )
        }
    }

    fun selectAlarm(value: Boolean, id: Int) {
        val newAlarms = _homeUiState.value.alarms.map {
            if (it.appLinkAlarm.id == id) {
                it.copy(selected = value)
            } else {
                it
            }
        }.toPersistentList()

        _homeUiState.update {
            it.copy(
                alarms = newAlarms
            )
        }
    }

    fun selectAllAlarm(value: Boolean) {
        _homeUiState.update {
            it.copy(
                alarms = _homeUiState.value.alarms.map { it.copy(selected = value) }
                    .toPersistentList()
            )
        }
    }
}