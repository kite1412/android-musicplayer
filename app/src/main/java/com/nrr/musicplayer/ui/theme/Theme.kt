package com.nrr.musicplayer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SlateBlue,
    secondary = BurntUmber,
    tertiary = MossGreen,
    background = DeepCharcoal,
    surface = DeepCharcoal,
    onPrimary = SoftSilver,
    onSecondary = SoftSilver,
    onTertiary = SoftSilver,
    onBackground = SoftSilver,
    onSurface = SoftSilver,
    primaryContainer = AmberGlow,
    secondaryContainer = WarmCopper,
    tertiaryContainer = GunmetalGray,
    onPrimaryContainer = DeepCharcoal,
    onSecondaryContainer = DeepCharcoal,
    onTertiaryContainer = DeepCharcoal
)

private val LightColorScheme = lightColorScheme(
    primary = SoftSage,
    secondary = SunsetPeach,
    tertiary = DuskyRose,
    background = CreamyWhite,
    surface = CreamyWhite,
    onPrimary = WarmCharcoal,
    onSecondary = WarmCharcoal,
    onTertiary = WarmCharcoal,
    onBackground = WarmCharcoal,
    onSurface = WarmCharcoal,
    primaryContainer = LightMint,
    secondaryContainer = SoftApricot,
    tertiaryContainer = LightTaupe,
    onPrimaryContainer = WarmCharcoal,
    onSecondaryContainer = WarmCharcoal,
    onTertiaryContainer = WarmCharcoal
)

@Composable
fun MusicPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
        typography = Typography.copy(
            bodyLarge = Typography.bodyLarge.copy(
                color = colorScheme.onBackground
            )
        ),
        content = content
    )
}