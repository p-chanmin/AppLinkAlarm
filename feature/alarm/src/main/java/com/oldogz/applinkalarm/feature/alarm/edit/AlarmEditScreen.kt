package com.oldogz.applinkalarm.feature.alarm.edit

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.analytics.logEvent
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.applinkalarm.feature.alarm.component.AppIconImage
import com.oldogz.applinkalarm.feature.alarm.component.WheelPicker
import com.oldogz.applinkalarm.feature.alarm.model.AlarmEditUiEvent
import com.oldogz.applinkalarm.feature.alarm.model.AlarmEditUiState
import com.oldogz.applinkalarm.feature.alarm.util.dayOfWeekToString
import com.oldogz.applinkalarm.feature.alarm.util.getFileName
import com.oldogz.core.admob.LocalAdMobManager
import com.oldogz.core.designsystem.component.AppLinkAlarmButton
import com.oldogz.core.designsystem.component.AppLinkAlarmFilterChip
import com.oldogz.core.designsystem.component.AppLinkAlarmIconButton
import com.oldogz.core.designsystem.component.AppLinkAlarmSlider
import com.oldogz.core.designsystem.component.AppLinkAlarmSwitch
import com.oldogz.core.designsystem.component.AppLinkAlarmTextField
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.designsystem.theme.BitterSweet
import com.oldogz.core.designsystem.theme.NeonBlue
import com.oldogz.core.designsystem.theme.Paddings
import com.oldogz.core.firebase.LocalFirebaseManager
import com.oldogz.core.firebase.model.FA
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.LinkTarget
import com.oldogz.core.model.PeriodOfDay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun AlarmEditScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    popBackStack: () -> Unit,
    alarmEditViewModel: AlarmEditViewModel = hiltViewModel()
) {
    val alarmEditUiState by alarmEditViewModel.alarmEditUiState.collectAsStateWithLifecycle()
    val hasPremium by alarmEditViewModel.hasPremium.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = 5)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = 30)
    val periodOfDayState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    val firebaseManager = LocalFirebaseManager.current
    val configuration = LocalConfiguration.current

    val adManager = LocalAdMobManager.current
    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        firebaseManager.screenLogEvent("AlarmEditScreen", configuration.orientation)
        alarmEditViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    LaunchedEffect(Unit) {
        alarmEditViewModel.event.collect { event ->
            when (event) {
                is AlarmEditUiEvent.AlarmEditComplete -> {
                    popBackStack()
                    activity?.let {
                        if (hasPremium == false) {
                            adManager.showInterstitialAlarmAd(it)
                        }
                    }
                }

                is AlarmEditUiEvent.AlarmLoad -> {
                    coroutineScope.launch {
                        hourState.scrollToItem(event.hour - 1)
                        minuteState.scrollToItem(event.minute)
                        when (event.periodOfDay) {
                            PeriodOfDay.AM -> periodOfDayState.scrollToItem(0)
                            PeriodOfDay.PM -> periodOfDayState.scrollToItem(1)
                        }
                    }
                }
            }
        }
    }

    AlarmEditContent(
        alarmEditUiState = alarmEditUiState,
        paddingValues = paddingValues,
        popBackStack = popBackStack,
        updateLinkTarget = alarmEditViewModel::updateLinkTarget,
        hourState = hourState,
        minuteState = minuteState,
        periodOfDayState = periodOfDayState,
        updateHour = alarmEditViewModel::updateHour,
        updateMinute = alarmEditViewModel::updateMinute,
        updatePeriodOfDay = alarmEditViewModel::updatePeriodOfDay,
        updateDayOfWeek = alarmEditViewModel::updateDayOfWeek,
        updateAlarmName = alarmEditViewModel::updateAlarmName,
        updateMessage = alarmEditViewModel::updateMessage,
        updateAlarmMode = alarmEditViewModel::updateAlarmMode,
        updateVibrate = alarmEditViewModel::updateVibrate,
        updateAlarmSound = alarmEditViewModel::updateAlarmSound,
        updateAlarmVolume = alarmEditViewModel::updateAlarmVolume,
        updateVisibleSelectAppDialog = alarmEditViewModel::updateVisibleSelectAppDialog,
        saveAlarm = alarmEditViewModel::saveAlarm,
        cancelExactAlarmPermissionDialog = alarmEditViewModel::cancelExactAlarmPermissionDialog,
    )
}

