package com.example.aplikasiumkm.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.aplikasiumkm.utils.TemaKasir

private val LightColorScheme = lightColorScheme(
    primary = HijauTuaPrimary,
    onPrimary = WhiteSurface,
    secondary = OranyeBataAccent,
    onSecondary = WhiteSurface,
    background = KremBackground,
    onBackground = HampirHitamText,
    surface = WhiteSurface,
    onSurface = HampirHitamText,
    error = MerahPeringatanError,
    onError = WhiteSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = HijauTuaPrimary,
    onPrimary = WhiteSurface,
    secondary = OranyeBataAccent,
    onSecondary = WhiteSurface,
    background = HampirHitamText,
    onBackground = KremBackground,
    surface = HampirHitamText,
    onSurface = KremBackground,
    error = MerahPeringatanError,
    onError = WhiteSurface
)

private val AppShapes = Shapes(
    small = TemaKasir.ShapeRadiusKecil,
    medium = TemaKasir.ShapeRadiusKecil,
    large = TemaKasir.ShapeRadiusKecil
)

@Composable
fun APLIKASIUMKMTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our UMKM branding palette by default
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
        shapes = AppShapes,
        typography = Typography,
        content = content
    )
}
