package com.oldogz.core.data

import app.cash.turbine.test
import com.oldogz.core.database.datasource.SettingDataSource
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest

internal class InAppServiceRepositoryTest : StringSpec() {

    private lateinit var testDispatcher: TestDispatcher
    private val settingDataSource: SettingDataSource = mockk(relaxed = true)

    init {
        beforeSpec {
            testDispatcher = StandardTestDispatcher()
        }

        "rejectFlexibleUpdateDate 조회 테스트" {
            runTest(testDispatcher) {
                // Given
                val result = "2025-05-02T11:09:35.545010Z"
                coEvery { settingDataSource.rejectFlexibleUpdateDate } returns flowOf(result)
                val inAppServiceRepository = InAppServiceRepository(settingDataSource)

                inAppServiceRepository.rejectFlexibleUpdateDate.test {

                    // When
                    val rejectFlexibleUpdateDate = awaitItem()

                    // Then
                    rejectFlexibleUpdateDate shouldBe result
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "rejectFlexibleUpdateDate 저장 테스트" {
            runTest(testDispatcher) {
                // Given
                val result = "2025-06-02T11:09:35.545010Z"
                val flow = MutableStateFlow("2025-05-02T11:09:35.545010Z")
                coEvery { settingDataSource.rejectFlexibleUpdateDate } returns flow
                coEvery { settingDataSource.setRejectFlexibleUpdateDate(result) } answers {
                    flow.value = result
                }
                val inAppServiceRepository = InAppServiceRepository(settingDataSource)

                inAppServiceRepository.setRejectFlexibleUpdateDate(result)
                inAppServiceRepository.rejectFlexibleUpdateDate.test {

                    // When
                    val rejectFlexibleUpdateDate = awaitItem()

                    // Then
                    rejectFlexibleUpdateDate shouldBe result
                    cancelAndConsumeRemainingEvents()
                }
            }
        }
    }
}