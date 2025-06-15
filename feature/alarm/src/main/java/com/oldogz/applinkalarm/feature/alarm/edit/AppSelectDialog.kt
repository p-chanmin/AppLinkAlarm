package com.oldogz.applinkalarm.feature.alarm.edit

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.drawable.toDrawable
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.applinkalarm.feature.alarm.model.AppInfo
import com.oldogz.core.designsystem.component.AppLinkAlarmAsyncImage
import com.oldogz.core.designsystem.component.AppLinkAlarmIconButton
import com.oldogz.core.designsystem.component.AppLinkAlarmTextField
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.designsystem.theme.Paddings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppSelectDialog(
    updateLinkedAppPackage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val apps = remember { mutableStateListOf<AppInfo>() }
    var searchKeyWords by rememberSaveable { mutableStateOf("") }
    val filteredApps = remember { mutableStateListOf<AppInfo>() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
            val appInfoList = resolveInfoList.map { resolveInfo ->
                val app = resolveInfo.activityInfo.applicationInfo
                AppInfo(
                    appName = app.loadLabel(packageManager).toString(),
                    packageName = app.packageName,
                    icon = app.loadIcon(packageManager)
                )
            }
            apps.addAll(appInfoList)
            searchApps(searchKeyWords, apps, filteredApps)
        }
    }

    LaunchedEffect(searchKeyWords) {
        searchApps(searchKeyWords, apps, filteredApps)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            AppLinkAlarmTopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                title = stringResource(R.string.feature_alarm_top_app_bar_select_app),
                navigationIcon = {
                    AppLinkAlarmIconButton(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.feature_alarm_icon_description_close),
                        onClick = onDismiss
                    )
                },
                actions = {}
            )
            AppLinkAlarmTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Paddings.xlarge),
                value = searchKeyWords,
                onValueChange = { searchKeyWords = it },
                placeholder = stringResource(R.string.feature_alarm_text_search),
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = Paddings.large)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (apps.isEmpty()) {
                    CircularProgressIndicator()
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredApps, key = { it.packageName }) { appInfo ->
                        AppInfoItem(
                            appInfo = appInfo,
                            onClick = {
                                updateLinkedAppPackage(appInfo.packageName)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun AppInfoItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    appInfo: AppInfo
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick() }
            .padding(Paddings.xlarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppLinkAlarmAsyncImage(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            drawable = appInfo.icon,
            contentDescription = stringResource(
                R.string.feature_alarm_icon_description_app_icon,
                appInfo.appName
            ),
        )
        Column(
            modifier = Modifier.padding(start = Paddings.xlarge),
        ) {
            Text(
                text = appInfo.appName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                modifier = Modifier.padding(top = Paddings.small),
                text = appInfo.packageName,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    }
}

private fun searchApps(
    searchKeyWords: String,
    apps: SnapshotStateList<AppInfo>,
    filteredApps: SnapshotStateList<AppInfo>
) {
    filteredApps.clear()
    val keywords = searchKeyWords
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }

    if (keywords.isEmpty()) {
        filteredApps.addAll(apps)
    } else {
        val pattern = keywords.joinToString("|") { Regex.escape(it) }
        val regex = Regex(pattern, RegexOption.IGNORE_CASE)

        val result = apps.filter { regex.containsMatchIn(it.appName) }
        filteredApps.addAll(result)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppInfoItemPreview() {
    AppLinkAlarmTheme {
        AppInfoItem(
            appInfo = AppInfo(
                appName = "앱 이름",
                packageName = "com.dev.oldogz",
                icon = (1).toDrawable(),
            ),
            onClick = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppSelectDialogPreview() {
    AppLinkAlarmTheme {
        AppSelectDialog(
            updateLinkedAppPackage = {},
            onDismiss = {}
        )
    }
}