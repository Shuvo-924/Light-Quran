package com.shuvo.quran.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = QuranPrimary,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = QuranDarkBackground,
    onBackground = QuranDarkText,
    surface = QuranDarkBackground,
    onSurface = QuranDarkText
)

private val LightColorScheme = lightColorScheme(
    primary = QuranPrimary,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = QuranLightBackground,
    onBackground = Color(0xFF25251F),
    surface = Color.White,
    onSurface = Color(0xFF25251F)
)

private val SepiaColorScheme = lightColorScheme(
    primary = SepiaText,
    secondary = SepiaSecondary,
    background = SepiaBackground,
    onBackground = SepiaText,
    surface = SepiaBackground,
    onSurface = SepiaText
)

private val NightBlueColorScheme = darkColorScheme(
    primary = NightBlueSecondary,
    secondary = NightBlueSecondary,
    background = NightBlueBackground,
    onBackground = NightBlueText,
    surface = NightBlueBackground,
    onSurface = NightBlueText
)

private val ForestGreenColorScheme = darkColorScheme(
    primary = ForestGreenSecondary,
    secondary = ForestGreenSecondary,
    background = ForestGreenBackground,
    onBackground = ForestGreenText,
    surface = ForestGreenBackground,
    onSurface = ForestGreenText
)

@Composable
fun QuranTheme(
    themeName: String = "Light",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeName) {
        "Dark" -> DarkColorScheme
        "Sepia" -> SepiaColorScheme
        "Night Blue" -> NightBlueColorScheme
        "Forest Green" -> ForestGreenColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
