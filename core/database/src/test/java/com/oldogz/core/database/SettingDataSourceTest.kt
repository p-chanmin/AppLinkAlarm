package com.oldogz.core.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.oldogz.core.database.datasource.SettingDataSource
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.rules.TemporaryFolder

internal class SettingDataSourceTest : StringSpec() {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var tempFolder: TemporaryFolder
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var settingDataSource: SettingDataSource

    init {

        beforeSpec {
            testDispatcher = StandardTestDispatcher()
            tempFolder = TemporaryFolder.builder().assureDeletion().build().apply { create() }

            dataStore = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(testDispatcher),
                produceFile = { tempFolder.newFile("test.preferences_pb") }
            )
            settingDataSource = SettingDataSource(dataStore)
        }

        afterSpec {
            tempFolder.delete()
        }

        "rejectFlexibleUpdateDate 초기 상태 테스트" {
            runTest(testDispatcher) {
                // Given

                // When
                settingDataSource.rejectFlexibleUpdateDate.test {

                    // Then
                    awaitItem() shouldBe null
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "rejectFlexibleUpdateDate 저장 및 조회 테스트" {
            runTest(testDispatcher) {
                // Given
                val result = "2025-05-02T11:09:35.545010Z"
                settingDataSource.setRejectFlexibleUpdateDate(result)

                // When
                settingDataSource.rejectFlexibleUpdateDate.test {

                    // Then
                    awaitItem() shouldBe result
                    cancelAndConsumeRemainingEvents()
                }
            }
        }
    }
}