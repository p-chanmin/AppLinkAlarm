package com.oldogz.core.billing

import androidx.compose.runtime.staticCompositionLocalOf

val LocalSubscriptionManager = staticCompositionLocalOf<SubscriptionManager> {
    FakeSubscriptionManager()
}