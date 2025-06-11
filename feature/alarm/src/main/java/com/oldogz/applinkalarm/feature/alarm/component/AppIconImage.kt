package com.oldogz.applinkalarm.feature.alarm.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oldogz.core.designsystem.component.AppLinkAlarmAsyncImage
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@Composable
internal fun AppIconImage(
    linkedAppPackage: String
) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    val appInfo = try {
        packageManager.getApplicationInfo(linkedAppPackage, 0)
    } catch (e: Exception) {
        null
    }
    val label = appInfo?.loadLabel(packageManager) ?: "Not Found"
    val icon = appInfo?.loadIcon(packageManager)

    AppLinkAlarmAsyncImage(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp)),
        drawable = icon,
        contentDescription = "$label icon",
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AppLinkAlarmItemPreview() {
    AppLinkAlarmTheme {
        AppIconImage(
            linkedAppPackage = ""
        )
    }
}