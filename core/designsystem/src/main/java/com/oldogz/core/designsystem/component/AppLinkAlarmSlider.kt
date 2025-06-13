package com.oldogz.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLinkAlarmSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    onPositionChangeFinished: () -> Unit = {},
) {
    Slider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onPositionChangeFinished,
        thumb = {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        },
        track = { sliderPositions ->
            val trackHeight = 4.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        RoundedCornerShape(trackHeight / 2)
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(sliderPositions.value / sliderPositions.valueRange.endInclusive)
                    .height(trackHeight)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(trackHeight / 2)
                    )
            )
        }
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AppLinkAlarmSliderPreview() {
    AppLinkAlarmTheme {
        AppLinkAlarmSlider(
            modifier = Modifier,
            value = 0.5f,
            onValueChange = {},
        )
    }
}