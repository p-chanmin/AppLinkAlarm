package com.oldogz.core.admob.di

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.oldogz.core.admob.AdMobManager
import com.oldogz.core.admob.AdMobManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AdMobModule {

    @Singleton
    @Provides
    fun provideAdMobManager(@ApplicationContext context: Context): AdMobManager {
        MobileAds.initialize(context)
        return AdMobManagerImpl(context)
    }
}