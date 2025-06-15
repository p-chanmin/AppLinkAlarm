package com.oldogz.applinkalarm.feature.alarm.open

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.oldogz.applinkalarm.feature.alarm.model.OpenAppUiState
import com.oldogz.core.data.AppLinkAlarmRepository
import com.oldogz.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OpenAppViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val appLinkAlarmRepository: AppLinkAlarmRepository
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _openAppUiState = MutableStateFlow(OpenAppUiState())
    val openAppUiState = _openAppUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        _openAppUiState.value
    )

    init {
        val id = savedStateHandle.toRoute<Route.OpenApp>().id
        appLinkAlarmRepository.getAlarmById(id)
            .onEach { linkedAlarm ->
                _openAppUiState.update {
                    it.copy(
                        id = id,
                        alarmName = linkedAlarm.alarmName,
                        alarmMessage = linkedAlarm.alarmMessage,
                        hour = linkedAlarm.hour,
                        minute = linkedAlarm.minute,
                        periodOfDay = linkedAlarm.periodOfDay,
                        linkedAppPackage = linkedAlarm.linkedAppPackage
                    )
                }
            }.catch { e ->
                _errorFlow.emit(e)
            }.launchIn(viewModelScope)
    }
}