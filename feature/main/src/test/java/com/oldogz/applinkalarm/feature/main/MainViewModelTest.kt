package com.oldogz.applinkalarm.feature.main

import app.cash.turbine.test
import com.oldogz.applinkalarm.feature.main.model.MainUiState
import com.oldogz.core.data.InAppServiceRepository
import com.oldogz.core.firebase.FakeFirebaseManager
import com.oldogz.core.firebase.FirebaseManager
import com.oldogz.core.testing.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.assertEquals

internal class MainViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val inAppServiceRepository: InAppServiceRepository = mockk(relaxed = true)
    private val firebaseManager: FirebaseManager = FakeFirebaseManager()
    private lateinit var mainViewModel: MainViewModel

    @Test
    fun `업데이트 거절 시간을 가져올 수 있다`() = runTest {

        // Given
        val result = "2025-05-02T11:09:35.545010Z"
        coEvery { inAppServiceRepository.rejectFlexibleUpdateDate } returns flowOf(result)
        mainViewModel = MainViewModel(inAppServiceRepository, firebaseManager)

        // When
        mainViewModel.mainUiState.test {

            // Then
            val uiState = awaitItem()

            val expected = MainUiState(
                rejectFlexibleUpdateDate = ZonedDateTime.parse(result)
            )
            assertEquals(expected, uiState)
        }
    }

    @Test
    fun `업데이트 거절 시간을 저장할 수 있다`() = runTest {

        // Given
        mockkStatic(ZonedDateTime::class)
        mockkStatic(ZoneId::class)

        val fixedDateTime = ZonedDateTime.parse("2025-06-02T11:09:35.545010Z")
        every { ZoneId.systemDefault() } returns ZoneId.of("Asia/Seoul")
        every { ZonedDateTime.now(ZoneOffset.UTC) } returns fixedDateTime

        val flow = MutableStateFlow("2025-05-02T11:09:35.545010Z")
        coEvery { inAppServiceRepository.rejectFlexibleUpdateDate } returns flow
        coEvery { inAppServiceRepository.setRejectFlexibleUpdateDate(fixedDateTime.toString()) } answers {
            flow.value = fixedDateTime.toString()
        }
        mainViewModel = MainViewModel(inAppServiceRepository, firebaseManager)

        // When
        mainViewModel.setRejectFlexibleUpdateDate()

        mainViewModel.mainUiState.test {

            // Then
            val uiState = awaitItem()

            val expected = MainUiState(
                rejectFlexibleUpdateDate = fixedDateTime
            )
            assertEquals(expected, uiState)
        }
    }
}