package com.crumbatelier.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Scheme = lightColorScheme(
    primary              = ChocolateBrown,
    onPrimary            = LightTextOnDark,
    secondary            = CaramelBrown,
    onSecondary          = LightTextOnDark,
    secondaryContainer   = SoftBeige,
    onSecondaryContainer = BrownText,
    background           = CreamBackground,
    onBackground         = BrownText,
    surface              = CardSurface,
    onSurface            = BrownText,
    surfaceVariant       = SoftBeige,
    onSurfaceVariant     = MutedText,
    outline              = DividerColor,
    error                = ErrorRed,
    onError              = LightTextOnDark,
)

@Composable
fun CrumbAtelierTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            window.statusBarColor = ChocolateBrown.toArgb()
            window.navigationBarColor = ChocolateBrown.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars     = false
                isAppearanceLightNavigationBars = false
            }
        }
    }
    MaterialTheme(
        colorScheme = Scheme,
        typography  = CrumbTypography,
        content     = content
    )
}