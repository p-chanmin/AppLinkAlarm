package com.oldogz.core.data

import com.oldogz.core.database.datasource.SettingDataSource
import javax.inject.Inject

class InAppServiceRepository @Inject constructor(
    private val settingDataSource: SettingDataSource
) {
    val rejectFlexibleUpdateDate = settingDataSource.rejectFlexibleUpdateDate

    suspend fun setRejectFlexibleUpdateDate(rejectFlexibleUpdateDate: String) {
        settingDataSource.setRejectFlexibleUpdateDate(rejectFlexibleUpdateDate)
    }
}