@Composable
private fun AlarmEditContent(
    alarmEditUiState: AlarmEditUiState,
    paddingValues: PaddingValues,
    popBackStack: () -> Unit,
    updateLinkTarget: (LinkTarget) -> Unit,
    hourState: LazyListState,
    minuteState: LazyListState,
    periodOfDayState: LazyListState,
    updateHour: (Int) -> Unit,
    updateMinute: (Int) -> Unit,
    updatePeriodOfDay: (PeriodOfDay) -> Unit,
    updateDayOfWeek: (DayOfWeek) -> Unit,
    updateAlarmName: (String) -> Unit,
    updateMessage: (String) -> Unit,
    updateAlarmMode: () -> Unit,
    updateVibrate: (Boolean) -> Unit,
    updateAlarmSound: (String) -> Unit,
    updateAlarmVolume: (Float) -> Unit,
    updateVisibleSelectAppDialog: () -> Unit,
    saveAlarm: () -> Unit,
    cancelExactAlarmPermissionDialog: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val firebaseManager = LocalFirebaseManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            AppLinkAlarmTopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                title = if (alarmEditUiState.id == null) {
                    stringResource(R.string.feature_alarm_top_app_bar_new_alarm)
                } else {
                    stringResource(R.string.feature_alarm_top_app_bar_edit_alarm)
                },
                navigationIcon = {
                    AppLinkAlarmIconButton(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.feature_alarm_icon_description_close),
                        onClick = popBackStack
                    )
                },
                actions = {}
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                ChooseApp(
                    linkTarget = alarmEditUiState.linkTarget,
                    updateVisibleSelectAppDialog = updateVisibleSelectAppDialog
                )
                AlarmTimer(
                    hourState = hourState,
                    minuteState = minuteState,
                    periodOfDayState = periodOfDayState,
                    updateHour = updateHour,
                    updateMinute = updateMinute,
                    updatePeriodOfDay = updatePeriodOfDay,
                )
                RepeatDays(
                    dayOfWeek = alarmEditUiState.dayOfWeek,
                    updateDayOfWeek = updateDayOfWeek,
                )
                AlarmInfo(
                    alarmName = alarmEditUiState.alarmName,
                    message = alarmEditUiState.message,
                    updateAlarmName = updateAlarmName,
                    updateMessage = updateMessage,
                )
                AlarmMode(
                    alarmMode = alarmEditUiState.alarmMode,
                    vibrate = alarmEditUiState.vibrate,
                    alarmSound = alarmEditUiState.alarmSound,
                    alarmVolume = alarmEditUiState.alarmVolume,
                    updateAlarmMode = {
                        coroutineScope.launch {
                            updateAlarmMode()
                            delay(10)
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    },
                    updateVibrate = updateVibrate,
                    updateAlarmSound = updateAlarmSound,
                    updateAlarmVolume = updateAlarmVolume,
                )
            }

            AppLinkAlarmButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Paddings.large),
                content = stringResource(R.string.feature_alarm_text_alarm_save),
                enabled = (alarmEditUiState.linkTarget != null &&
                        alarmEditUiState.dayOfWeek.isNotEmpty() &&
                        alarmEditUiState.alarmName.isNotEmpty() &&
                        alarmEditUiState.message.isNotEmpty()),
                onClick = {
                    firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_SAVE) {
                        param(FA.Param.Key.HOUR, alarmEditUiState.hour.toString())
                        param(FA.Param.Key.MINUTE, alarmEditUiState.minute.toString())
                        param(FA.Param.Key.PERIOD_OF_DAY, alarmEditUiState.periodOfDay.name)
                        param(FA.Param.Key.DAY_OF_WEEK, alarmEditUiState.dayOfWeek.toString())
                        param(FA.Param.Key.ALARM_MODE, alarmEditUiState.alarmMode.name)
                    }
                    saveAlarm()
                }
            )
        }
    }

    if (alarmEditUiState.visibleSelectAppDialog) {
        AppSelectDialog(
            updateLinkTarget = updateLinkTarget,
            onDismiss = updateVisibleSelectAppDialog
        )
    }

    if (alarmEditUiState.visibleExactAlarmPermissionDialog) {
        ExactAlarmPermissionDialog(
            onDismiss = cancelExactAlarmPermissionDialog
        )
    }
}

