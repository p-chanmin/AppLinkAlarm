package com.oldogz.core.firebase.model

object FA {

    object Event {
        const val ALARM_SAVE = "alarm_save"
        const val ALARM_ACTIVE_STATE_UPDATE = "alarm_active_state_update"

        const val ALARM_SELECT_MODE = "alarm_select_mode"
        const val ALARM_SELECT = "alarm_select"
        const val ALARM_DELETE = "alarm_delete"

        const val LINKED_APP_OPEN = "linked_app_open"

        const val ALARM_TRIGGERED = "alarm_triggered"
        const val ALARM_DISMISSED = "alarm_dismissed"
        const val ALARM_MISSED = "alarm_missed"
        const val LINKED_APP_NOT_FOUND = "linked_app_not_found"
    }

    object Param {

        object Key {
            const val HOUR = "hour"
            const val MINUTE = "minute"
            const val PERIOD_OF_DAY = "period_of_day"
            const val DAY_OF_WEEK = "day_of_week"
            const val ALARM_MODE = "alarm_mode"

            const val ACTIVE_STATE = "active_state"
            const val CHECKED_STATE = "checked_state"

            const val SELECT_TYPE = "select_type"
        }

        object Value {
            const val ALL = "all"
            const val SELECTED = "selected"
            const val ONE = "one"
        }
    }
}