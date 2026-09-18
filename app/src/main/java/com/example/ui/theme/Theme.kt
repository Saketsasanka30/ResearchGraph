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
    primary = AcademicIndigo,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = AcademicIndigoDark,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,
    secondary = AcademicTeal,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    secondaryContainer = AcademicTealDark,
    onSecondaryContainer = androidx.compose.ui.graphics.Color.White,
    tertiary = AcademicAmber,
    onTertiary = androidx.compose.ui.graphics.Color.Black,
    background = SlateNavyDark,
    onBackground = TextPrimaryDark,
    surface = SlateNavyCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SlateNavyElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = SlateBorderDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AcademicIndigoDark,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFEEF2FF),
    onPrimaryContainer = AcademicIndigoDark,
    secondary = AcademicTealDark,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFCCFBF1),
    onSecondaryContainer = AcademicTealDark,
    tertiary = AcademicAmberDark,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    background = ParchmentLight,
    onBackground = TextPrimaryLight,
    surface = ParchmentCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = ParchmentBorderLight,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
