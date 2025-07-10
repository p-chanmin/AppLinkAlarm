package com.oldogz.applinkalarm

import android.app.Application
import androidx.work.Configuration
import com.oldogz.core.alarm.workermanager.factory.WrapperWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AppLinkAlarmApp() : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: WrapperWorkerFactory

    override val workManagerConfiguration: Configuration get() = Configuration.Builder()
        .setMinimumLoggingLevel(android.util.Log.DEBUG)
        .setWorkerFactory(workerFactory)
        .build()
}