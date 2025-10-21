package com.oldogz.applinkalarm.feature.alarm.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.core.designsystem.component.AppLinkAlarmDialog
import com.oldogz.core.designsystem.component.AppLinkAlarmTextField
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@Composable
fun AddUrlDialog(
    addUrl: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var url by rememberSaveable { mutableStateOf("") }

    AppLinkAlarmDialog(
        dialogTitle = stringResource(R.string.feature_alarm_text_add_url),
        content = {
            AppLinkAlarmTextField(
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                singleLine = false,
                value = url,
                placeholder = stringResource(R.string.feature_alarm_text_add_url_description),
                onValueChange = {
                    url = it
                }
            )
        },
        imageVector = Icons.Filled.AddLink,
        contentDescription = stringResource(R.string.feature_alarm_text_add_url),
        confirmText = stringResource(R.string.feature_alarm_text_select_add),
        dismissText = stringResource(R.string.feature_alarm_text_select_cancel),
        onConfirmation = {
            addUrl(url)
            onDismiss()
        },
        onDismissRequest = onDismiss
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AddUrlDialogPreview() {
    AppLinkAlarmTheme {
        AddUrlDialog(
            addUrl = {},
            onDismiss = {}
        )
    }
}