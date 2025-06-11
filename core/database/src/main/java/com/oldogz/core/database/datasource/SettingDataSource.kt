package com.oldogz.core.database.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    val lastReviewDate: Flow<String?> = dataStore.data.map {
        it[LAST_REVIEW_DATE]
    }.distinctUntilChanged()

    val rejectFlexibleUpdateDate: Flow<String?> = dataStore.data.map {
        it[REJECT_FLEXIBLE_UPDATE_DATE]
    }.distinctUntilChanged()

    suspend fun setLastReviewDate(reviewDate: String) {
        dataStore.edit {
            it[LAST_REVIEW_DATE] = reviewDate
        }
    }

    suspend fun setRejectFlexibleUpdateDate(rejectFlexibleUpdateDate: String) {
        dataStore.edit {
            it[REJECT_FLEXIBLE_UPDATE_DATE] = rejectFlexibleUpdateDate
        }
    }

    companion object {
        val LAST_REVIEW_DATE = stringPreferencesKey("lastReviewDate")
        val REJECT_FLEXIBLE_UPDATE_DATE = stringPreferencesKey("rejectFlexibleUpdateDate")
    }
}