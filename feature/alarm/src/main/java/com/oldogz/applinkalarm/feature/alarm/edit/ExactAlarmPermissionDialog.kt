package com.oldogz.applinkalarm.feature.alarm.edit

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppBlocking
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.core.designsystem.component.AppLinkAlarmDialog
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@Composable
fun ExactAlarmPermissionDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AppLinkAlarmDialog(
        dialogTitle = stringResource(R.string.feature_alarm_text_permission_denied),
        content = {
            Text(text = stringResource(R.string.feature_alarm_text_exact_alarm_permission_denied_content))
        },
        imageVector = Icons.Filled.AppBlocking,
        contentDescription = stringResource(R.string.feature_alarm_text_permission_denied),
        confirmText = stringResource(R.string.feature_alarm_text_permission_allow),
        dismissText = stringResource(R.string.feature_alarm_text_permission_do_not_allow),
        onConfirmation = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    "package:${context.packageName}".toUri(),
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
            onDismiss()
        },
        onDismissRequest = onDismiss
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExactAlarmPermissionDialogPreview() {
    AppLinkAlarmTheme {
        ExactAlarmPermissionDialog(
            onDismiss = {}
        )
    }
}