package com.oldogz.applinkalarm.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldogz.applinkalarm.feature.main.model.MainUiState
import com.oldogz.core.data.InAppServiceRepository
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
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val inAppServiceRepository: InAppServiceRepository,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState = _mainUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        _mainUiState.value
    )

    init {
        loadServiceData()
    }

    private fun loadServiceData() {
        inAppServiceRepository.rejectFlexibleUpdateDate
            .onEach { rejectFlexibleUpdateDate ->
                _mainUiState.update {
                    it.copy(
                        rejectFlexibleUpdateDate = rejectFlexibleUpdateDate?.let {
                            ZonedDateTime.parse(it)
                        }
                    )
                }
            }.catch { throwable ->
                _errorFlow.emit(throwable)
            }.launchIn(viewModelScope)
    }

    fun setRejectFlexibleUpdateDate() {
        viewModelScope.launch {
            val currentDate = ZonedDateTime.now(ZoneOffset.UTC).toString()
            inAppServiceRepository.setRejectFlexibleUpdateDate(currentDate)
        }
    }
}