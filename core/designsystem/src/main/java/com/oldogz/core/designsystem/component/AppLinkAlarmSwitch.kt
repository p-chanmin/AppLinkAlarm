package com.oldogz.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.designsystem.theme.White

@Composable
fun AppLinkAlarmSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors().copy(
            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
            uncheckedBorderColor = Color.Transparent,
            uncheckedTrackColor = MaterialTheme.colorScheme.secondary,
            checkedThumbColor = White,
            checkedBorderColor = Color.Transparent,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
        )
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AppLinkAlarmSwitchPreview() {
    AppLinkAlarmTheme {
        Column {
            AppLinkAlarmSwitch(
                modifier = Modifier,
                checked = true,
                onCheckedChange = {},
            )
            AppLinkAlarmSwitch(
                modifier = Modifier,
                checked = false,
                onCheckedChange = {},
            )
        }
    }
}