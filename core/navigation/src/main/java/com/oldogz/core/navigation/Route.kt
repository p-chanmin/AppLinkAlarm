package com.oldogz.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object AlarmHome : Route

    @Serializable
    data class AlarmEdit(val id: Int? = null) : Route

    @Serializable
    data object Setting : Route
}