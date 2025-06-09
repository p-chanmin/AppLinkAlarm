package com.oldogz.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppBlocking
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.designsystem.theme.White

@Composable
fun AppLinkAlarmDialog(
    dialogTitle: String,
    dialogText: String,
    imageVector: ImageVector,
    contentDescription: String?,
    confirmText: String,
    dismissText: String,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.background,
    iconContentColor: Color = MaterialTheme.colorScheme.onBackground,
    titleContentColor: Color = MaterialTheme.colorScheme.onBackground,
    textContentColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    AlertDialog(
        icon = {
            Icon(imageVector = imageVector, contentDescription = contentDescription)
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                    onDismissRequest()
                }
            ) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = dismissText)
            }
        },
        containerColor = containerColor,
        iconContentColor = iconContentColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AppLinkAlarmDialogPreview() {
    AppLinkAlarmTheme {
        AppLinkAlarmDialog(
            dialogTitle = "title",
            dialogText = "content text",
            imageVector = Icons.Filled.AppBlocking,
            contentDescription = "",
            confirmText = "Confirm",
            dismissText = "Dismiss",
            onConfirmation = {},
            onDismissRequest = {}
        )
    }
}