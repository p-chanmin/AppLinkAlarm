package com.oldogz.applinkalarm.feature.main.model

import androidx.compose.runtime.Immutable
import java.time.ZonedDateTime

@Immutable
data class MainUiState(
    val rejectFlexibleUpdateDate: ZonedDateTime? = ZonedDateTime.now(),
)