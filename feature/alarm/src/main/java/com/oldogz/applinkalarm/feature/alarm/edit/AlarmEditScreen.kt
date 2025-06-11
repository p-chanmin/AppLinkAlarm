package com.oldogz.applinkalarm.feature.alarm.edit

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oldogz.applinkalarm.feature.alarm.component.WheelPicker
import com.oldogz.applinkalarm.feature.alarm.model.AlarmEditUiEvent
import com.oldogz.applinkalarm.feature.alarm.model.AlarmEditUiState
import com.oldogz.core.designsystem.component.AppLinkAlarmAsyncImage
import com.oldogz.core.designsystem.component.AppLinkAlarmButton
import com.oldogz.core.designsystem.component.AppLinkAlarmFilterChip
import com.oldogz.core.designsystem.component.AppLinkAlarmIconButton
import com.oldogz.core.designsystem.component.AppLinkAlarmSwitch
import com.oldogz.core.designsystem.component.AppLinkAlarmTextField
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.designsystem.theme.BitterSweet
import com.oldogz.core.designsystem.theme.NeonBlue
import com.oldogz.core.designsystem.theme.Paddings
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.DayOfWeek
import com.oldogz.core.model.PeriodOfDay
import kotlinx.collections.immutable.ImmutableList
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
    val coroutineScope = rememberCoroutineScope()
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = 5)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = 30)
    val periodOfDayState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    LaunchedEffect(Unit) {
        alarmEditViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    LaunchedEffect(Unit) {
        alarmEditViewModel.event.collect { event ->
            when (event) {
                is AlarmEditUiEvent.AlarmEditComplete -> popBackStack()
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
        updateLinkedAppPackage = alarmEditViewModel::updateLinkedAppPackage,
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
        updateDirectAppLaunch = alarmEditViewModel::updateDirectAppLaunch,
        updateVibrate = alarmEditViewModel::updateVibrate,
        selectAppDialog = alarmEditViewModel::selectAppDialog,
        saveAlarm = alarmEditViewModel::saveAlarm
    )
}

@Composable
private fun AlarmEditContent(
    alarmEditUiState: AlarmEditUiState,
    paddingValues: PaddingValues,
    popBackStack: () -> Unit,
    updateLinkedAppPackage: (String) -> Unit,
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
    updateDirectAppLaunch: (Boolean) -> Unit,
    updateVibrate: (Boolean) -> Unit,
    selectAppDialog: () -> Unit,
    saveAlarm: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.background),
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
                    "New Alarm"
                } else {
                    "Edit Alarm"
                },
                navigationIcon = {
                    AppLinkAlarmIconButton(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
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
                    linkedAppPackage = alarmEditUiState.linkedAppPackage,
                    selectAppDialog = selectAppDialog
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
                    directAppLaunch = alarmEditUiState.directAppLaunch,
                    vibrate = alarmEditUiState.vibrate,
                    updateAlarmMode = {
                        coroutineScope.launch {
                            updateAlarmMode()
                            delay(10)
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    },
                    updateDirectAppLaunch = updateDirectAppLaunch,
                    updateVibrate = updateVibrate,
                )
            }

            AppLinkAlarmButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Paddings.large),
                content = "Save",
                enabled = (alarmEditUiState.linkedAppPackage != null &&
                        alarmEditUiState.dayOfWeek.isNotEmpty() &&
                        alarmEditUiState.alarmName.isNotEmpty() &&
                        alarmEditUiState.message.isNotEmpty()),
                onClick = saveAlarm
            )
        }
    }

    if (alarmEditUiState.selectAppDialog) {
        AppSelectDialog(
            updateLinkedAppPackage = updateLinkedAppPackage,
            onDismiss = selectAppDialog
        )
    }
}

