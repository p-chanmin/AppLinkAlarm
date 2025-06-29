package com.oldogz.core.firebase

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

interface FirebaseManager {
    val firebaseAnalytics: FirebaseAnalytics
    val firebaseCrashlytics: FirebaseCrashlytics

    fun screenLogEvent(screenName: String, orientation: Int)
    fun reportNonFatalError(error: Throwable)
    fun logCrashlyticsMessage(message: String)
}