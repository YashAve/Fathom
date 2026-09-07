package com.development.fathom.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

val FathomTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 32.sp,
        lineHeight = (1.1).em,
        letterSpacing = (-0.025).em,
        fontFamily = FathomFonts.spaceGrotesk,
        fontWeight = FontWeight.W600
    ),
    displayMedium = TextStyle(
        fontSize = 26.sp,
        lineHeight = (1.15).em,
        letterSpacing = (-0.02).em,
        fontFamily = FathomFonts.spaceGrotesk,
        fontWeight = FontWeight.W600
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = (1.2).em,
        letterSpacing = (-0.015).em,
        fontFamily = FathomFonts.spaceGrotesk,
        fontWeight = FontWeight.W600
    ),
    bodyLarge = TextStyle(
        fontSize = 17.sp,
        lineHeight = (1.5).em,
        letterSpacing = 0.em,
        fontFamily = FathomFonts.spaceGrotesk,
        fontWeight = FontWeight.W400
    ),
    bodyMedium = TextStyle(
        fontSize = 15.sp,
        lineHeight = (1.5).em,
        letterSpacing = 0.em,
        fontFamily = FathomFonts.spaceGrotesk,
        fontWeight = FontWeight.W400
    ),
    bodySmall = TextStyle(
        fontSize = 13.sp,
        lineHeight = (1.45).em,
        letterSpacing = 0.em,
        fontFamily = FathomFonts.spaceGrotesk,
        fontWeight = FontWeight.W600
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = (1.2).em,
        letterSpacing = 0.em,
        fontFamily = FathomFonts.spaceGrotesk,
        fontWeight = FontWeight.W600
    ),
    titleMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = (1.2).em,
        letterSpacing = (0.04).em,
        fontFamily = FathomFonts.jetbrainsMono,
        fontWeight = FontWeight.W500
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = (1.35).em,
        letterSpacing = (0.06).em,
        fontFamily = FathomFonts.jetbrainsMono,
        fontWeight = FontWeight.W500
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        lineHeight = (1.3).em,
        letterSpacing = (0.16).em,
        fontFamily = FathomFonts.jetbrainsMono,
        fontWeight = FontWeight.W500
    )
)