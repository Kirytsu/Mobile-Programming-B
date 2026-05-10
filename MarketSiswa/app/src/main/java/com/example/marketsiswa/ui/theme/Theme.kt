package com.example.marketsiswa.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    primaryContainer = BlueContainer,
    onPrimaryContainer = BlueOnContainer,
    secondary = BlueLight,
    onSecondary = White,
    secondaryContainer = BlueSurface,
    onSecondaryContainer = BlueOnContainer,
    surface = White,
    onSurface = DarkText,
    surfaceVariant = BlueSurface,
    onSurfaceVariant = SubText,
    background = BlueSurface,
    onBackground = DarkText
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueLight,
    onPrimary = BlueOnContainer,
    primaryContainer = BluePrimary,
    onPrimaryContainer = BlueContainer
)

@Composable
fun MarketSiswaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}