package com.oldogz.applinkalarm.feature.alarm.component

import android.R.attr.text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.analytics.logEvent
import com.oldogz.applinkalarm.feature.alarm.R
import com.oldogz.core.designsystem.component.AppLinkAlarmButton
import com.oldogz.core.designsystem.theme.Paddings
import com.oldogz.core.firebase.LocalFirebaseManager
import com.oldogz.core.firebase.model.FA
import com.oldogz.core.model.AlarmMode
import com.oldogz.core.model.LinkTarget
import com.oldogz.core.model.PeriodOfDay

@Composable
internal fun OpenAppInfo(
    alarmName: String,
    alarmMessage: String,
    hour: Int,
    minute: Int,
    alarmMode: AlarmMode,
    periodOfDay: PeriodOfDay,
    linkTarget: LinkTarget,
    onClick: () -> Unit,
) {
    val firebaseManager = LocalFirebaseManager.current

    Column(
        modifier = Modifier.padding(horizontal = Paddings.xlarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (val target = linkTarget) {
            is LinkTarget.App -> {
                AppIconImage(
                    linkedAppPackage = target.packageName,
                    size = 64.dp
                )
            }

            is LinkTarget.Url -> {

            }
        }

        val hourText = hour.toString().padStart(2, '0')
        val minuteText = minute.toString().padStart(2, '0')
        Text(
            modifier = Modifier.padding(vertical = Paddings.large),
            text = "$hourText:$minuteText $periodOfDay",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = alarmName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            modifier = Modifier.padding(vertical = Paddings.large),
            text = alarmMessage,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        AppLinkAlarmButton(
            modifier = Modifier
                .fillMaxWidth(),
            content = when (alarmMode) {
                AlarmMode.STANDARD -> stringResource(R.string.feature_alarm_text_dismiss_and_open_app)
                AlarmMode.NOTIFICATION_ONLY -> stringResource(R.string.feature_alarm_text_open_app)
            },
            onClick = {
                firebaseManager.firebaseAnalytics.logEvent(FA.Event.LINKED_APP_OPEN) {
                    param(FA.Param.Key.ALARM_MODE, alarmMode.name)
                }
                onClick()
            }
        )
    }
}