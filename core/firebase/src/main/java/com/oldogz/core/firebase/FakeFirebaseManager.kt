package com.oldogz.core.firebase

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class FakeFirebaseManager : FirebaseManager {
    override val firebaseAnalytics: FirebaseAnalytics
        get() = TODO("Not yet implemented")
    override val firebaseCrashlytics: FirebaseCrashlytics
        get() = TODO("Not yet implemented")

    override fun reportNonFatalError(error: Throwable) {}

    override fun logCrashlyticsMessage(message: String) {}

    override fun screenLogEvent(screenName: String, orientation: Int) {}
}