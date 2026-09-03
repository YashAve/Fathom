package com.development.fathom.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

@Composable
fun FathomTheme(
    isDarkMode: Boolean = true,
    content: @Composable () -> Unit = { }
) {
    CompositionLocalProvider(LocalFathomColors provides if (isDarkMode) FathomDarkColors else FathomLightColors) {
        MaterialTheme(
            typography = Typography,
            content = content
        )
    }
}