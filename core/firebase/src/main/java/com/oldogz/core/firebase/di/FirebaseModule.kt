package com.oldogz.core.firebase.di

import android.content.Context
import com.google.firebase.FirebaseApp
import com.oldogz.core.firebase.FirebaseManager
import com.oldogz.core.firebase.FirebaseManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {
    @Singleton
    @Provides
    fun provideFirebaseManager(@ApplicationContext context: Context): FirebaseManager {
        FirebaseApp.initializeApp(context)
        return FirebaseManagerImpl(context)
    }
}