package com.oldogz.core.firebase

import androidx.compose.runtime.staticCompositionLocalOf

val LocalFirebaseManager = staticCompositionLocalOf<FirebaseManager> {
    FakeFirebaseManager()
}