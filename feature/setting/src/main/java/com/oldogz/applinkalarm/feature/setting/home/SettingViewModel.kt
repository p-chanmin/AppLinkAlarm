package com.oldogz.applinkalarm.feature.setting.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldogz.applinkalarm.feature.setting.model.SettingUiState
import com.oldogz.core.alarm.manager.AppLinkAlarmScheduleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val appLinkAlarmScheduleManager: AppLinkAlarmScheduleManager,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _settingUiState = MutableStateFlow(SettingUiState())
    val settingUiState = _settingUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        _settingUiState.value
    )

    fun updatePermission(notificationPermission: Boolean) {
        _settingUiState.update {
            it.copy(
                notificationPermission = notificationPermission,
                exactAlarmPermission = appLinkAlarmScheduleManager.checkScheduleExactAlarms()
            )
        }
    }
}