@Composable
internal fun ChooseApp(
    linkedAppPackage: String?,
    selectAppDialog: () -> Unit,
) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
            text = "App",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        if (linkedAppPackage != null) {
            val appInfo = try {
                packageManager.getApplicationInfo(linkedAppPackage, 0)
            } catch (e: Exception) {
                null
            }
            val label = appInfo?.loadLabel(packageManager) ?: "Not Found"
            val icon = appInfo?.loadIcon(packageManager)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .clickable { selectAppDialog() }
                    .padding(Paddings.xlarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    AppLinkAlarmAsyncImage(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        drawable = icon,
                        contentDescription = "$label icon",
                    )
                    Column(
                        modifier = Modifier.padding(start = Paddings.xlarge),
                    ) {
                        Text(
                            text = label.toString(),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            modifier = Modifier.padding(top = Paddings.small),
                            text = appInfo?.packageName ?: "Not Found",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectAppDialog() }
                    .padding(start = Paddings.xlarge)
                    .padding(vertical = Paddings.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier,
                    text = "Choose App",
                    style = MaterialTheme.typography.bodyLarge
                )

                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Choose App",
                    onClick = selectAppDialog
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
            text = "Time",
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
                WheelPicker(
                    modifier = Modifier.weight(1f),
                    state = periodOfDayState,
                    list = listOf("AM", "PM"),
                    itemHeight = 50.dp,
                    selectedItem = {
                        it?.let { periodOfDay ->
                            if (periodOfDay == "AM") {
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
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
            text = "Repeat Days",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = "S",
                labelColor = BitterSweet,
                enabled = true,
                selected = DayOfWeek.SUNDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.SUNDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = "M",
                enabled = true,
                selected = DayOfWeek.MONDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.MONDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = "T",
                enabled = true,
                selected = DayOfWeek.TUESDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.TUESDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = "W",
                enabled = true,
                selected = DayOfWeek.WEDNESDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.WEDNESDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = "T",
                enabled = true,
                selected = DayOfWeek.THURSDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.THURSDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = "F",
                enabled = true,
                selected = DayOfWeek.FRIDAY in dayOfWeek,
                onClick = { updateDayOfWeek(DayOfWeek.FRIDAY) }
            )
            AppLinkAlarmFilterChip(
                modifier = Modifier,
                labelText = "S",
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
            text = "Alarm Info",
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
            placeholder = "Alarm Name",
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
            placeholder = "Message",
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
    directAppLaunch: Boolean,
    vibrate: Boolean,
    updateAlarmMode: () -> Unit,
    updateDirectAppLaunch: (Boolean) -> Unit,
    updateVibrate: (Boolean) -> Unit,
) {
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
            val tooltipState = rememberTooltipState()
            Text(
                modifier = Modifier,
                text = "Alarm Mode",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            TooltipBox(
                modifier = Modifier,
                positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                tooltip = {
                    RichTooltip(
                        title = { Text("Alarm Mode") },
                        colors = TooltipDefaults.richTooltipColors().copy(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            titleContentColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text("Flexible Alarm: At the set time, you will receive a notification on your phone. Tapping the notification will open a specific app.\n\nInstant Alarm: At the set time, an alarm will sound with a ringtone. You can open a specific app when dismissing the alarm.\n\nDirect App Launch: At the set time, the app will open automatically.")
                    }
                },
                state = tooltipState
            ) {
                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Alarm Mode Info",
                    onClick = {
                        coroutineScope.launch {
                            tooltipState.show()
                        }
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { updateAlarmMode() }
                .padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    modifier = Modifier,
                    text = "Mode",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    modifier = Modifier.padding(top = Paddings.small),
                    text = if (alarmMode == AlarmMode.INSTANT) {
                        "Instant Alarm Mode"
                    } else {
                        "Flexible Alarm Mode"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { updateDirectAppLaunch(!directAppLaunch) }
                .padding(horizontal = Paddings.xlarge, vertical = Paddings.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    modifier = Modifier,
                    text = "Direct App Launch",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    modifier = Modifier.padding(top = Paddings.small),
                    text = if (directAppLaunch) {
                        "On"
                    } else {
                        "Off"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                )
            }

            AppLinkAlarmSwitch(
                modifier = Modifier,
                checked = directAppLaunch,
                onCheckedChange = updateDirectAppLaunch
            )
        }

        AnimatedVisibility(
            visible = alarmMode == AlarmMode.INSTANT,
            enter = slideInVertically { fullHeight -> -fullHeight },
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = Paddings.xlarge, vertical = Paddings.large),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            modifier = Modifier,
                            text = "Alarm Sound",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            modifier = Modifier.padding(top = Paddings.small),
                            text = "Off",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }
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
                            text = "Vibrate",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            modifier = Modifier.padding(top = Paddings.small),
                            text = if (vibrate) {
                                "On"
                            } else {
                                "Off"
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AlarmEditContentPreview() {
    AppLinkAlarmTheme {
        AlarmEditContent(
            alarmEditUiState = AlarmEditUiState(
                linkedAppPackage = null
            ),
            paddingValues = PaddingValues(),
            popBackStack = {},
            updateLinkedAppPackage = {},
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
            updateDirectAppLaunch = {},
            updateVibrate = {},
            selectAppDialog = {},
            saveAlarm = {}
        )
    }
}