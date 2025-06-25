package com.oldogz.core.admob

import android.app.Activity
import com.google.android.gms.ads.AdRequest

class FakeAdMobManager : AdMobManager {
    override val adRequest: AdRequest
        get() = TODO("Not yet implemented")

    override fun showInterstitialAlarmAd(activity: Activity) {
        TODO("Not yet implemented")
    }
}