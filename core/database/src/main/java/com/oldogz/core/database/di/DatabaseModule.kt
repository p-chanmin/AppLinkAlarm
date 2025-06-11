package com.oldogz.core.database.di

import android.content.Context
import androidx.room.Room
import com.oldogz.core.database.AppLinkAlarmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppLinkAlarmDatabase(
        @ApplicationContext context: Context,
    ): AppLinkAlarmDatabase {
        val db = Room.databaseBuilder(
            context = context,
            AppLinkAlarmDatabase::class.java,
            "app_link_alarm_database.db"
        ).build()
        return db
    }

    @Provides
    fun provideAlarmEntityDao(appLinkAlarmDatabase: AppLinkAlarmDatabase) =
        appLinkAlarmDatabase.alarmEntityDao()
}