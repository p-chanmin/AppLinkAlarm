package com.oldogz.applinkalarm.feature.alarm.home

import SmallNativeAd
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.AppBlocking
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.analytics.logEvent
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.applinkalarm.feature.alarm.component.AppLinkAlarmItem
import com.oldogz.applinkalarm.feature.alarm.edit.ExactAlarmPermissionDialog
import com.oldogz.applinkalarm.feature.alarm.model.AlarmHomeUiEvent
import com.oldogz.applinkalarm.feature.alarm.model.AlarmHomeUiState
import com.oldogz.applinkalarm.feature.alarm.model.AppLinkAlarmUiState
import com.oldogz.applinkalarm.feature.alarm.model.PermissionState
import com.oldogz.applinkalarm.feature.alarm.open.DismissAlarmScreen
import com.oldogz.applinkalarm.feature.alarm.util.normalizeUrl
import com.oldogz.core.designsystem.component.AppLinkAlarmDialog
import com.oldogz.core.designsystem.component.AppLinkAlarmIconButton
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.designsystem.theme.Paddings
import com.oldogz.core.firebase.LocalFirebaseManager
import com.oldogz.core.firebase.model.FA
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.LinkTarget
import com.oldogz.core.model.PeriodOfDay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun AlarmHomeScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToAlarmEdit: (Int?) -> Unit,
    navigateToSetting: () -> Unit,
    alarmHomeViewModel: AlarmHomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val homeUiState by alarmHomeViewModel.homeUiState.collectAsStateWithLifecycle()
    val currentAppLinkAlarmId by alarmHomeViewModel.currentAppLinkAlarmId.collectAsStateWithLifecycle()
    val hasPremium by alarmHomeViewModel.hasPremium.collectAsStateWithLifecycle()
    val firebaseManager = LocalFirebaseManager.current
    val configuration = LocalConfiguration.current

    LaunchedEffect(Unit) {
        firebaseManager.screenLogEvent("AlarmHomeScreen", configuration.orientation)
        alarmHomeViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    LaunchedEffect(Unit) {
        alarmHomeViewModel.event.collect { event ->
            when (event) {
                is AlarmHomeUiEvent.LinkedAppOpen -> {
                    when (val target = event.linkTarget) {
                        is LinkTarget.App -> {
                            val launchIntent =
                                context.packageManager.getLaunchIntentForPackage(target.packageName)
                            if (launchIntent != null) {
                                context.startActivity(launchIntent)
                            } else {
                                onShowErrorSnackBar(
                                    Throwable(
                                        context.getString(
                                            R.string.feature_alarm_error_text_app_not_found,
                                            target.packageName
                                        )
                                    )
                                )
                            }
                        }

                        is LinkTarget.Url -> {
                            val normalizeUrl = normalizeUrl(target.urlString)
                            val intent = Intent(Intent.ACTION_VIEW, normalizeUrl.toUri())
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.feature_alarm_error_text_url),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    if (currentAppLinkAlarmId == null) {
        AlarmHomeContent(
            homeUiState = homeUiState,
            hasPremium = hasPremium,
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToAlarmEdit = navigateToAlarmEdit,
            navigateToSetting = navigateToSetting,
            updateAlarmActive = alarmHomeViewModel::updateAlarmActive,
            updateSelectMode = alarmHomeViewModel::updateSelectMode,
            selectAlarm = alarmHomeViewModel::selectAlarm,
            selectAllAlarm = alarmHomeViewModel::selectAllAlarm,
            updateSelectedAlarmActive = alarmHomeViewModel::updateSelectedAlarmActive,
            deleteSelectedAlarm = alarmHomeViewModel::deleteSelectedAlarm,
            updateNotificationPermissionState = alarmHomeViewModel::updateNotificationPermissionState,
            cancelExactAlarmPermissionDialog = alarmHomeViewModel::cancelExactAlarmPermissionDialog,
        )
    } else {
        DismissAlarmScreen(
            paddingValues = paddingValues,
            onShowErrorSnackBar = onShowErrorSnackBar,
            dismissAlarm = alarmHomeViewModel::dismissAlarm,
        )
    }
}

@Composable
private fun AlarmHomeContent(
    homeUiState: AlarmHomeUiState,
    hasPremium: Boolean?,
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToAlarmEdit: (Int?) -> Unit,
    navigateToSetting: () -> Unit,
    updateAlarmActive: (AppLinkAlarm, Boolean) -> Unit,
    updateSelectMode: (Boolean, Int?) -> Unit,
    selectAlarm: (Boolean, Int) -> Unit,
    selectAllAlarm: (Boolean) -> Unit,
    updateSelectedAlarmActive: (Boolean) -> Unit,
    deleteSelectedAlarm: () -> Unit,
    updateNotificationPermissionState: (PermissionState, Boolean) -> Unit,
    cancelExactAlarmPermissionDialog: () -> Unit,
) {

    CheckPermission(
        updateNotificationPermissionState = updateNotificationPermissionState,
        visibleNotificationPermissionDialog = homeUiState.visibleNotificationPermissionDialog,
        onShowErrorSnackBar = onShowErrorSnackBar
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AlarmHomeTopAppBar(
                isSelectMode = homeUiState.isSelectMode,
                alarms = homeUiState.alarms,
                navigateToAlarmEdit = navigateToAlarmEdit,
                navigateToSetting = navigateToSetting,
                updateSelectMode = updateSelectMode,
                selectAllAlarm = selectAllAlarm
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(homeUiState.alarms, key = { it.appLinkAlarm.id }) { uiState ->
                    AppLinkAlarmItem(
                        modifier = Modifier,
                        selectMode = homeUiState.isSelectMode,
                        selected = uiState.selected,
                        navigateToAlarmEdit = navigateToAlarmEdit,
                        appLinkAlarm = uiState.appLinkAlarm,
                        updateAlarmActive = { updateAlarmActive(uiState.appLinkAlarm, it) },
                        updateSelectMode = updateSelectMode,
                        selectAlarm = selectAlarm,
                    )
                }
            }

            if (!homeUiState.isSelectMode && hasPremium == false) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Paddings.small),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    SmallNativeAd(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            AnimatedVisibility(
                visible = homeUiState.isSelectMode && homeUiState.alarms.any { it.selected },
                enter = slideInVertically { fullHeight -> fullHeight },
            ) {
                AlarmSelectController(
                    updateSelectedAlarmActive = updateSelectedAlarmActive,
                    deleteSelectedAlarm = deleteSelectedAlarm,
                )
            }
        }
    }

    if (homeUiState.visibleExactAlarmPermissionDialog) {
        ExactAlarmPermissionDialog(
            onDismiss = cancelExactAlarmPermissionDialog
        )
    }
}

@Composable
private fun CheckPermission(
    visibleNotificationPermissionDialog: Boolean,
    updateNotificationPermissionState: (PermissionState, Boolean) -> Unit,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isPreview = LocalInspectionMode.current

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            updateNotificationPermissionState(PermissionState.GRANTED, false)
        } else {
            val shouldShowRationale = permissions.keys.any {
                shouldShowRequestPermissionRationale(context as Activity, it)
            }
            if (shouldShowRationale) {
                updateNotificationPermissionState(PermissionState.DENIED, true)
            } else {
                updateNotificationPermissionState(PermissionState.DENIED, false)
            }
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isPreview) {
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        val requiredPermissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                        permissionsLauncher.launch(requiredPermissions)
                    }

                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    if (visibleNotificationPermissionDialog) {
        AppLinkAlarmDialog(
            dialogTitle = stringResource(R.string.feature_alarm_text_permission_denied),
            content = {
                Text(text = stringResource(R.string.feature_alarm_text_notification_permission_denied_content))
            },
            imageVector = Icons.Filled.AppBlocking,
            contentDescription = stringResource(R.string.feature_alarm_text_permission_denied),
            confirmText = stringResource(R.string.feature_alarm_text_permission_allow),
            dismissText = stringResource(R.string.feature_alarm_text_permission_do_not_allow),
            onConfirmation = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val requiredPermissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                    permissionsLauncher.launch(requiredPermissions)
                }
                updateNotificationPermissionState(PermissionState.DENIED, false)
            },
            onDismissRequest = {
                updateNotificationPermissionState(PermissionState.DENIED, false)
                onShowErrorSnackBar(Throwable(context.getString(R.string.feature_alarm_error_text_notification_permission_denied)))
            }
        )
    }
}

@Composable
private fun AlarmHomeTopAppBar(
    isSelectMode: Boolean,
    alarms: ImmutableList<AppLinkAlarmUiState>,
    navigateToAlarmEdit: (Int?) -> Unit,
    navigateToSetting: () -> Unit,
    updateSelectMode: (Boolean, Int?) -> Unit,
    selectAllAlarm: (Boolean) -> Unit,
) {
    val firebaseManager = LocalFirebaseManager.current

    if (isSelectMode) {
        AppLinkAlarmTopAppBar(
            modifier = Modifier
                .fillMaxWidth(),
            title = if (alarms.none { it.selected }) {
                stringResource(R.string.feature_alarm_top_app_bar_title_select_default)
            } else {
                stringResource(
                    R.string.feature_alarm_top_app_bar_title_select_alarm,
                    alarms.count { it.selected }
                )
            },
            navigationIcon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val checkBoxState = when {
                        alarms.all { it.selected } -> ToggleableState.On
                        alarms.none { it.selected } -> ToggleableState.Off
                        else -> ToggleableState.Indeterminate
                    }
                    TriStateCheckbox(
                        state = checkBoxState,
                        onClick = {
                            when (checkBoxState) {
                                ToggleableState.On -> {
                                    firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_SELECT) {
                                        param(FA.Param.Key.CHECKED_STATE, false.toString())
                                        param(FA.Param.Key.SELECT_TYPE, FA.Param.Value.ALL)
                                    }
                                    selectAllAlarm(false)
                                }

                                else -> {
                                    firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_SELECT) {
                                        param(FA.Param.Key.CHECKED_STATE, true.toString())
                                        param(FA.Param.Key.SELECT_TYPE, FA.Param.Value.ALL)
                                    }
                                    selectAllAlarm(true)
                                }
                            }
                        },
                    )
                    Text(
                        modifier = Modifier.offset(y = (-10).dp),
                        text = stringResource(R.string.feature_alarm_text_select_all),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_SELECT_MODE) {
                            param(FA.Param.Key.ACTIVE_STATE, false.toString())
                        }
                        updateSelectMode(false, null)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.feature_alarm_text_select_cancel),
                        style = MaterialTheme.typography.labelLarge.copy(
                            MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }
        )
    } else {
        AppLinkAlarmTopAppBar(
            modifier = Modifier
                .fillMaxWidth(),
            title = stringResource(R.string.feature_alarm_top_app_bar_title_default),
            navigationIcon = {
                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.feature_alarm_icon_description_settings),
                    onClick = navigateToSetting
                )
            },
            actions = {
                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.feature_alarm_icon_description_add_alarm),
                    onClick = { navigateToAlarmEdit(null) }
                )
            }
        )
    }
}

