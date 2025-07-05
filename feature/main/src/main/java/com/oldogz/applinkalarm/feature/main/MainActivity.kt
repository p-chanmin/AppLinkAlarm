package com.oldogz.applinkalarm.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.oldogz.core.admob.AdMobManager
import com.oldogz.core.admob.LocalAdMobManager
import com.oldogz.core.billing.LocalSubscriptionManager
import com.oldogz.core.billing.SubscriptionManager
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.firebase.FirebaseManager
import com.oldogz.core.firebase.LocalFirebaseManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var adMobManager: AdMobManager

    @Inject
    lateinit var firebaseManager: FirebaseManager

    @Inject
    lateinit var subscriptionManager: SubscriptionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        subscriptionManager.initialize()

        setContent {
            val navigator: MainNavigator = rememberMainNavigator()
            AppLinkAlarmTheme {
                CompositionLocalProvider(
                    LocalAdMobManager provides adMobManager,
                    LocalFirebaseManager provides firebaseManager,
                    LocalSubscriptionManager provides subscriptionManager,
                    LocalDensity provides Density(LocalDensity.current.density, 0.8f),
                ) {
                    MainScreen(
                        navigator = navigator,
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptionManager.endConnection()
    }
}