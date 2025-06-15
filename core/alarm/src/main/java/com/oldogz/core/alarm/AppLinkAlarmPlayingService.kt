package com.oldogz.core.alarm

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppLinkAlarmPlayingService : Service() {

    private val binder: IBinder = LocalBinder()
    override fun onBind(intent: Intent): IBinder = binder
    inner class LocalBinder : Binder()


}