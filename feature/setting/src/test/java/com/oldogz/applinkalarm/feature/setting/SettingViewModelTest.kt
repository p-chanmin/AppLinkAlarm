package com.oldogz.applinkalarm.feature.setting

import app.cash.turbine.test
import com.oldogz.applinkalarm.feature.setting.home.SettingViewModel
import com.oldogz.applinkalarm.feature.setting.model.SettingUiState
import com.oldogz.core.alarm.manager.AppLinkAlarmScheduleManager
import com.oldogz.core.testing.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

internal class SettingViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val appLinkAlarmScheduleManager: AppLinkAlarmScheduleManager = mockk(relaxed = true)
    private lateinit var settingViewModel: SettingViewModel

    @Test
    fun `권한 상태를 업데이트 할 수 있다`() = runTest {

        // Given
        coEvery { appLinkAlarmScheduleManager.checkScheduleExactAlarms() } returns true
        settingViewModel = SettingViewModel(appLinkAlarmScheduleManager)

        // When
        settingViewModel.updatePermission(true)

        settingViewModel.settingUiState.test {

            // Then
            val uiState = awaitItem()

            val expected = SettingUiState(
                notificationPermission = true,
                exactAlarmPermission = true
            )

            assertEquals(expected, uiState)
        }
    }
}