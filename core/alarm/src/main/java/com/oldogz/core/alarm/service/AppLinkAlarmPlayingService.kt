package com.oldogz.core.alarm.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.net.toUri
import com.google.firebase.analytics.logEvent
import com.oldogz.core.alarm.R
import com.oldogz.core.alarm.manager.AppLinkAlarmManager
import com.oldogz.core.alarm.manager.AppLinkAlarmNotificationManager
import com.oldogz.core.alarm.manager.AppLinkAlarmStateManager
import com.oldogz.core.billing.BuildConfig
import com.oldogz.core.billing.SubscriptionManager
import com.oldogz.core.data.AppLinkAlarmRepository
import com.oldogz.core.firebase.FirebaseManager
import com.oldogz.core.firebase.model.FA
import com.oldogz.core.model.AppLinkAlarm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@AndroidEntryPoint
class AppLinkAlarmPlayingService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    @Inject
    lateinit var appLinkAlarmRepository: AppLinkAlarmRepository

    @Inject
    lateinit var appLinkAlarmNotificationManager: AppLinkAlarmNotificationManager

    @Inject
    lateinit var appLinkAlarmStateManager: AppLinkAlarmStateManager

    @Inject
    lateinit var appLinkAlarmManager: AppLinkAlarmManager

    @Inject
    lateinit var firebaseManager: FirebaseManager

    @Inject
    lateinit var subscriptionManager: SubscriptionManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var audioManager: AudioManager
    private lateinit var vibrator: Vibrator

    private val mutex = Mutex()

    private var mediaPlayer: MediaPlayer? = null
    private var mediaVolumeBeforeAlarm: Int = 0

    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        ).build()

    override fun onCreate() {
        super.onCreate()
        appLinkAlarmNotificationManager.registerNotificationChannels()
        subscriptionManager.initialize()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        mediaVolumeBeforeAlarm = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            INTENT_ACTION_SERVICE_APP_LINK_ALARM_ON -> {
                val alarmId = intent.getIntExtra(INTENT_EXTRA_SERVICE_APP_LINK_ALARM_ID, -1)
                if (alarmId != -1) {
                    subscriptionManager.queryPurchases(BuildConfig.PREMIUM_MEMBERSHIP_PRODUCT_ID) { hasPremium ->
                        serviceScope.launch {
                            val appLinkAlarm = appLinkAlarmRepository.getAlarmById(alarmId).first()
                            mutex.withLock {
                                mediaPlayer?.let {
                                    mediaPlayer?.stop()
                                    mediaPlayer?.release()
                                    mediaPlayer = null
                                }
                                vibrator.cancel()
                                playAppLinkAlarm(appLinkAlarm, !hasPremium)
                            }
                        }
                    }
                }
            }

            INTENT_ACTION_SERVICE_APP_LINK_ALARM_OFF -> {
                firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_DISMISSED) {}
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }


    private suspend fun playAppLinkAlarm(appLinkAlarm: AppLinkAlarm, includeAds: Boolean) {
        startForeground(
            appLinkAlarm.id,
            appLinkAlarmNotificationManager.createNotification(appLinkAlarm, includeAds)
        )

        appLinkAlarmStateManager.currentAppLinkAlarmId.value?.let { id ->
            notifyMissedAlarm(id, includeAds)
        }

        appLinkAlarmStateManager.updateCurrentAppLinkAlarmId(appLinkAlarm.id)

        initMediaPlayer(appLinkAlarm)

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * appLinkAlarm.alarmVolume * 0.01f).toInt(),
            0
        )

        audioManager.requestAudioFocus(focusRequest)

        mediaPlayer?.start()
        mediaPlayer?.isLooping = true

        if (appLinkAlarm.vibrate) {
            val effect = VibrationEffect.createWaveform(longArrayOf(0, 1000, 1000), 0)
            vibrator.vibrate(effect)
        }
    }

    private fun initMediaPlayer(appLinkAlarm: AppLinkAlarm) {
        val sound = appLinkAlarm.alarmSound
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        mediaPlayer = runCatching {
            require(sound != null) { "Uri is null" }
            MediaPlayer().apply {
                val uri = sound.toUri()
                setDataSource(this@AppLinkAlarmPlayingService, uri)
                prepare()
            }
        }.getOrElse { _ ->
            serviceScope.launch {
                appLinkAlarmRepository.updateAlarm(appLinkAlarm.copy(alarmSound = null))
            }
            MediaPlayer.create(this@AppLinkAlarmPlayingService, R.raw.default_sound)
        }
    }

    private suspend fun notifyMissedAlarm(id: Int, includeAds: Boolean) {
        firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_MISSED) {}
        val appLinkAlarm = appLinkAlarmRepository.getAlarmById(id).first()
        val notification = appLinkAlarmNotificationManager.createMissedAlarmNotification(
            appLinkAlarm,
            includeAds
        )
        appLinkAlarmNotificationManager.notify(appLinkAlarm.id, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            mediaVolumeBeforeAlarm,
            0
        )

        vibrator.cancel()
        audioManager.abandonAudioFocusRequest(focusRequest)
        subscriptionManager.endConnection()
        appLinkAlarmStateManager.updateCurrentAppLinkAlarmId(null)
        stopForeground(STOP_FOREGROUND_REMOVE)
        serviceScope.cancel()
    }

    companion object {
        const val INTENT_ACTION_SERVICE_APP_LINK_ALARM_ON = "intentActionServiceAppLinkAlarmOn"
        const val INTENT_ACTION_SERVICE_APP_LINK_ALARM_OFF = "intentActionServiceAppLinkAlarmOff"
        const val INTENT_EXTRA_SERVICE_APP_LINK_ALARM_ID = "intentActionServiceAppLinkAlarmId"
    }
}