@Composable
private fun AlarmSelectController(
    updateSelectedAlarmActive: (Boolean) -> Unit,
    deleteSelectedAlarm: () -> Unit,
) {
    val firebaseManager = LocalFirebaseManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLinkAlarmIconButton(
                modifier = Modifier,
                imageVector = Icons.Filled.AlarmOn,
                contentDescription = stringResource(R.string.feature_alarm_icon_description_selected_alarm_on),
                onClick = {
                    firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_ACTIVE_STATE_UPDATE) {
                        param(FA.Param.Key.ACTIVE_STATE, true.toString())
                        param(FA.Param.Key.SELECT_TYPE, FA.Param.Value.SELECTED)
                    }
                    updateSelectedAlarmActive(true)
                }
            )
            Text(
                modifier = Modifier.offset(y = (-8).dp),
                text = stringResource(R.string.feature_alarm_text_on),
                style = MaterialTheme.typography.labelMedium
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLinkAlarmIconButton(
                modifier = Modifier,
                imageVector = Icons.Filled.AlarmOff,
                contentDescription = stringResource(R.string.feature_alarm_icon_description_selected_alarm_off),
                onClick = {
                    firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_ACTIVE_STATE_UPDATE) {
                        param(FA.Param.Key.ACTIVE_STATE, false.toString())
                        param(FA.Param.Key.SELECT_TYPE, FA.Param.Value.SELECTED)
                    }
                    updateSelectedAlarmActive(false)
                }
            )
            Text(
                modifier = Modifier.offset(y = (-8).dp),
                text = stringResource(R.string.feature_alarm_text_off),
                style = MaterialTheme.typography.labelMedium
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLinkAlarmIconButton(
                modifier = Modifier,
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = stringResource(R.string.feature_alarm_icon_description_selected_alarm_delete),
                onClick = {
                    firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_DELETE) {
                        param(FA.Param.Key.SELECT_TYPE, FA.Param.Value.SELECTED)
                    }
                    deleteSelectedAlarm()
                }
            )
            Text(
                modifier = Modifier.offset(y = (-8).dp),
                text = stringResource(R.string.feature_alarm_text_selected_alarm_delete),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeContentPreview() {
    AppLinkAlarmTheme {
        AlarmHomeContent(
            homeUiState = AlarmHomeUiState(
                isSelectMode = false,
                visibleNotificationPermissionDialog = false,
                alarms = persistentListOf(
                    AppLinkAlarmUiState(
                        selected = true,
                        appLinkAlarm = AppLinkAlarm(
                            id = 1,
                            hour = 10,
                            minute = 3,
                            periodOfDay = PeriodOfDay.AM,
                            alarmName = "알람 테스트 1 입니다.",
                            dayOfWeek = listOf(
                                DayOfWeek.MONDAY,
                                DayOfWeek.TUESDAY,
                                DayOfWeek.WEDNESDAY,
                                DayOfWeek.THURSDAY,
                                DayOfWeek.FRIDAY,
                                DayOfWeek.SATURDAY,
                                DayOfWeek.SUNDAY
                            ),
                            active = true
                        )
                    ),
                    AppLinkAlarmUiState(
                        selected = false,
                        appLinkAlarm = AppLinkAlarm(
                            id = 2,
                            hour = 10,
                            minute = 3,
                            periodOfDay = PeriodOfDay.AM,
                            alarmName = "알람 테스트 1 입니다.",
                            dayOfWeek = listOf(
                                DayOfWeek.MONDAY,
                                DayOfWeek.TUESDAY,
                                DayOfWeek.WEDNESDAY,
                                DayOfWeek.THURSDAY,
                                DayOfWeek.FRIDAY,
                                DayOfWeek.SATURDAY,
                                DayOfWeek.SUNDAY
                            ),
                            active = true
                        )
                    ),
                )
            ),
            hasPremium = false,
            paddingValues = PaddingValues(),
            onShowErrorSnackBar = {},
            navigateToAlarmEdit = {},
            navigateToSetting = {},
            updateAlarmActive = { _, _ -> },
            updateSelectMode = { _, _ -> },
            selectAlarm = { _, _ -> },
            selectAllAlarm = {},
            updateSelectedAlarmActive = {},
            deleteSelectedAlarm = {},
            updateNotificationPermissionState = { _, _ -> },
            cancelExactAlarmPermissionDialog = {},
        )
    }
}