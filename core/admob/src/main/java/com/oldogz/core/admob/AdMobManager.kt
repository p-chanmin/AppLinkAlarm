package com.oldogz.core.admob

import android.app.Activity
import com.google.android.gms.ads.AdRequest

interface AdMobManager {
    val adRequest: AdRequest
    fun showInterstitialAlarmAd(activity: Activity)
}