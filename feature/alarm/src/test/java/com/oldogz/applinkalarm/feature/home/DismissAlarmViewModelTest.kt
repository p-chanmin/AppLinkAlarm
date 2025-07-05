package com.oldogz.applinkalarm.feature.home

import app.cash.turbine.test
import com.oldogz.applinkalarm.feature.alarm.open.DismissAlarmViewModel
import com.oldogz.core.billing.FakeSubscriptionManager
import com.oldogz.core.billing.SubscriptionManager
import com.oldogz.core.data.AppLinkAlarmRepository
import com.oldogz.core.firebase.FakeFirebaseManager
import com.oldogz.core.firebase.FirebaseManager
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.PeriodOfDay
import com.oldogz.core.testing.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

internal class DismissAlarmViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val appLinkAlarmRepository: AppLinkAlarmRepository = mockk(relaxed = true)
    private val firebaseManager: FirebaseManager = FakeFirebaseManager()
    private val subscriptionManager: SubscriptionManager = FakeSubscriptionManager()
    private lateinit var dismissAlarmViewModel: DismissAlarmViewModel

    @Test
    fun `알람 id를 통해 데이터를 확인할 수 있다`() = runTest {
        coEvery { appLinkAlarmRepository.getAlarmById(1) } returns flowOf(alarm)

        dismissAlarmViewModel = DismissAlarmViewModel(
            appLinkAlarmRepository,
            firebaseManager,
            subscriptionManager
        )

        dismissAlarmViewModel.updateAppLinkAlarm(1)

        dismissAlarmViewModel.openAppUiState.test {
            val uiState = awaitItem()

            assertEquals(alarm.id, uiState.id)
            assertEquals(alarm.alarmName, uiState.alarmName)
            assertEquals(alarm.alarmMessage, uiState.alarmMessage)
            assertEquals(alarm.hour, uiState.hour)
            assertEquals(alarm.minute, uiState.minute)
            assertEquals(alarm.alarmMode, uiState.alarmMode)
            assertEquals(alarm.periodOfDay, uiState.periodOfDay)
            assertEquals(alarm.linkedAppPackage, uiState.linkedAppPackage)
        }
    }

    companion object {
        val alarm = AppLinkAlarm(
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
    }
}