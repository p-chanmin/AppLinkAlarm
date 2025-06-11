package com.oldogz.applinkalarm.feature.alarm.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldogz.applinkalarm.feature.alarm.model.AlarmHomeUiState
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
                alarms.forEach {
                    println(it)
                }
                _homeUiState.update {
                    it.copy(
                        alarms = alarms.toPersistentList()
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
}