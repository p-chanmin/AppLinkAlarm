package com.oldogz.core.admob

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdMobManagerImpl(private val context: Context) : AdMobManager {

    override val adRequest = AdRequest.Builder().build()

    private var interstitialAlarmAd: InterstitialAd? = null

    init {
        loadInterstitialAlarmAd()
    }

    override fun showInterstitialAlarmAd(activity: Activity) {
        interstitialAlarmAd?.show(activity)
        loadInterstitialAlarmAd()
    }

    private fun loadInterstitialAlarmAd() {
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_ADS_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAlarmAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAlarmAd = ad
                }
            }
        )
    }
}