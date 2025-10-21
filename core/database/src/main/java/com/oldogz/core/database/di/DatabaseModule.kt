package com.oldogz.core.database.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        ).addMigrations(MIGRATION_1_2)
            .build()
        return db
    }

    @Provides
    fun provideAlarmEntityDao(appLinkAlarmDatabase: AppLinkAlarmDatabase) =
        appLinkAlarmDatabase.alarmEntityDao()

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE alarm ADD COLUMN linkTarget TEXT NOT NULL DEFAULT ''")

            db.execSQL(
                """
                UPDATE alarm 
                SET linkTarget = '{"type":"com.oldogz.core.model.LinkTarget.App","packageName":"' || linkedAppPackage || '"}'
                WHERE linkedAppPackage != ''
            """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE alarm_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    linkTarget TEXT NOT NULL,
                    hour INTEGER NOT NULL,
                    minute INTEGER NOT NULL,
                    periodOfDay TEXT NOT NULL,
                    dayOfWeek TEXT NOT NULL,
                    alarmName TEXT NOT NULL,
                    alarmMessage TEXT NOT NULL,
                    alarmMode TEXT NOT NULL,
                    vibrate INTEGER NOT NULL,
                    alarmSound TEXT,
                    alarmVolume INTEGER NOT NULL,
                    active INTEGER NOT NULL
                )
            """.trimIndent()
            )

            db.execSQL(
                """
                INSERT INTO alarm_new (id, linkTarget, hour, minute, periodOfDay, dayOfWeek, 
                                       alarmName, alarmMessage, alarmMode, vibrate, alarmSound, 
                                       alarmVolume, active)
                SELECT id, linkTarget, hour, minute, periodOfDay, dayOfWeek, 
                       alarmName, alarmMessage, alarmMode, vibrate, alarmSound, 
                       alarmVolume, active
                FROM alarm
            """.trimIndent()
            )

            db.execSQL("DROP TABLE alarm")

            db.execSQL("ALTER TABLE alarm_new RENAME TO alarm")
        }
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(PREFERENCES_STORE_NAME) }
        )

    private const val PREFERENCES_STORE_NAME = "AppLinkAlarmDataStore"
}