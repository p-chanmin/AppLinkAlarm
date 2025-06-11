package com.oldogz.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@Composable
fun AppLinkAlarmIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun AppLinkAlarmButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: String,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AppLinkAlarmIconButtonPreview() {
    AppLinkAlarmTheme {
        AppLinkAlarmIconButton(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Settings",
            onClick = {}
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AppLinkAlarmButtonPreview() {
    AppLinkAlarmTheme {
        Column {
            AppLinkAlarmButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                content = "enable = true",
                onClick = {}
            )
            AppLinkAlarmButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                content = "enable = false",
                onClick = {}
            )
            AppLinkAlarmButton(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onBackground,
                enabled = true,
                content = "enable = false",
                onClick = {}
            )
        }
    }
}