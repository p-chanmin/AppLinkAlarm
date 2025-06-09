package com.oldogz.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLinkAlarmTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        navigationIcon = navigationIcon,
        actions = actions
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AppLinkAlarmTopAppBarPreview() {
    AppLinkAlarmTheme {
        AppLinkAlarmTopAppBar(
            title = "AppLink Alarms",
            navigationIcon = {
                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    onClick = {}
                )
            },
            actions = {
                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Alarm",
                    onClick = {}
                )
            }
        )
    }
}