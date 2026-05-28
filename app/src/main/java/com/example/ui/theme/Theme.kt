package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = UmairaRed,
    secondary = UmairaDarkRed,
    tertiary = AuraGold,
    background = UmairaRichBlack,
    surface = UmairaCardGray,
    onPrimary = UmairaWhite,
    onSecondary = UmairaWhite,
    onBackground = UmairaWhite,
    onSurface = UmairaWhite
  )

private val LightColorScheme = DarkColorScheme // Always premium cinematic dark theme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force premium dark theme
  dynamicColor: Boolean = false, // Preserve brand identity strictly
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
