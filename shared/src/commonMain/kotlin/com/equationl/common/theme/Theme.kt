package com.equationl.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Grey500,
    primaryVariant = Grey700,
    secondary = Grey200,
    secondaryVariant = Grey700
    //background = Color.LightGray
)

private val LightColorPalette = lightColors(
    primary = Amber500,
    primaryVariant = Amber700,
    secondary = AmberA200,
    secondaryVariant = Color.Unspecified
    //background = Color.White,
)

@Composable
fun CalculatorComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}