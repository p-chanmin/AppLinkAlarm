package com.oldogz.applinkalarm.feature.alarm.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.core.designsystem.component.AppLinkAlarmAsyncImage
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.model.LinkTarget

@Composable
internal fun AppIconImage(
    linkTarget: LinkTarget,
    size: Dp = 48.dp
) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    when (val target = linkTarget) {
        is LinkTarget.App -> {
            val appInfo = try {
                packageManager.getApplicationInfo(target.packageName, 0)
            } catch (e: Exception) {
                null
            }
            val label =
                appInfo?.loadLabel(packageManager)
                    ?: stringResource(R.string.feature_alarm_text_not_found)

            AppLinkAlarmAsyncImage(
                modifier = Modifier
                    .size(size)
                    .clip(RoundedCornerShape(8.dp)),
                packageName = target.packageName,
                contentDescription = stringResource(
                    R.string.feature_alarm_icon_description_app_icon,
                    label
                ),
            )
        }

        is LinkTarget.Url -> {
            Image(
                modifier = Modifier
                    .size(size)
                    .clip(RoundedCornerShape(8.dp)),
                imageVector = ImageVector.vectorResource(R.drawable.outline_link_24),
                contentDescription = "URL"
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AppLinkAlarmItemPreview() {
    AppLinkAlarmTheme {
        Column {
            AppIconImage(
                linkTarget = LinkTarget.App(packageName = "")
            )

            AppIconImage(
                linkTarget = LinkTarget.Url(urlString = "sample.com")
            )
        }
    }
}