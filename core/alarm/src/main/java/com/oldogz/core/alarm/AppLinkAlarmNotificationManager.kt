package com.oldogz.core.alarm

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLinkAlarmNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
}