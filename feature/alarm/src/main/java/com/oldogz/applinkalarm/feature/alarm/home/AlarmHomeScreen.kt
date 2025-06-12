package com.oldogz.applinkalarm.feature.alarm.home

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oldogz.applinkalarm.feature.alarm.component.AppLinkAlarmItem
import com.oldogz.applinkalarm.feature.alarm.model.AlarmHomeUiState
import com.oldogz.applinkalarm.feature.alarm.model.AppLinkAlarmUiState
import com.oldogz.core.designsystem.component.AppLinkAlarmIconButton
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.model.AppLinkAlarm
import com.oldogz.core.model.DayOfWeek
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

    val homeUiState by alarmHomeViewModel.homeUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        alarmHomeViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    AlarmHomeContent(
        homeUiState = homeUiState,
        paddingValues = paddingValues,
        navigateToAlarmEdit = navigateToAlarmEdit,
        navigateToSetting = navigateToSetting,
        updateAlarmActive = alarmHomeViewModel::updateAlarmActive,
        updateSelectMode = alarmHomeViewModel::updateSelectMode,
        selectAlarm = alarmHomeViewModel::selectAlarm,
        selectAllAlarm = alarmHomeViewModel::selectAllAlarm,
        updateSelectedAlarmActive = alarmHomeViewModel::updateSelectedAlarmActive,
        deleteSelectedAlarm = alarmHomeViewModel::deleteSelectedAlarm,
    )
}

@Composable
private fun AlarmHomeContent(
    homeUiState: AlarmHomeUiState,
    paddingValues: PaddingValues,
    navigateToAlarmEdit: (Int?) -> Unit,
    navigateToSetting: () -> Unit,
    updateAlarmActive: (AppLinkAlarm, Boolean) -> Unit,
    updateSelectMode: (Boolean, Int?) -> Unit,
    selectAlarm: (Boolean, Int) -> Unit,
    selectAllAlarm: (Boolean) -> Unit,
    updateSelectedAlarmActive: (Boolean) -> Unit,
    deleteSelectedAlarm: () -> Unit,
) {
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
    if (isSelectMode) {
        AppLinkAlarmTopAppBar(
            modifier = Modifier
                .fillMaxWidth(),
            title = if (alarms.none { it.selected }) {
                "Select Alarms"
            } else {
                "${alarms.count { it.selected }} selected"
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
                                    selectAllAlarm(false)
                                }

                                else -> {
                                    selectAllAlarm(true)
                                }
                            }
                        },
                    )
                    Text(
                        modifier = Modifier.offset(y = (-10).dp),
                        text = "All",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            },
            actions = {
                TextButton(
                    onClick = { updateSelectMode(false, null) }
                ) {
                    Text(
                        text = "Cancel",
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
            title = "AppLink Alarms",
            navigationIcon = {
                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    onClick = navigateToSetting
                )
            },
            actions = {
                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Alarm",
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
                contentDescription = "selected alarm On",
                onClick = { updateSelectedAlarmActive(true) }
            )
            Text(
                modifier = Modifier.offset(y = (-8).dp),
                text = "On",
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
                contentDescription = "selected alarm Off",
                onClick = { updateSelectedAlarmActive(false) }
            )
            Text(
                modifier = Modifier.offset(y = (-8).dp),
                text = "Off",
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
                contentDescription = "selected alarm Delete",
                onClick = deleteSelectedAlarm
            )
            Text(
                modifier = Modifier.offset(y = (-8).dp),
                text = "Delete",
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
                isSelectMode = true,
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
            paddingValues = PaddingValues(),
            navigateToAlarmEdit = {},
            navigateToSetting = {},
            updateAlarmActive = { _, _ -> },
            updateSelectMode = { _, _ -> },
            selectAlarm = { _, _ -> },
            selectAllAlarm = {},
            updateSelectedAlarmActive = {},
            deleteSelectedAlarm = {}
        )
    }
}