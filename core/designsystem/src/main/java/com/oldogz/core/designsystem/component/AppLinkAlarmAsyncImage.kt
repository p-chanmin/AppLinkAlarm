package com.oldogz.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@Composable
fun AppLinkAlarmAsyncImage(
    modifier: Modifier = Modifier,
    packageName: String,
    contentDescription: String?
) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    val appInfo = try {
        packageManager.getApplicationInfo(packageName, 0)
    } catch (e: Exception) {
        null
    }
    val icon = appInfo?.loadIcon(packageManager)

    AsyncImage(
        modifier = modifier,
        model = icon,
        contentDescription = contentDescription,
        placeholder = ColorPainter(MaterialTheme.colorScheme.secondary),
        error = ColorPainter(MaterialTheme.colorScheme.secondary),
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AppLinkAlarmAsyncImagePreview() {
    AppLinkAlarmTheme {
        Column {
            AppLinkAlarmAsyncImage(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                packageName = "",
                contentDescription = ""
            )
        }
    }
}