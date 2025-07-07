package com.oldogz.core.billing.di

import android.content.Context
import com.oldogz.core.billing.SubscriptionManager
import com.oldogz.core.billing.SubscriptionManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BillingModule {
    @Singleton
    @Provides
    fun provideSubscriptionManager(@ApplicationContext context: Context): SubscriptionManager {
        return SubscriptionManagerImpl(context)
    }
}