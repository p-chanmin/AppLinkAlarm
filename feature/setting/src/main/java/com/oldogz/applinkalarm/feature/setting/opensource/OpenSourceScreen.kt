package com.oldogz.applinkalarm.feature.setting.opensource

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.oldogz.applinkalarm.feature.setting.R
import com.oldogz.core.designsystem.component.AppLinkAlarmIconButton
import com.oldogz.core.designsystem.component.AppLinkAlarmTopAppBar
import com.oldogz.core.designsystem.theme.AppLinkAlarmTheme
import com.oldogz.core.firebase.LocalFirebaseManager

@Composable
fun OpenSourceScreen(
    paddingValues: PaddingValues,
    popBackStack: () -> Unit,
) {
    val firebaseManager = LocalFirebaseManager.current
    val configuration = LocalConfiguration.current

    LaunchedEffect(Unit) {
        firebaseManager.screenLogEvent("OpenSourceScreen", configuration.orientation)
    }

    val libraries by rememberLibraries(R.raw.aboutlibraries)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
    ) {
        AppLinkAlarmTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.feature_setting_text_open_source_license),
            navigationIcon = {
                AppLinkAlarmIconButton(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.feature_setting_icon_description_close),
                    onClick = popBackStack
                )
            },
            actions = {}
        )

        LibrariesContainer(libraries, Modifier.fillMaxSize())
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OpenSourceScreenPreview() {
    AppLinkAlarmTheme {
        OpenSourceScreen(
            paddingValues = PaddingValues(),
            popBackStack = {},
        )
    }
}