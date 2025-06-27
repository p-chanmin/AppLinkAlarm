package com.oldogz.core.navigation

import android.net.Uri
import androidx.core.net.toUri
import kotlinx.serialization.Serializable

const val DEEP_LINK_BASE_PATH = "applinkalarm://feature"

fun getDeepLinkOf(path: String): Uri {
    return listOf(DEEP_LINK_BASE_PATH, path).joinToString("/").toUri()
}

sealed interface Route {
    @Serializable
    data object AlarmHome : Route

    @Serializable
    data class AlarmEdit(val id: Int? = null) : Route

    @Serializable
    data object Setting : Route

    @Serializable
    data class OpenApp(val id: Int) : Route

    @Serializable
    data object OpenSource : Route
}