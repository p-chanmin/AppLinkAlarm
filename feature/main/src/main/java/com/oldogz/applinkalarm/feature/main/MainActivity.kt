package com.oldogz.applinkalarm.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.oldogz.core.admob.AdMobManager
import com.oldogz.core.admob.LocalAdMobManager
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var adMobManager: AdMobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navigator: MainNavigator = rememberMainNavigator()
            AppLinkAlarmTheme {
                CompositionLocalProvider(
                    LocalAdMobManager provides adMobManager,
                ) {
                    MainScreen(
                        navigator = navigator,
                    )
                }
            }
        }
    }
}