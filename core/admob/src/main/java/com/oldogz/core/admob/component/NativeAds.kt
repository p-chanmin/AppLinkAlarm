import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.oldogz.core.admob.BuildConfig
import com.oldogz.core.admob.databinding.SmallNativeAdsViewBinding

@Composable
fun SmallNativeAd(
    modifier: Modifier = Modifier
) {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    val context = LocalContext.current
    var isDisposed by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        loadNativeAd(
            context = context,
            onAdLoaded = { ad ->
                if (!isDisposed) {
                    nativeAd = ad
                } else {
                    ad.destroy()
                }
            },
        )

        onDispose {
            isDisposed = true
            nativeAd?.destroy()
            nativeAd = null
            println("Native ad was destroyed.")
        }
    }

    nativeAd?.let { ad -> NativeAds(ad, modifier) }
}

@Composable
private fun NativeAds(
    nativeAd: NativeAd,
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    AndroidViewBinding(
        modifier = modifier,
        factory = SmallNativeAdsViewBinding::inflate
    ) {
        val adView = root.also { view ->
            view.headlineView = this.adHeadline
            view.imageView = this.adAppIcon
            view.callToActionView = this.adCallToAction
            view.starRatingView = this.adStars
            view.iconView = this.adAppIcon
            view.mediaView = this.adMedia
        }

        this.container.setBackgroundColor(backgroundColor)

        nativeAd.icon?.let {
            this.adAppIcon.setImageDrawable(it.drawable)
        }
        nativeAd.headline?.let {
            this.adHeadline.text = it
            this.adHeadline.setTextColor(textColor)
        }
        nativeAd.callToAction?.let {
            this.adCallToAction.text = it
        }
        nativeAd.starRating?.let {
            this.adStars.rating = it.toFloat()
        }
        nativeAd.mediaContent?.let {
            this.adMedia.mediaContent = it
        }
        adView.setNativeAd(nativeAd)
    }
}

fun loadNativeAd(context: Context, onAdLoaded: (NativeAd) -> Unit) {
    val adLoader =
        AdLoader.Builder(context, BuildConfig.ADMOB_NATIVE_ADS_ID)
            .forNativeAd { nativeAd -> onAdLoaded(nativeAd) }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    println("Native ad failed to load: ${error.message}")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    println("Native ad was loaded.")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    println("Native ad recorded an impression.")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    println("Native ad was clicked.")
                }
            }).withNativeAdOptions(
                NativeAdOptions.Builder().setAdChoicesPlacement(
                    NativeAdOptions.ADCHOICES_TOP_RIGHT
                ).build()
            ).build()
    adLoader.loadAd(AdRequest.Builder().build())
}