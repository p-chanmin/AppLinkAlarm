import android.content.Context
import android.view.View
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
            view.callToActionView = this.adCallToAction
            view.starRatingView = this.adStars
            view.iconView = this.adAppIcon
            view.mediaView = this.adMedia
        }

        this.container.setBackgroundColor(backgroundColor)

        nativeAd.icon?.let {
            this.adAppIcon.setImageDrawable(it.drawable)
        } ?: run {
            adAppIcon.visibility = View.GONE
        }

        nativeAd.headline?.let {
            this.adHeadline.text = it
            this.adHeadline.setTextColor(textColor)
        } ?: run {
            adHeadline.visibility = View.INVISIBLE
        }

        nativeAd.callToAction?.let {
            this.adCallToAction.text = it
        } ?: run {
            adCallToAction.visibility = View.GONE
        }

        nativeAd.starRating?.let {
            this.adStars.rating = it.toFloat()
        } ?: run {
            adStars.visibility = View.GONE
        }

        nativeAd.mediaContent?.let {
            this.adMedia.mediaContent = it
        } ?: run {
            adMedia.visibility = View.INVISIBLE
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
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                }
            }).withNativeAdOptions(
                NativeAdOptions.Builder().setAdChoicesPlacement(
                    NativeAdOptions.ADCHOICES_TOP_RIGHT
                ).build()
            ).build()
    adLoader.loadAd(AdRequest.Builder().build())
}