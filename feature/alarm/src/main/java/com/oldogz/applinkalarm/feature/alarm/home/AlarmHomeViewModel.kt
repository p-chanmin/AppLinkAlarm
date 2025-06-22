package com.oldogz.applinkalarm.feature.alarm.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldogz.applinkalarm.feature.alarm.model.AlarmHomeUiEvent
import com.oldogz.applinkalarm.feature.alarm.model.AlarmHomeUiState
import com.oldogz.applinkalarm.feature.alarm.model.AppLinkAlarmUiState
import com.oldogz.applinkalarm.feature.alarm.model.PermissionState
import com.oldogz.core.alarm.AppLinkAlarmManager
import com.oldogz.core.alarm.AppLinkAlarmPlayingService
import com.oldogz.core.alarm.AppLinkAlarmStateManager
import com.oldogz.core.data.AppLinkAlarmRepository
import com.oldogz.core.model.AppLinkAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmHomeViewModel @Inject constructor(
    private val appLinkAlarmRepository: AppLinkAlarmRepository,
    private val appLinkAlarmManager: AppLinkAlarmManager,
    private val appLinkAlarmStateManager: AppLinkAlarmStateManager,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _service = MutableStateFlow<AppLinkAlarmPlayingService?>(null)
    val service get() = _service.asStateFlow()

    private val _homeUiState = MutableStateFlow(AlarmHomeUiState())
    val homeUiState = _homeUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        _homeUiState.value
    )

    private val _event = MutableSharedFlow<AlarmHomeUiEvent>()
    val event get() = _event.asSharedFlow()

    init {
        appLinkAlarmStateManager.appLinkAlarmPlayingService
            .onStart {
                appLinkAlarmStateManager.bindService()
            }.onEach { service ->
                _service.value = service
            }.launchIn(viewModelScope)

        loadAlarm()
    }

    private fun loadAlarm() {
        appLinkAlarmRepository.alarms
            .onStart {
                if (!appLinkAlarmManager.checkScheduleExactAlarms()) {
                    val alarms = appLinkAlarmRepository.alarms.first()
                    alarms.forEach { alarm ->
                        appLinkAlarmRepository.updateAlarm(
                            alarm.copy(active = false)
                        )
                    }
                }
            }.onEach { alarms ->
                _homeUiState.update {
                    it.copy(
                        alarms = alarms.map { AppLinkAlarmUiState(appLinkAlarm = it) }
                            .toPersistentList()
                    )
                }
            }.catch {
                _errorFlow.emit(it)
            }.launchIn(viewModelScope)
    }

    fun updateAlarmActive(appLinkAlarm: AppLinkAlarm, active: Boolean) {
        viewModelScope.launch {
            try {
                if (appLinkAlarmManager.checkScheduleExactAlarms() || !active) {
                    if (active) {
                        appLinkAlarmManager.scheduleAlarm(appLinkAlarm.copy(active = true))
                    } else {
                        appLinkAlarmManager.cancelAlarm(appLinkAlarm.id)
                    }
                    appLinkAlarmRepository.updateAlarm(
                        appLinkAlarm.copy(
                            active = active
                        )
                    )
                } else {
                    _homeUiState.update {
                        it.copy(
                            visibleExactAlarmPermissionDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                _errorFlow.emit(e)
            }
        }
    }

    fun updateSelectedAlarmActive(active: Boolean) {
        viewModelScope.launch {
            try {
                if (appLinkAlarmManager.checkScheduleExactAlarms() || !active) {
                    _homeUiState.value.alarms
                        .filter { it.selected }
                        .map { it.appLinkAlarm }
                        .forEach { appLinkAlarm ->
                            if (active) {
                                appLinkAlarmManager.scheduleAlarm(appLinkAlarm.copy(active = true))
                            } else {
                                appLinkAlarmManager.cancelAlarm(appLinkAlarm.id)
                            }
                            appLinkAlarmRepository.updateAlarm(
                                appLinkAlarm.copy(
                                    active = active
                                )
                            )
                        }
                    updateSelectMode(false)
                } else {
                    _homeUiState.update {
                        it.copy(
                            visibleExactAlarmPermissionDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                _errorFlow.emit(e)
            }
        }
    }

    fun deleteSelectedAlarm() {
        viewModelScope.launch {
            try {
                _homeUiState.value.alarms
                    .filter { it.selected }
                    .map { it.appLinkAlarm.id }
                    .forEach { id ->
                        appLinkAlarmManager.cancelAlarm(id)
                        appLinkAlarmRepository.deleteAlarmById(id)
                    }
                updateSelectMode(false)
            } catch (e: Exception) {
                _errorFlow.emit(e)
            }
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

    fun updateNotificationPermissionState(permissionState: PermissionState, dialogState: Boolean) {
        _homeUiState.update {
            it.copy(
                notificationPermissionState = permissionState,
                visibleNotificationPermissionDialog = dialogState
            )
        }
    }

    fun cancelExactAlarmPermissionDialog() {
        _homeUiState.update {
            it.copy(visibleExactAlarmPermissionDialog = false)
        }
    }

    fun dismissAlarm(linkedAppPackage: String) {
        viewModelScope.launch {
            _service.value?.stopSelf()
            _event.emit(AlarmHomeUiEvent.LinkedAppOpen(linkedAppPackage))
        }
    }

    override fun onCleared() {
        super.onCleared()
        appLinkAlarmStateManager.unbindService()
    }
}