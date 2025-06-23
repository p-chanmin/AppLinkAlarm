package com.oldogz.core.database

import app.cash.turbine.test
import com.oldogz.core.database.dao.AlarmEntityDao
import com.oldogz.core.database.datasource.AppLinkAlarmDataSource
import com.oldogz.core.database.entity.AlarmEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest

internal class AppLinkAlarmDataSourceTest : StringSpec() {

    private lateinit var testDispatcher: TestDispatcher
    private val alarmEntityDao: AlarmEntityDao = mockk(relaxed = true)

    init {
        beforeSpec {
            testDispatcher = StandardTestDispatcher()
        }

        "alarms 조회 테스트" {
            runTest(testDispatcher) {

                // Given
                coEvery { alarmEntityDao.getAllAlarm() } returns flowOf(
                    listOf(alarmEntity1, alarmEntity2)
                )

                val appLinkAlarmDataSource = AppLinkAlarmDataSource(alarmEntityDao)

                // When
                appLinkAlarmDataSource.alarms.test {

                    // Then
                    val alarms = awaitItem()

                    alarms shouldBe listOf(alarmEntity1, alarmEntity2)

                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "getAlarmById 조회 테스트" {
            runTest(testDispatcher) {

                // Given
                coEvery { alarmEntityDao.getAlarmById(alarmEntity1.id) } returns flowOf(alarmEntity1)

                val appLinkAlarmDataSource = AppLinkAlarmDataSource(alarmEntityDao)

                // When
                appLinkAlarmDataSource.getAlarmById(alarmEntity1.id).test {

                    // Then
                    val alarms = awaitItem()

                    alarms shouldBe alarmEntity1

                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "alarm 추가 테스트" {
            runTest(testDispatcher) {

                // Given
                val alarmsFlow = MutableStateFlow(listOf(alarmEntity1))

                coEvery { alarmEntityDao.insert(alarmEntity2.copy(id = 0)) } answers {
                    alarmsFlow.value = listOf(alarmEntity1, alarmEntity2)
                    alarmEntity2.id.toLong()
                }

                coEvery { alarmEntityDao.getAllAlarm() } returns alarmsFlow

                val appLinkAlarmDataSource = AppLinkAlarmDataSource(alarmEntityDao)

                appLinkAlarmDataSource.alarms.test {

                    var alarm = awaitItem()
                    alarm shouldBe listOf(alarmEntity1)

                    // When
                    appLinkAlarmDataSource.addAlarm(
                        linkedAppPackage = alarmEntity2.linkedAppPackage,
                        hour = alarmEntity2.hour,
                        minute = alarmEntity2.minute,
                        periodOfDay = alarmEntity2.periodOfDay,
                        dayOfWeek = alarmEntity2.dayOfWeek,
                        alarmName = alarmEntity2.alarmName,
                        alarmMessage = alarmEntity2.alarmMessage,
                        alarmMode = alarmEntity2.alarmMode,
                        vibrate = alarmEntity2.vibrate,
                        alarmSound = alarmEntity2.alarmSound,
                        alarmVolume = alarmEntity2.alarmVolume,
                        active = alarmEntity2.active,
                    )

                    // Then
                    alarm = awaitItem()
                    alarm shouldBe listOf(alarmEntity1, alarmEntity2)

                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "alarm 수정 테스트" {
            runTest(testDispatcher) {

                // Given
                val alarmsFlow = MutableStateFlow(listOf(alarmEntity1, alarmEntity2))

                coEvery { alarmEntityDao.update(alarmEntity1.copy(id = alarmEntity2.id)) } answers {
                    alarmsFlow.value = listOf(alarmEntity1, alarmEntity1.copy(id = alarmEntity2.id))
                    alarmEntity2.id.toLong()
                }

                coEvery { alarmEntityDao.getAlarmById(alarmEntity2.id) } returns flowOf(alarmEntity2)

                coEvery { alarmEntityDao.getAllAlarm() } returns alarmsFlow

                val appLinkAlarmDataSource = AppLinkAlarmDataSource(alarmEntityDao)

                appLinkAlarmDataSource.alarms.test {

                    var alarm = awaitItem()
                    alarm shouldBe listOf(alarmEntity1, alarmEntity2)

                    // When
                    appLinkAlarmDataSource.updateAlarm(
                        id = alarmEntity2.id,
                        linkedAppPackage = alarmEntity1.linkedAppPackage,
                        hour = alarmEntity1.hour,
                        minute = alarmEntity1.minute,
                        periodOfDay = alarmEntity1.periodOfDay,
                        dayOfWeek = alarmEntity1.dayOfWeek,
                        alarmName = alarmEntity1.alarmName,
                        alarmMessage = alarmEntity1.alarmMessage,
                        alarmMode = alarmEntity1.alarmMode,
                        vibrate = alarmEntity1.vibrate,
                        alarmSound = alarmEntity1.alarmSound,
                        alarmVolume = alarmEntity1.alarmVolume,
                        active = alarmEntity1.active,
                    )

                    // Then
                    alarm = awaitItem()
                    alarm shouldBe listOf(alarmEntity1, alarmEntity1.copy(id = alarmEntity2.id))

                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "alarm 삭제 테스트" {
            runTest(testDispatcher) {

                // Given
                val alarmsFlow = MutableStateFlow(listOf(alarmEntity1, alarmEntity2))

                coEvery { alarmEntityDao.deleteAlarmById(alarmEntity2.id) } answers {
                    alarmsFlow.value = listOf(alarmEntity1)
                }

                coEvery { alarmEntityDao.getAllAlarm() } returns alarmsFlow

                val appLinkAlarmDataSource = AppLinkAlarmDataSource(alarmEntityDao)

                appLinkAlarmDataSource.alarms.test {

                    var alarm = awaitItem()
                    alarm shouldBe listOf(alarmEntity1, alarmEntity2)

                    // When
                    appLinkAlarmDataSource.deleteAlarmById(alarmEntity2.id)

                    // Then
                    alarm = awaitItem()
                    alarm shouldBe listOf(alarmEntity1)

                    cancelAndConsumeRemainingEvents()
                }
            }
        }
    }

    companion object {
        val alarmEntity1 = AlarmEntity(
            id = 1,
            linkedAppPackage = "com.example.app.a",
            hour = 12,
            minute = 0,
            periodOfDay = "\"PM\"",
            dayOfWeek = "[\"THURSDAY\", \"FRIDAY\", \"SATURDAY\"]",
            alarmName = "Test Alarm 1",
            alarmMessage = "This is a test alarm 1",
            alarmMode = "\"STANDARD\"",
            vibrate = true,
            alarmSound = "android.resource://com.example.app/raw/alarm_sound",
            alarmVolume = 50,
            active = true
        )

        val alarmEntity2 = AlarmEntity(
            id = 2,
            linkedAppPackage = "com.example.app.b",
            hour = 3,
            minute = 40,
            periodOfDay = "\"AM\"",
            dayOfWeek = "[\"SUNDAY\", \"MONDAY\", \"TUESDAY\", \"WEDNESDAY\"]",
            alarmName = "Test Alarm 2",
            alarmMessage = "This is a test alarm 2",
            alarmMode = "\"NOTIFICATION_ONLY\"",
            vibrate = false,
            alarmSound = null,
            alarmVolume = 80,
            active = false
        )
    }
}