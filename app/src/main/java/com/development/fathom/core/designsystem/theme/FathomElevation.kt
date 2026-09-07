package com.development.fathom.core.designsystem.theme

import androidx.compose.ui.unit.dp

object FathomElevation {

    object Light {
        const val sm    = 0.06f   // cards
        const val md    = 0.09f   // floating elements
        const val lg    = 0.12f   // sheets
        const val press = 0.08f   // pressed state
    }

    object Dark {
        const val sm    = 0.45f
        const val md    = 0.55f
        const val lg    = 0.65f
        const val press = 0.50f
    }

    val card   = 2.dp    // --fa-shadow-sm  (offset-y 2px)
    val float  = 6.dp    // --fa-shadow-md  (offset-y 6px)
    val sheet  = 14.dp   // --fa-shadow-lg  (offset-y 14px)
    val press  = 1.dp    // --fa-shadow-press

    object GlowAlpha {
        const val current   = 0.30f
        const val currentLg = 0.34f
        const val warm      = 0.26f
        const val currentDark      = 0.34f
        const val currentDarkShadow = 0.50f
        const val currentDarkLg    = 0.42f
        const val currentDarkLgShadow = 0.55f
        const val warmDark         = 0.32f
        const val warmDarkShadow   = 0.50f
    }

    val glowRadius   = 28.dp   // --fa-glow-current
    val glowRadiusLg = 48.dp   // --fa-glow-current-lg

    val glassBlur = 18.dp      // --fa-blur-glass
}