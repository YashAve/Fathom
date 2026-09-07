package com.development.fathom.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val FathomShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // --fa-radius-xs
    small = RoundedCornerShape(12.dp),  // --fa-radius-sm
    medium = RoundedCornerShape(20.dp),  // --fa-radius-md
    large = RoundedCornerShape(24.dp),  // --fa-radius-lg
    extraLarge = RoundedCornerShape(28.dp),  // --fa-radius-xl
)

object FathomRadius {
    val card = 28.dp   // --fa-radius-card
    val sheet = 32.dp   // --fa-radius-sheet
    val art = 20.dp   // --fa-radius-art
    val artSm = 14.dp   // --fa-radius-art-sm
    val field = 999.dp  // --fa-radius-field   (pill)
    val chip = 999.dp  // --fa-radius-chip
    val button = 999.dp  // --fa-radius-button
    val dialog = 32.dp   // --fa-radius-dialog
    val xxl = 40.dp   // --fa-radius-3xl
}