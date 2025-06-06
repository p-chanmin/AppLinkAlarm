package com.oldogz.applinkalarm.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        WindowCompat.getInsetsController(window, window.decorView).apply {
//            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            hide(WindowInsetsCompat.Type.navigationBars())
//        }

        enableEdgeToEdge()

        setContent {
            val navigator: MainNavigator = rememberMainNavigator()
            MainScreen(
                navigator = navigator,
            )
        }
    }
}