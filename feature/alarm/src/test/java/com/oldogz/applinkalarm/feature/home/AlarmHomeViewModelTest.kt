package com.oldogz.applinkalarm.feature.home

import app.cash.turbine.test
import com.oldogz.applinkalarm.feature.alarm.home.AlarmHomeViewModel
import com.oldogz.applinkalarm.feature.alarm.model.AlarmHomeUiEvent
import com.oldogz.applinkalarm.feature.alarm.model.AppLinkAlarmUiState
import com.oldogz.applinkalarm.feature.alarm.model.PermissionState
import com.oldogz.core.alarm.AppLinkAlarmManager
import com.oldogz.core.alarm.AppLinkAlarmStateManager
import com.oldogz.core.data.AppLinkAlarmRepository
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.PeriodOfDay
import com.oldogz.core.testing.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

internal class AlarmHomeViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val appLinkAlarmRepository: AppLinkAlarmRepository = mockk(relaxed = true)
    private val appLinkAlarmManager: AppLinkAlarmManager = mockk(relaxed = true)
    private val appLinkAlarmStateManager: AppLinkAlarmStateManager = mockk(relaxed = true)
    private lateinit var alarmHomeViewModel: AlarmHomeViewModel

    @Test
    fun `저장된 알람 데이터를 확인할 수 있다`() = runTest {

        // Given
        val alarms = listOf(alarm1, alarm2)

        coEvery { appLinkAlarmRepository.alarms } returns flowOf(alarms)
        alarmHomeViewModel = AlarmHomeViewModel(
            appLinkAlarmRepository,
            appLinkAlarmManager,
            appLinkAlarmStateManager
        )

        // When
        alarmHomeViewModel.homeUiState.test {

            // Then
            val uiState = awaitItem()

            val expected = alarms.map {
                AppLinkAlarmUiState(
                    selected = false,
                    appLinkAlarm = it
                )
            }.toPersistentList()

            assertEquals(alarms.size, uiState.alarms.size)
            assertEquals(expected, uiState.alarms)
        }
    }

    @Test
    fun `알람의 활성 상태를 변경할 수 있다`() = runTest {

        // Given
        val alarms = MutableStateFlow(listOf(alarm1))
        println(alarms.value)

        coEvery { appLinkAlarmRepository.alarms } returns alarms
        coEvery { appLinkAlarmRepository.updateAlarm(alarm1.copy(active = true)) } answers {
            alarms.value = listOf(alarm1.copy(active = true))
        }
        coEvery { appLinkAlarmRepository.updateAlarm(alarm1.copy(active = false)) } answers {
            alarms.value = listOf(alarm1.copy(active = false))
        }
        coEvery { appLinkAlarmManager.checkScheduleExactAlarms() } returns true

        alarmHomeViewModel = AlarmHomeViewModel(
            appLinkAlarmRepository,
            appLinkAlarmManager,
            appLinkAlarmStateManager
        )

        alarmHomeViewModel.homeUiState.test {

            var uiState = awaitItem()
            println(uiState)
            assertEquals(true, uiState.alarms.first().appLinkAlarm.active)

            // When
            alarmHomeViewModel.updateAlarmActive(alarm1, false)

            // Then
            uiState = awaitItem()
            assertEquals(false, uiState.alarms.first().appLinkAlarm.active)

            // When
            alarmHomeViewModel.updateAlarmActive(alarm1, true)

            // Then
            uiState = awaitItem()
            assertEquals(true, uiState.alarms.first().appLinkAlarm.active)
        }
    }

    @Test
    fun `알람을 선택하여 선택모드를 변경할 수 있다`() = runTest {

        // Given
        val alarms = listOf(alarm1, alarm2)
        coEvery { appLinkAlarmRepository.alarms } returns flowOf(alarms)
        alarmHomeViewModel = AlarmHomeViewModel(
            appLinkAlarmRepository,
            appLinkAlarmManager,
            appLinkAlarmStateManager
        )

        // When
        alarmHomeViewModel.updateSelectMode(true, alarm1.id)

        // Then
        alarmHomeViewModel.homeUiState.test {
            val uiState = awaitItem()

            val expected = listOf(
                AppLinkAlarmUiState(
                    selected = true,
                    appLinkAlarm = alarm1
                ),
                AppLinkAlarmUiState(
                    selected = false,
                    appLinkAlarm = alarm2
                )
            ).toPersistentList()

            assertEquals(true, uiState.isSelectMode)
            assertEquals(expected, uiState.alarms)
        }
    }

    @Test
    fun `알람을 단일 선택할 수 있다`() = runTest {
        // Given
        val alarms = listOf(alarm1, alarm2)
        coEvery { appLinkAlarmRepository.alarms } returns flowOf(alarms)
        alarmHomeViewModel = AlarmHomeViewModel(
            appLinkAlarmRepository,
            appLinkAlarmManager,
            appLinkAlarmStateManager
        )

        // When
        alarmHomeViewModel.selectAlarm(true, alarm2.id)

        // Then
        alarmHomeViewModel.homeUiState.test {
            val uiState = awaitItem()

            val expected = listOf(
                AppLinkAlarmUiState(
                    selected = false,
                    appLinkAlarm = alarm1
                ),
                AppLinkAlarmUiState(
                    selected = true,
                    appLinkAlarm = alarm2
                )
            ).toPersistentList()

            assertEquals(expected, uiState.alarms)
        }
    }

    @Test
    fun `알람을 전체 선택할 수 있다`() = runTest {
        // Given
        val alarms = listOf(alarm1, alarm2)
        coEvery { appLinkAlarmRepository.alarms } returns flowOf(alarms)
        alarmHomeViewModel = AlarmHomeViewModel(
            appLinkAlarmRepository,
            appLinkAlarmManager,
            appLinkAlarmStateManager
        )

        // When
        alarmHomeViewModel.selectAllAlarm(true)

        // Then
        alarmHomeViewModel.homeUiState.test {
            val uiState = awaitItem()

            val expected = listOf(
                AppLinkAlarmUiState(
                    selected = true,
                    appLinkAlarm = alarm1
                ),
                AppLinkAlarmUiState(
                    selected = true,
                    appLinkAlarm = alarm2
                )
            ).toPersistentList()

            assertEquals(expected, uiState.alarms)
        }
    }

    @Test
    fun `선택된 알람의 활성 상태를 변경할 수 있다`() = runTest {

        val alarms = MutableStateFlow(listOf(alarm1, alarm2))

        coEvery { appLinkAlarmRepository.alarms } returns alarms
        coEvery { appLinkAlarmRepository.updateAlarm(alarm1.copy(active = true)) } answers {
            alarms.value = listOf(alarm1.copy(active = true), alarm2)
        }
        coEvery { appLinkAlarmRepository.updateAlarm(alarm1.copy(active = false)) } answers {
            alarms.value = listOf(alarm1.copy(active = false), alarm2)
        }
        coEvery { appLinkAlarmManager.checkScheduleExactAlarms() } returns true

        alarmHomeViewModel = AlarmHomeViewModel(
            appLinkAlarmRepository,
            appLinkAlarmManager,
            appLinkAlarmStateManager
        )

        // Given
        alarmHomeViewModel.selectAlarm(true, alarm1.id)


        alarmHomeViewModel.homeUiState.test {
            var uiState = awaitItem()

            var expected = listOf(
                AppLinkAlarmUiState(
                    selected = true,
                    appLinkAlarm = alarm1
                ),
                AppLinkAlarmUiState(
                    selected = false,
                    appLinkAlarm = alarm2
                )
            ).toPersistentList()

            assertEquals(expected, uiState.alarms)

            // When
            alarmHomeViewModel.updateSelectedAlarmActive(false)

            // Then
            uiState = awaitItem()

            expected = listOf(
                AppLinkAlarmUiState(
                    selected = false,
                    appLinkAlarm = alarm1.copy(active = false)
                ),
                AppLinkAlarmUiState(
                    selected = false,
                    appLinkAlarm = alarm2
                )
            ).toPersistentList()

            assertEquals(expected, uiState.alarms)
        }
    }

    @Test
    fun `선택된 알람을 삭제할 수 있다`() = runTest {

        val alarms = MutableStateFlow(listOf(alarm1, alarm2))

        coEvery { appLinkAlarmRepository.alarms } returns alarms
        coEvery { appLinkAlarmRepository.deleteAlarmById(alarm1.id) } answers {
            alarms.value = listOf(alarm2)
        }

        alarmHomeViewModel = AlarmHomeViewModel(
            appLinkAlarmRepository,
            appLinkAlarmManager,
            appLinkAlarmStateManager
        )

        // Given
        alarmHomeViewModel.selectAlarm(true, alarm1.id)

        alarmHomeViewModel.homeUiState.test {
            var uiState = awaitItem()

            var expected = listOf(
                AppLinkAlarmUiState(
                    selected = true,
                    appLinkAlarm = alarm1
                ),
                AppLinkAlarmUiState(
                    selected = false,
                    appLinkAlarm = alarm2
                )
            ).toPersistentList()

            assertEquals(expected, uiState.alarms)

            // When
            alarmHomeViewModel.deleteSelectedAlarm()

            // Then
            uiState = awaitItem()

            expected = listOf(
                AppLinkAlarmUiState(
                    selected = false,
                    appLinkAlarm = alarm2
                )
            ).toPersistentList()

            assertEquals(expected, uiState.alarms)
        }
    }

    @Test
    fun `알림 권한 다이얼로그 상태를 변경할 수 있다`() = runTest {
        // Given
        alarmHomeViewModel = AlarmHomeViewModel(
            appLinkAlarmRepository,
            appLinkAlarmManager,
            appLinkAlarmStateManager
        )

        alarmHomeViewModel.homeUiState.test {
            var uiState = awaitItem()
            assertEquals(PermissionState.GRANTED, uiState.notificationPermissionState)
            assertEquals(false, uiState.visibleNotificationPermissionDialog)

            // When
            alarmHomeViewModel.updateNotificationPermissionState(
                PermissionState.DENIED,
                true
            )

            // Then
            uiState = awaitItem()
            println(uiState)
            assertEquals(PermissionState.DENIED, uiState.notificationPermissionState)
            assertEquals(true, uiState.visibleNotificationPermissionDialog)
        }
    }

    @Test
    fun `알람 및 리마인더 권한 다이얼로그 상태를 변경할 수 있다`() = runTest {
        // Given

        coEvery { appLinkAlarmManager.checkScheduleExactAlarms() } returns false

        alarmHomeViewModel = AlarmHomeViewModel(
            appLinkAlarmRepository,
            appLinkAlarmManager,
            appLinkAlarmStateManager
        )

        // When
        alarmHomeViewModel.updateAlarmActive(alarm1, true)

        // Then
        alarmHomeViewModel.homeUiState.test {
            var uiState = awaitItem()
            assertEquals(true, uiState.visibleExactAlarmPermissionDialog)

            // When
            alarmHomeViewModel.cancelExactAlarmPermissionDialog()

            // Then
            uiState = awaitItem()
            assertEquals(false, uiState.visibleExactAlarmPermissionDialog)
        }
    }

    @Test
    fun `알람을 해제할 수 있다`() = runTest {
        // Given
        alarmHomeViewModel = AlarmHomeViewModel(
            appLinkAlarmRepository,
            appLinkAlarmManager,
            appLinkAlarmStateManager
        )

        alarmHomeViewModel.event.test {

            // When
            alarmHomeViewModel.dismissAlarm(alarm1.linkedAppPackage)

            // Then
            val event = awaitItem()
            assertEquals(AlarmHomeUiEvent.LinkedAppOpen(alarm1.linkedAppPackage), event)
        }
    }

    companion object {
        val alarm1 = AppLinkAlarm(
            id = 1,
            linkedAppPackage = "com.example.app.a",
            hour = 12,
            minute = 0,
            periodOfDay = PeriodOfDay.AM,
            dayOfWeek = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
            alarmName = "테스트 알람1",
            alarmMessage = "테스트 메시지2",
            alarmMode = AlarmMode.STANDARD,
            vibrate = true,
            alarmSound = "alarm_sound_2.mp3",
            alarmVolume = 80,
            active = true,
        )

        val alarm2 = AppLinkAlarm(
            id = 2,
            linkedAppPackage = "com.example.app.b",
            hour = 1,
            minute = 0,
            periodOfDay = PeriodOfDay.PM,
            dayOfWeek = listOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
            alarmName = "테스트 알람2",
            alarmMessage = "테스트 메시지2",
            alarmMode = AlarmMode.ONLY_NOTIFICATION,
            vibrate = true,
            alarmSound = "alarm_sound_2.mp3",
            alarmVolume = 80,
            active = true,
        )
    }
}