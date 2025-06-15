package com.oldogz.applinkalarm.feature.alarm.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.core.designsystem.component.AppLinkAlarmAsyncImage
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@Composable
internal fun AppIconImage(
    linkedAppPackage: String,
    size: Dp = 48.dp
) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    val appInfo = try {
        packageManager.getApplicationInfo(linkedAppPackage, 0)
    } catch (e: Exception) {
        null
    }
    val label =
        appInfo?.loadLabel(packageManager) ?: stringResource(R.string.feature_alarm_text_not_found)
    val icon = appInfo?.loadIcon(packageManager)

    AppLinkAlarmAsyncImage(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp)),
        drawable = icon,
        contentDescription = stringResource(
            R.string.feature_alarm_icon_description_app_icon,
            label
        ),
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