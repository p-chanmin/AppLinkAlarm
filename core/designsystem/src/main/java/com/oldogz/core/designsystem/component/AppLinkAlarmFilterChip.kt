package com.oldogz.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.designsystem.theme.BitterSweet

@Composable
fun AppLinkAlarmFilterChip(
    modifier: Modifier = Modifier,
    labelText: String,
    labelColor: Color = MaterialTheme.colorScheme.onBackground,
    enabled: Boolean = true,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    FilterChip(
        modifier = modifier,
        onClick = onClick,
        label = {
            Text(
                text = labelText
            )
        },
        selected = selected,
        enabled = enabled,
        colors = FilterChipDefaults.filterChipColors().copy(
            containerColor = MaterialTheme.colorScheme.background,
            labelColor = labelColor,
            selectedContainerColor = MaterialTheme.colorScheme.onSecondary,
            selectedLabelColor = labelColor
        ),
        shape = CircleShape,
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.onSecondary,
            selectedBorderColor = MaterialTheme.colorScheme.onSecondary,
            borderWidth = 1.dp,
        )
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AppLinkAlarmFilterChipPreview() {
    AppLinkAlarmTheme {
        Column {
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = "M",
                enabled = true,
                selected = false,
                onClick = {}
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = "M",
                enabled = true,
                selected = true,
                onClick = {}
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = "M",
                labelColor = BitterSweet,
                enabled = true,
                selected = false,
                onClick = {}
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = "M",
                labelColor = BitterSweet,
                enabled = true,
                selected = true,
                onClick = {}
            )
        }
    }
}