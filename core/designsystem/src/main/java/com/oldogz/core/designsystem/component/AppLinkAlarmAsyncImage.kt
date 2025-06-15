package com.oldogz.core.designsystem.component

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import coil.compose.AsyncImage
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@Composable
fun AppLinkAlarmAsyncImage(
    modifier: Modifier = Modifier,
    drawable: Drawable?,
    contentDescription: String?
) {
    AsyncImage(
        modifier = modifier,
        model = drawable,
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
                drawable = (1).toDrawable(),
                contentDescription = ""
            )
        }
    }
}