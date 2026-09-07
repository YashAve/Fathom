package com.development.fathom.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun FathomTheme(
    isDarkMode: Boolean = true,
    content: @Composable () -> Unit = { }
) {
    CompositionLocalProvider(LocalFathomColors provides if (isDarkMode) FathomDarkColors else FathomLightColors) {
        MaterialTheme(
            typography = FathomTypography,
            content = content
        )
    }
}