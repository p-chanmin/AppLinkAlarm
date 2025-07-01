package com.oldogz.core.data

import com.oldogz.core.database.datasource.SettingDataSource
import javax.inject.Inject

class InAppServiceRepository @Inject constructor(
    private val settingDataSource: SettingDataSource
) {
    val lastReviewDate = settingDataSource.lastReviewDate

    val rejectFlexibleUpdateDate = settingDataSource.rejectFlexibleUpdateDate

    suspend fun setLastReviewDate(reviewDate: String) {
        settingDataSource.setLastReviewDate(reviewDate)
    }

    suspend fun setRejectFlexibleUpdateDate(rejectFlexibleUpdateDate: String) {
        settingDataSource.setRejectFlexibleUpdateDate(rejectFlexibleUpdateDate)
    }
}