package com.oldogz.applinkalarm.feature.alarm.util

fun normalizeUrl(input: String): String {
    return if (!input.startsWith("http://") && !input.startsWith("https://")) {
        "https://$input"
    } else input
}