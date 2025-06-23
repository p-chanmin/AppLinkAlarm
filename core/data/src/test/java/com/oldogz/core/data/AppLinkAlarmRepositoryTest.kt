package com.oldogz.core.data

import app.cash.turbine.test
import com.oldogz.core.database.datasource.AppLinkAlarmDataSource
import com.oldogz.core.database.entity.AlarmEntity
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.PeriodOfDay
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class AppLinkAlarmRepositoryTest : StringSpec() {

    private lateinit var testDispatcher: TestDispatcher
    private val appLinkAlarmDataSource: AppLinkAlarmDataSource = mockk(relaxed = true)

    init {

        beforeSpec {
            testDispatcher = StandardTestDispatcher()
        }

        "alarms 조회 테스트" {
            runTest(testDispatcher) {

                // Given
                coEvery { appLinkAlarmDataSource.alarms } returns flowOf(
                    listOf(alarmEntity1, alarmEntity2)
                )

                val appLinkAlarmRepository = AppLinkAlarmRepository(appLinkAlarmDataSource)

                // When
                appLinkAlarmRepository.alarms.test {

                    // Then
                    val alarms = awaitItem()

                    alarms shouldBe listOf(alarm1, alarm2)

                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "alarms 조회 정렬 테스트" {
            runTest(testDispatcher) {

                // Given
                coEvery { appLinkAlarmDataSource.alarms } returns flowOf(
                    listOf(alarmEntity2, alarmEntity1)
                )

                val appLinkAlarmRepository = AppLinkAlarmRepository(appLinkAlarmDataSource)

                // When
                appLinkAlarmRepository.alarms.test {

                    // Then
                    val alarms = awaitItem()

                    alarms shouldBe listOf(alarm1, alarm2)

                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "getAlarmById 조회 테스트" {
            runTest(testDispatcher) {

                // Given
                coEvery { appLinkAlarmDataSource.getAlarmById(alarmEntity1.id) } returns flowOf(
                    alarmEntity1
                )

                val appLinkAlarmRepository = AppLinkAlarmRepository(appLinkAlarmDataSource)

                // When
                appLinkAlarmRepository.getAlarmById(alarmEntity1.id).test {

                    // Then
                    val alarm = awaitItem()

                    alarm shouldBe alarm1

                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "alarm 추가 테스트" {
            runTest(testDispatcher) {

                // Given
                val alarms = MutableStateFlow(listOf(alarmEntity1))

                coEvery { appLinkAlarmDataSource.alarms } returns alarms

                coEvery {
                    appLinkAlarmDataSource.addAlarm(
                        linkedAppPackage = alarm2.linkedAppPackage,
                        hour = alarm2.hour,
                        minute = alarm2.minute,
                        periodOfDay = Json.encodeToString(alarm2.periodOfDay),
                        dayOfWeek = Json.encodeToString(alarm2.dayOfWeek),
                        alarmName = alarm2.alarmName,
                        alarmMessage = alarm2.alarmMessage,
                        alarmMode = Json.encodeToString(alarm2.alarmMode),
                        vibrate = alarm2.vibrate,
                        alarmSound = alarm2.alarmSound,
                        alarmVolume = alarm2.alarmVolume,
                        active = alarm2.active,
                    )
                } answers {
                    alarms.value = listOf(alarmEntity1, alarmEntity2)
                    alarm2.id
                }

                val appLinkAlarmRepository = AppLinkAlarmRepository(appLinkAlarmDataSource)

                appLinkAlarmRepository.alarms.test {

                    var alarm = awaitItem()
                    alarm shouldBe listOf(alarm1)

                    // When
                    appLinkAlarmRepository.addAlarm(alarm2)

                    // Then
                    alarm = awaitItem()
                    alarm shouldBe listOf(alarm1, alarm2)

                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "alarm 수정 테스트" {
            runTest(testDispatcher) {

                // Given
                val alarms = MutableStateFlow(listOf(alarmEntity1, alarmEntity2))

                coEvery { appLinkAlarmDataSource.alarms } returns alarms

                coEvery {
                    appLinkAlarmDataSource.updateAlarm(
                        id = alarm2.id,
                        linkedAppPackage = alarm1.linkedAppPackage,
                        hour = alarm1.hour,
                        minute = alarm1.minute,
                        periodOfDay = Json.encodeToString(alarm1.periodOfDay),
                        dayOfWeek = Json.encodeToString(alarm1.dayOfWeek),
                        alarmName = alarm1.alarmName,
                        alarmMessage = alarm1.alarmMessage,
                        alarmMode = Json.encodeToString(alarm1.alarmMode),
                        vibrate = alarm1.vibrate,
                        alarmSound = alarm1.alarmSound,
                        alarmVolume = alarm1.alarmVolume,
                        active = alarm1.active,
                    )
                } answers {
                    alarms.value = listOf(alarmEntity1, alarmEntity1.copy(id = alarm2.id))
                }

                val appLinkAlarmRepository = AppLinkAlarmRepository(appLinkAlarmDataSource)

                appLinkAlarmRepository.alarms.test {

                    var alarm = awaitItem()
                    alarm shouldBe listOf(alarm1, alarm2)

                    // When
                    appLinkAlarmRepository.updateAlarm(alarm1.copy(id = alarm2.id))

                    // Then
                    alarm = awaitItem()
                    alarm shouldBe listOf(alarm1, alarm1.copy(id = alarm2.id))

                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "alarm 삭제 테스트" {
            runTest(testDispatcher) {

                // Given
                val alarms = MutableStateFlow(listOf(alarmEntity1, alarmEntity2))

                coEvery { appLinkAlarmDataSource.alarms } returns alarms

                coEvery {
                    appLinkAlarmDataSource.deleteAlarmById(alarm1.id)
                } answers {
                    alarms.value = listOf(alarmEntity2)
                }

                val appLinkAlarmRepository = AppLinkAlarmRepository(appLinkAlarmDataSource)

                appLinkAlarmRepository.alarms.test {

                    var alarm = awaitItem()
                    alarm shouldBe listOf(alarm1, alarm2)

                    // When
                    appLinkAlarmRepository.deleteAlarmById(alarm1.id)

                    // Then
                    alarm = awaitItem()
                    alarm shouldBe listOf(alarm2)

                    cancelAndConsumeRemainingEvents()
                }
            }
        }
    }

    companion object {
        val alarmEntity1 = AlarmEntity(
            id = 1,
            linkedAppPackage = "com.example.app.a",
            hour = 10,
            minute = 30,
            periodOfDay = "\"AM\"",
            dayOfWeek = "[\"THURSDAY\", \"FRIDAY\", \"SATURDAY\"]",
            alarmName = "Test Alarm 1",
            alarmMessage = "This is a test alarm 1",
            alarmMode = "\"STANDARD\"",
            vibrate = true,
            alarmSound = "android.resource://com.example.app/raw/alarm_sound",
            alarmVolume = 50,
            active = true
        )

        val alarm1 = AppLinkAlarm(
            id = alarmEntity1.id,
            linkedAppPackage = alarmEntity1.linkedAppPackage,
            hour = alarmEntity1.hour,
            minute = alarmEntity1.minute,
            periodOfDay = PeriodOfDay.AM,
            dayOfWeek = listOf(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY),
            alarmName = alarmEntity1.alarmName,
            alarmMessage = alarmEntity1.alarmMessage,
            alarmMode = AlarmMode.STANDARD,
            vibrate = alarmEntity1.vibrate,
            alarmSound = alarmEntity1.alarmSound,
            alarmVolume = alarmEntity1.alarmVolume,
            active = alarmEntity1.active
        )

        val alarmEntity2 = AlarmEntity(
            id = 2,
            linkedAppPackage = "com.example.app.b",
            hour = 8,
            minute = 10,
            periodOfDay = "\"PM\"",
            dayOfWeek = "[\"SUNDAY\", \"MONDAY\", \"TUESDAY\", \"WEDNESDAY\"]",
            alarmName = "Test Alarm 2",
            alarmMessage = "This is a test alarm 2",
            alarmMode = "\"NOTIFICATION_ONLY\"",
            vibrate = false,
            alarmSound = null,
            alarmVolume = 80,
            active = false
        )

        val alarm2 = AppLinkAlarm(
            id = alarmEntity2.id,
            linkedAppPackage = alarmEntity2.linkedAppPackage,
            hour = alarmEntity2.hour,
            minute = alarmEntity2.minute,
            periodOfDay = PeriodOfDay.PM,
            dayOfWeek = listOf(
                DayOfWeek.SUNDAY,
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY
            ),
            alarmName = alarmEntity2.alarmName,
            alarmMessage = alarmEntity2.alarmMessage,
            alarmMode = AlarmMode.NOTIFICATION_ONLY,
            vibrate = alarmEntity2.vibrate,
            alarmSound = alarmEntity2.alarmSound,
            alarmVolume = alarmEntity2.alarmVolume,
            active = alarmEntity2.active
        )
    }
}