package com.oldogz.core.alarm

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLinkAlarmStateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var bindCount = 0
    private val _appLinkAlarmPlayingService: MutableStateFlow<AppLinkAlarmPlayingService?> =
        MutableStateFlow(null)
    val appLinkAlarmPlayingService get() = _appLinkAlarmPlayingService.asStateFlow()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val serviceBinder = binder as? AppLinkAlarmPlayingService.LocalBinder
            _appLinkAlarmPlayingService.value = serviceBinder?.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _appLinkAlarmPlayingService.value = null
            unbindService()
            bindService()
        }
    }

    fun bindService(): Boolean {
        val intent = Intent(context, AppLinkAlarmPlayingService::class.java)
        bindCount++
        return context.bindService(intent, connection, 0)
    }

    fun unbindService() {
        if (bindCount == 1) {
            context.unbindService(connection)
        }
        bindCount--
    }
}