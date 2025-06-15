package com.oldogz.applinkalarm.feature.alarm.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun Uri.getFileName(context: Context): String? {
    val contentResolver = context.contentResolver
    return try {
        contentResolver.query(this, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    } catch (e: Exception) {
        null
    }
}