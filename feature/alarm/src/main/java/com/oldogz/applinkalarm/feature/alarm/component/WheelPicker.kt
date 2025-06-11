package com.oldogz.applinkalarm.feature.alarm.component

import android.content.res.Configuration
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme

@Composable
internal fun <T> WheelPicker(
    modifier: Modifier = Modifier,
    state: LazyListState,
    list: List<T>,
    itemHeight: Dp,
    selectedItem: (T?) -> Unit,
) {
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = state)
    val firstVisibleItemIndex by remember { derivedStateOf { state.firstVisibleItemIndex } }

    LaunchedEffect(firstVisibleItemIndex) {
        selectedItem(list.getOrNull(firstVisibleItemIndex))
    }

    LazyColumn(
        state = state,
        modifier = modifier.height(itemHeight * 3),
        flingBehavior = flingBehavior,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Box(
                modifier = Modifier.height(itemHeight)
            )
        }
        itemsIndexed(list, key = { i, _ -> i }) { i, item ->
            Box(
                modifier = Modifier.height(itemHeight), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.toString(),
                    style = if (i == firstVisibleItemIndex) {
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                )
            }
        }
        item {
            Box(
                modifier = Modifier.height(itemHeight)
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WheelPickerPreview() {
    AppLinkAlarmTheme {
        val state = rememberLazyListState()
        WheelPicker(
            modifier = Modifier.width(100.dp),
            state = state,
            list = (1..12).toList(),
            itemHeight = 50.dp,
            selectedItem = {}
        )
    }
}