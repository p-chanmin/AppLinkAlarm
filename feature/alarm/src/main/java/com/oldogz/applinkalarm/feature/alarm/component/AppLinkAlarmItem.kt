package com.oldogz.applinkalarm.feature.alarm.component

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oldogz.applinkalarm.feature.alarm.util.alarmModeToString
import com.oldogz.applinkalarm.feature.alarm.util.dayOfWeekToString
import com.oldogz.core.designsystem.component.AppLinkAlarmSwitch
import com.oldogz.core.designsystem.theme.Paddings
import com.oldogz.core.model.AppLinkAlarm

@Composable
internal fun AppLinkAlarmItem(
    modifier: Modifier = Modifier,
    selectMode: Boolean,
    selected: Boolean,
    navigateToAlarmEdit: (Int) -> Unit,
    appLinkAlarm: AppLinkAlarm,
    updateAlarmActive: (Boolean) -> Unit,
    updateSelectMode: (Boolean, Int?) -> Unit,
    selectAlarm: (Boolean, Int) -> Unit,
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Paddings.small),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .combinedClickable(
                    onClick = {
                        if (!selectMode) {
                            navigateToAlarmEdit(appLinkAlarm.id)
                        } else {
                            selectAlarm(!selected, appLinkAlarm.id)
                        }
                    },
                    onLongClick = {
                        if (!selectMode) {
                            updateSelectMode(true, appLinkAlarm.id)
                        }
                    },
                )
                .padding(Paddings.medium)
                .padding(vertical = Paddings.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (selectMode) {
                Checkbox(
                    modifier = Modifier.padding(end = Paddings.large),
                    checked = selected,
                    onCheckedChange = { selectAlarm(it, appLinkAlarm.id) },
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconImage(
                    linkedAppPackage = appLinkAlarm.linkedAppPackage
                )
                Column(
                    modifier = Modifier.padding(start = Paddings.xlarge),
                ) {
                    Text(
                        text = appLinkAlarm.alarmName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        modifier = Modifier.padding(top = Paddings.small),
                        text = alarmModeToString(
                            context,
                            appLinkAlarm.alarmMode,
                            appLinkAlarm.directAppLaunch
                        ),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                    Text(
                        modifier = Modifier.padding(top = Paddings.small),
                        text = dayOfWeekToString(context, appLinkAlarm.dayOfWeek),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                    val hour = appLinkAlarm.hour.toString().padStart(2, '0')
                    val minute = appLinkAlarm.minute.toString().padStart(2, '0')
                    Text(
                        modifier = Modifier.padding(top = Paddings.small),
                        text = "$hour:$minute ${appLinkAlarm.periodOfDay}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                }
            }
            if (!selectMode) {
                AppLinkAlarmSwitch(
                    modifier = Modifier,
                    checked = appLinkAlarm.active,
                    onCheckedChange = updateAlarmActive
                )
            }
        }
    }
}