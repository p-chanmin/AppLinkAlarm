package com.oldogz.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Malachite,
    onPrimary = EerieBlack,
    background = SeaSalt,
    onBackground = EerieBlack,
    secondary = MintCream,
    onSecondary = SeaGreen,
    onSurface = CambridgeBlue,
    error = BitterSweet
)

private val DarkColorScheme = darkColorScheme(
    primary = Malachite,
    onPrimary = EerieBlack,
    background = DarkGreen,
    onBackground = White,
    secondary = BrunswickGreen,
    onSecondary = CambridgeBlue,
    onSurface = CambridgeBlue,
    error = BitterSweet
)

@Composable
fun AppLinkAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}