@Composable
internal fun ChooseApp(
    linkTarget: LinkTarget?,
    updateVisibleSelectAppDialog: () -> Unit,
) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
            text = stringResource(R.string.feature_alarm_text_linked_app),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        linkTarget?.let { linkTarget ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .clickable { updateVisibleSelectAppDialog() }
                    .padding(Paddings.xlarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    AppIconImage(
                        linkTarget = linkTarget,
                        size = 32.dp
                    )

                    Column(
                        modifier = Modifier.padding(start = Paddings.xlarge),
                    ) {
                        val (label, destination) = when (val target = linkTarget) {
                            is LinkTarget.App -> {
                                val appInfo = try {
                                    packageManager.getApplicationInfo(target.packageName, 0)
                                } catch (e: Exception) {
                                    null
                                }
                                val label = appInfo?.loadLabel(packageManager)
                                    ?: stringResource(R.string.feature_alarm_text_not_found)

                                Pair(label.toString(), appInfo?.packageName)
                            }

                            is LinkTarget.Url -> {
                                Pair(
                                    stringResource(R.string.feature_alarm_text_url),
                                    target.urlString
                                )
                            }
                        }
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            modifier = Modifier.padding(top = Paddings.small),
                            text = destination
                                ?: stringResource(R.string.feature_alarm_text_not_found),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }
                }
            }
        }

        if (linkTarget == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { updateVisibleSelectAppDialog() }
                    .padding(start = Paddings.xlarge)
                    .padding(vertical = Paddings.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.feature_alarm_text_choose_app),
                    style = MaterialTheme.typography.bodyLarge
                )

                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = stringResource(R.string.feature_alarm_text_choose_app),
                    onClick = updateVisibleSelectAppDialog
                )
            }
        }
    }
}

@Composable
internal fun AlarmTimer(
    hourState: LazyListState,
    minuteState: LazyListState,
    periodOfDayState: LazyListState,
    updateHour: (Int) -> Unit,
    updateMinute: (Int) -> Unit,
    updatePeriodOfDay: (PeriodOfDay) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
            text = stringResource(R.string.feature_alarm_text_time),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WheelPicker(
                    modifier = Modifier.weight(1f),
                    state = hourState,
                    list = (1..12).toList(),
                    itemHeight = 50.dp,
                    selectedItem = { it?.let { hour -> updateHour(hour) } }
                )
                WheelPicker(
                    modifier = Modifier.weight(1f),
                    state = minuteState,
                    list = (0..59).toList(),
                    itemHeight = 50.dp,
                    selectedItem = { it?.let { minute -> updateMinute(minute) } }
                )
                val dayOfWeek = listOf(
                    stringResource(R.string.feature_alarm_text_am),
                    stringResource(R.string.feature_alarm_text_pm)
                )
                WheelPicker(
                    modifier = Modifier.weight(1f),
                    state = periodOfDayState,
                    list = dayOfWeek,
                    itemHeight = 50.dp,
                    selectedItem = {
                        it?.let { periodOfDay ->
                            if (periodOfDay == dayOfWeek[0]) {
                                updatePeriodOfDay(PeriodOfDay.AM)
                            } else {
                                updatePeriodOfDay(PeriodOfDay.PM)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
internal fun RepeatDays(
    dayOfWeek: ImmutableList<DayOfWeek>,
    updateDayOfWeek: (DayOfWeek) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(
                    horizontal = Paddings.xlarge,
                    vertical = Paddings.large
                ),
                text = stringResource(R.string.feature_alarm_text_repeat_days),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                modifier = Modifier.padding(
                    horizontal = Paddings.xlarge,
                    vertical = Paddings.large
                ),
                text = dayOfWeekToString(context, dayOfWeek),
                style = MaterialTheme.typography.labelMedium
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = stringResource(R.string.feature_alarm_text_sunday_simple),
                labelColor = BitterSweet,
                enabled = true,
                selected = DayOfWeek.SUNDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.SUNDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = stringResource(R.string.feature_alarm_text_monday_simple),
                enabled = true,
                selected = DayOfWeek.MONDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.MONDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = stringResource(R.string.feature_alarm_text_tuesday_simple),
                enabled = true,
                selected = DayOfWeek.TUESDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.TUESDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = stringResource(R.string.feature_alarm_text_wednesday_simple),
                enabled = true,
                selected = DayOfWeek.WEDNESDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.WEDNESDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = stringResource(R.string.feature_alarm_text_thursday_simple),
                enabled = true,
                selected = DayOfWeek.THURSDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.THURSDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = stringResource(R.string.feature_alarm_text_friday_simple),
                enabled = true,
                selected = DayOfWeek.FRIDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.FRIDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = stringResource(R.string.feature_alarm_text_saturday_simple),
                labelColor = NeonBlue,
                enabled = true,
                selected = DayOfWeek.SATURDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.SATURDAY) }
            )
        }
    }
}

