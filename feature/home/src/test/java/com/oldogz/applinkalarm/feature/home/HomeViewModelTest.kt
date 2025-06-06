package com.oldogz.applinkalarm.feature.home

import com.oldogz.core.testing.rule.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

internal class HomeViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    @Test
    fun `기본 테스트`() = runTest {
        assertEquals(1, 1)
    }
}