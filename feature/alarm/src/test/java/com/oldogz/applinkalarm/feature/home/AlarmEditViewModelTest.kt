package com.oldogz.applinkalarm.feature.home

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.oldogz.applinkalarm.feature.alarm.edit.AlarmEditViewModel
import com.oldogz.core.alarm.AppLinkAlarmManager
import com.oldogz.core.data.AppLinkAlarmRepository
import com.oldogz.core.firebase.FakeFirebaseManager
import com.oldogz.core.firebase.FirebaseManager
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.PeriodOfDay
import com.oldogz.core.testing.rule.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

internal class AlarmEditViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val appLinkAlarmRepository: AppLinkAlarmRepository = mockk(relaxed = true)
    private val appLinkAlarmManager: AppLinkAlarmManager = mockk(relaxed = true)
    private val firebaseManager: FirebaseManager = FakeFirebaseManager()
    private lateinit var alarmEditViewModel: AlarmEditViewModel

    @Before
    fun setUp() {
        alarmEditViewModel =
            AlarmEditViewModel(
                SavedStateHandle(),
                appLinkAlarmRepository,
                appLinkAlarmManager,
                firebaseManager
            )
    }

    @Test
    fun `알람에 연결할 앱의 패키지명을 설정할 수 있다`() = runTest {

        // Given
        val linkedAppPackage = "com.example.app"

        // When
        alarmEditViewModel.updateLinkedAppPackage(linkedAppPackage)

        // Then
        alarmEditViewModel.alarmEditUiState.test {
            val uiState = awaitItem()
            assertEquals(linkedAppPackage, uiState.linkedAppPackage)
        }
    }

    @Test
    fun `알람의 시간을 설정할 수 있다`() = runTest {

        // Given
        val hour = 12
        val minute = 10
        val periodOfDay = PeriodOfDay.PM

        alarmEditViewModel.alarmEditUiState.test {
            awaitItem()

            // When
            alarmEditViewModel.updateHour(hour)

            // Then
            var uiState = awaitItem()
            assertEquals(hour, uiState.hour)

            // When
            alarmEditViewModel.updateMinute(minute)

            // Then
            uiState = awaitItem()
            assertEquals(minute, uiState.minute)

            // When
            alarmEditViewModel.updatePeriodOfDay(periodOfDay)

            // Then
            uiState = awaitItem()
            assertEquals(periodOfDay, uiState.periodOfDay)
        }
    }

    @Test
    fun `알람의 요일을 설정할 수 있다`() = runTest {

        // Given
        val dayOfWeek = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.SATURDAY)

        // When
        dayOfWeek.forEach {
            alarmEditViewModel.updateDayOfWeek(it)
        }

        alarmEditViewModel.alarmEditUiState.test {

            //Then
            var uiState = awaitItem()
            assertEquals(dayOfWeek, uiState.dayOfWeek)

            // When
            alarmEditViewModel.updateDayOfWeek(DayOfWeek.MONDAY)

            // Then
            uiState = awaitItem()
            assertEquals(listOf(DayOfWeek.TUESDAY, DayOfWeek.SATURDAY), uiState.dayOfWeek)
        }
    }

    @Test
    fun `알람의 이름을 설정할 수 있다`() = runTest {

        // Given
        val alarmName = "Test Alarm"

        // When
        alarmEditViewModel.updateAlarmName(alarmName)

        // Then
        alarmEditViewModel.alarmEditUiState.test {
            val uiState = awaitItem()
            assertEquals(alarmName, uiState.alarmName)
        }
    }

    @Test
    fun `알람의 내용을 설정할 수 있다`() = runTest {

        // Given
        val alarmMessage = "This is a test alarm"

        // When
        alarmEditViewModel.updateMessage(alarmMessage)

        // Then
        alarmEditViewModel.alarmEditUiState.test {
            val uiState = awaitItem()
            assertEquals(alarmMessage, uiState.message)
        }
    }

    @Test
    fun `알람의 모드를 설정할 수 있다`() = runTest {

        // Given
        // When
        alarmEditViewModel.alarmEditUiState.test {

            // Then
            var uiState = awaitItem()
            assertEquals(AlarmMode.NOTIFICATION_ONLY, uiState.alarmMode)

            // When
            alarmEditViewModel.updateAlarmMode()

            // Then
            uiState = awaitItem()
            assertEquals(AlarmMode.STANDARD, uiState.alarmMode)
        }
    }

    @Test
    fun `알람의 사운드를 설정할 수 있다`() = runTest {

        // Given
        val alarmSoundUri = "android.resource://com.example.app/raw/alarm_sound"

        // When
        alarmEditViewModel.updateAlarmSound(alarmSoundUri)

        alarmEditViewModel.alarmEditUiState.test {

            // Then
            val uiState = awaitItem()
            assertEquals(alarmSoundUri, uiState.alarmSound)
        }
    }

    @Test
    fun `알람의 진동 여부를 설정할 수 있다`() = runTest {

        // Given
        // When
        alarmEditViewModel.alarmEditUiState.test {

            // Then
            var uiState = awaitItem()
            assertEquals(true, uiState.vibrate)

            // When
            alarmEditViewModel.updateVibrate(false)

            // Then
            uiState = awaitItem()
            assertEquals(false, uiState.vibrate)
        }
    }

    @Test
    fun `알람의 볼륨을 설정할 수 있다`() = runTest {

        // Given
        val volume = 0.5f

        // When
        alarmEditViewModel.updateAlarmVolume(volume)

        alarmEditViewModel.alarmEditUiState.test {

            // Then
            var uiState = awaitItem()
            assertEquals(50, uiState.alarmVolume)
        }
    }
}