@Composable
internal fun AlarmInfo(
    alarmName: String,
    message: String,
    updateAlarmName: (String) -> Unit,
    updateMessage: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
            text = stringResource(R.string.feature_alarm_text_alarm_info),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        AppLinkAlarmTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge),
            value = alarmName,
            onValueChange = updateAlarmName,
            placeholder = stringResource(R.string.feature_alarm_text_alarm_name),
            singleLine = true,
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Next)
            }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        AppLinkAlarmTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge)
                .padding(top = Paddings.large),
            value = message,
            onValueChange = updateMessage,
            placeholder = stringResource(R.string.feature_alarm_text_message),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlarmMode(
    alarmMode: AlarmMode,
    vibrate: Boolean,
    alarmSound: String?,
    alarmVolume: Int,
    updateAlarmMode: () -> Unit,
    updateVibrate: (Boolean) -> Unit,
    updateAlarmSound: (String) -> Unit,
    updateAlarmVolume: (Float) -> Unit,
) {
    val context = LocalContext.current
    val filePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, flag)
                updateAlarmSound(it.toString())
            }
        }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge)
                .padding(top = Paddings.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val coroutineScope = rememberCoroutineScope()
            val tooltipState = rememberTooltipState(isPersistent = true)
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_alarm_text_alarm_mode),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            TooltipBox(
                modifier = Modifier,
                positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                tooltip = {
                    RichTooltip(
                        title = {
                            Text(
                                text = stringResource(R.string.feature_alarm_text_alarm_mode)
                            )
                        },
                        colors = TooltipDefaults.richTooltipColors().copy(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            titleContentColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(stringResource(R.string.feature_alarm_text_alarm_mode_tool_tip))
                    }
                },
                state = tooltipState,
                enableUserInput = false,
            ) {
                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.feature_alarm_icon_description_alarm_mode_info),
                    onClick = {
                        coroutineScope.launch {
                            tooltipState.show()
                        }
                    }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { updateAlarmMode() }
                .padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.feature_alarm_text_mode),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                modifier = Modifier.padding(top = Paddings.small),
                text = if (alarmMode == AlarmMode.STANDARD) {
                    stringResource(R.string.feature_alarm_text_standard)
                } else {
                    stringResource(R.string.feature_alarm_text_notification_only)
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSecondary
                )
            )
        }

        AnimatedVisibility(
            visible = alarmMode == AlarmMode.STANDARD,
            enter = slideInVertically { fullHeight -> -fullHeight },
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { filePickerLauncher.launch(arrayOf("audio/*")) }
                        .padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.feature_alarm_text_alarm_sound),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        modifier = Modifier.padding(top = Paddings.small),
                        text = alarmSound?.toUri()?.getFileName(context)
                            ?: stringResource(R.string.feature_alarm_text_default_sound),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.feature_alarm_text_volume, alarmVolume),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    AppLinkAlarmSlider(
                        modifier = Modifier.fillMaxWidth(),
                        value = alarmVolume.toFloat() / 100,
                        onValueChange = updateAlarmVolume
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { updateVibrate(!vibrate) }
                        .padding(horizontal = Paddings.xlarge, vertical = Paddings.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            modifier = Modifier,
                            text = stringResource(R.string.feature_alarm_text_vibrate),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            modifier = Modifier.padding(top = Paddings.small),
                            text = if (vibrate) {
                                stringResource(R.string.feature_alarm_text_on)
                            } else {
                                stringResource(R.string.feature_alarm_text_off)
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }

                    AppLinkAlarmSwitch(
                        modifier = Modifier,
                        checked = vibrate,
                        onCheckedChange = updateVibrate
                    )
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, heightDp = 1200)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, heightDp = 1200)
@Composable
private fun AlarmEditContentPreview() {
    AppLinkAlarmTheme {
        AlarmEditContent(
            alarmEditUiState = AlarmEditUiState(
//                linkTarget = null,
//                linkTarget = LinkTarget.App(packageName = "example.com"),
                linkTarget = LinkTarget.Url(urlString = "sample.com"),
                alarmMode = AlarmMode.STANDARD,
                dayOfWeek = persistentListOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
            ),
            paddingValues = PaddingValues(),
            popBackStack = {},
            updateLinkTarget = {},
            hourState = rememberLazyListState(),
            minuteState = rememberLazyListState(),
            periodOfDayState = rememberLazyListState(),
            updateHour = {},
            updateMinute = {},
            updatePeriodOfDay = {},
            updateDayOfWeek = {},
            updateAlarmName = {},
            updateMessage = {},
            updateAlarmMode = {},
            updateVibrate = {},
            updateAlarmSound = {},
            updateAlarmVolume = {},
            updateVisibleSelectAppDialog = {},
            saveAlarm = {},
            cancelExactAlarmPermissionDialog = {},
        )
    }
}