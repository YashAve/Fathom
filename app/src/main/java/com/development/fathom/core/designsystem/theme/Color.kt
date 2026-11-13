package com.development.fathom.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class FathomColors(

    /** True for the light scheme. Branch on this for status-bar icon tint,
     *  blur fallbacks, and anywhere a token can't express the difference. */
    val isLight: Boolean,

    // ── Backgrounds ──────────────────────────────────────────────────────────
    // Pale, barely tinted, and there should be lots of it.
    val bgDeep: Color,
    val bgBase: Color,
    val bgRaised: Color,
    val bgSunken: Color,
    val bgElev: Color,
    val bgPress: Color,

    // ── Ink ──────────────────────────────────────────────────────────────────
    // Deep teal-black, never #000.
    val fgPrimary: Color,
    val fgSecondary: Color,
    val fgTertiary: Color,
    val fgDisabled: Color,

    // ── Current ──────────────────────────────────────────────────────────────
    // The primary accent, aqua.
    val currentDeep: Color,
    val current: Color,
    val currentLight: Color,
    val currentWash: Color,
    val currentOn: Color,

    // ── Warmth ───────────────────────────────────────────────────────────────
    // Coral counterpoint. One warm element per screen, at most.
    val warm: Color,
    val warmLight: Color,
    val warmWash: Color,

    // ── Borders ──────────────────────────────────────────────────────────────
    // Rare; separation comes from space and elevation.
    val borderSubtle: Color,
    val borderDefault: Color,

    // ── Diverging semantic aliases ───────────────────────────────────────────
    val textAccent: Color,
    val scrim: Color,
    val selection: Color,
    val focusRing: Color,

    // ── Raw channels ─────────────────────────────────────────────────────────
    // For composing translucency in either theme — use `.copy(alpha = …)`.
    // `paper` backs the glass gradients; `ink` backs scrims and overlays.
    val ink: Color,
    val paper: Color,

    val glassTop: Color,
    val glassBottom: Color,

    // ── Elevation tint ───────────────────────────────────────────────────────
    // Base colour for shadows. Teal in light, near-black in dark; apply the
    // per-level alpha at the call site (sm .06 / md .09 / lg .12 in light,
    // .45 / .55 / .65 in dark).
    val shadowTint: Color,
) {
    // ── Non-diverging semantic aliases ───────────────────────────────────────
    val textHeading: Color get() = fgPrimary
    val textBody: Color get() = fgSecondary
    val textQuiet: Color get() = fgTertiary
    val surfaceApp: Color get() = bgBase
    val surfaceCard: Color get() = bgRaised
    val surfaceGroove: Color get() = bgSunken
    val surfaceSheet: Color get() = bgElev
}

val FathomLightColors = FathomColors(
    isLight = true,
    bgDeep = Color(0xFFEFF5F3),
    bgBase = Color(0xFFF7FAF9),
    bgRaised = Color(0xFFFFFFFF),
    bgSunken = Color(0xFFEDF3F1),
    bgElev = Color(0xFFFFFFFF),
    bgPress = Color(0xFFE0E9E6),
    fgPrimary = Color(0xFF10201D),
    fgSecondary = Color(0xFF4A5F5A),
    fgTertiary = Color(0xFF8B9E99),
    fgDisabled = Color(0xFFC3D0CC),
    currentDeep = Color(0xFF17847F),
    current = Color(0xFF2FBDB4),
    currentLight = Color(0xFF7BE0C4),
    currentWash = Color(0xFFE4F7F3),
    currentOn = Color(0xFFFFFFFF),
    warm = Color(0xFFFF7A5C),
    warmLight = Color(0xFFFFB8A2),
    warmWash = Color(0xFFFFEDE7),
    borderSubtle = Color(0x0D10201D),   // ink @ 5%
    borderDefault = Color(0x1710201D),  // ink @ 9%
    textAccent = Color(0xFF17847F),     // currentDeep
    scrim = Color(0x4710201D),          // ink @ 28%
    selection = Color(0xFFE4F7F3),      // currentWash
    focusRing = Color(0x522FBDB4),      // current @ 32%
    ink = Color(0xFF10201D),
    paper = Color(0xFFFFFFFF),
    glassTop = Color(0xDBFFFFFF),      // paper @ 86%
    glassBottom = Color(0xB8FFFFFF),   // paper @ 72%
    shadowTint = Color(0xFF17847F),     // currentDeep
)

val FathomDarkColors = FathomColors(
    isLight = false,
    bgDeep = Color(0xFF04100F),
    bgBase = Color(0xFF081917),
    bgRaised = Color(0xFF0F2422),
    bgSunken = Color(0xFF0A1E1C),
    bgElev = Color(0xFF163330),
    bgPress = Color(0xFF1E4340),
    fgPrimary = Color(0xFFE9F5F2),
    fgSecondary = Color(0xFF9CB8B2),
    fgTertiary = Color(0xFF698680),
    fgDisabled = Color(0xFF405854),
    currentDeep = Color(0xFF1F9E96),
    current = Color(0xFF4FD9CE),
    currentLight = Color(0xFF8DF0DF),
    currentWash = Color(0x1F4FD9CE),    // current @ 12%
    currentOn = Color(0xFF04100F),
    warm = Color(0xFFFF8F70),
    warmLight = Color(0xFFFFB29A),
    warmWash = Color(0x1FFF8F70),       // warm @ 12%
    borderSubtle = Color(0x0FE9F5F2),   // paper @ 6%
    borderDefault = Color(0x1AE9F5F2),  // paper @ 10%
    textAccent = Color(0xFF8DF0DF),     // currentLight — diverges from light
    scrim = Color(0x9904100F),          // #04100F @ 60%
    selection = Color(0x2E4FD9CE),      // current @ 18% — not currentWash
    focusRing = Color(0x664FD9CE),      // current @ 40%
    ink = Color(0xFF04100F),
    paper = Color(0xFFE9F5F2),
    glassTop = Color(0xE0163330),      // bgElev @ 88%
    glassBottom = Color(0xC70F2422),   // bgRaised @ 78%
    shadowTint = Color(0xFF020908),     // near-black, not teal, in dark
)

val LocalFathomColors = compositionLocalOf { FathomDarkColors }

object FathomTheme {
    val colors: FathomColors
        @Composable get() = LocalFathomColors.current
}