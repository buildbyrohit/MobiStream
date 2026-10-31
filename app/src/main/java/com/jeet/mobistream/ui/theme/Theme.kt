package com.jeet.mobistream.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkAmoledColorScheme = darkColorScheme(
    primary = CrimsonRed,
    onPrimary = TextPureWhite,
    secondary = LiquidRuby,
    background = AmoledBlack,
    surface = PureSurface,
    onBackground = TextPureWhite,
    onSurface = TextPureWhite,
    surfaceVariant = GlassSurfaceDark,
    onSurfaceVariant = TextSoftMuted
)

@Composable
fun MobistreamTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkAmoledColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AmoledBlack.toArgb()
            window.navigationBarColor = AmoledBlack.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

