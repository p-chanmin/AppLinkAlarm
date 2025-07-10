package com.oldogz.core.model

enum class AlarmMode {
    NOTIFICATION_ONLY,
    STANDARD;

    companion object {
        fun fromString(value: String?): AlarmMode {
            return when (value) {
                NOTIFICATION_ONLY.name -> NOTIFICATION_ONLY
                STANDARD.name -> STANDARD
                else -> NOTIFICATION_ONLY
            }
        }
    }
}