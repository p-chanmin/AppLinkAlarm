package com.oldogz.applinkalarm.feature.alarm.open

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldogz.applinkalarm.feature.alarm.model.OpenAppUiState
import com.oldogz.core.alarm.manager.AppLinkAlarmStateManager
import com.oldogz.core.billing.SubscriptionManager
import com.oldogz.core.data.AppLinkAlarmRepository
import com.oldogz.core.firebase.FirebaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DismissAlarmViewModel @Inject constructor(
    private val appLinkAlarmRepository: AppLinkAlarmRepository,
    private val firebaseManager: FirebaseManager,
    private val appLinkAlarmStateManager: AppLinkAlarmStateManager,
    private val subscriptionManager: SubscriptionManager,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    val hasPremium = subscriptionManager.subscriptionState

    private val _openAppUiState = MutableStateFlow(OpenAppUiState())
    val openAppUiState = _openAppUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        _openAppUiState.value
    )

    init {
        appLinkAlarmStateManager.currentAppLinkAlarmId.onEach { id ->
            id?.let { updateAppLinkAlarm(it) }
        }.launchIn(viewModelScope)
    }

    suspend fun updateAppLinkAlarm(id: Int) {
        try {
            val linkedAlarm = appLinkAlarmRepository.getAlarmById(id).first()
            _openAppUiState.update {
                it.copy(
                    id = linkedAlarm.id,
                    alarmName = linkedAlarm.alarmName,
                    alarmMessage = linkedAlarm.alarmMessage,
                    hour = linkedAlarm.hour,
                    minute = linkedAlarm.minute,
                    alarmMode = linkedAlarm.alarmMode,
                    periodOfDay = linkedAlarm.periodOfDay,
                    linkedAppPackage = linkedAlarm.linkedAppPackage
                )
            }
        } catch (e: Exception) {
            firebaseManager.reportNonFatalError(e)
            _errorFlow.emit(e)
        }
    }
}