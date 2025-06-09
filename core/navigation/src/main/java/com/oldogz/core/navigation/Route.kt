package com.oldogz.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object AlarmHome : Route
}