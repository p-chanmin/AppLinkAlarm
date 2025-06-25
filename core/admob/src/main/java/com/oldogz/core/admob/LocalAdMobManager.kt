package com.oldogz.core.admob

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAdMobManager = staticCompositionLocalOf<AdMobManager> {
    FakeAdMobManager()
}