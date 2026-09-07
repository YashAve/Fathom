package com.development.fathom.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing

object FathomMotion {

    /** Default enter/exit — fast start, long tail. */
    val glide   = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
    /** Symmetric in-out, for shape morphs. */
    val morph   = CubicBezierEasing(0.65f, 0f,    0.35f, 1f)
    /** Content rising into place. */
    val rise    = CubicBezierEasing(0.16f, 1f,    0.30f, 1f)
    /** One gentle overshoot — settle into position. */
    val settle  = CubicBezierEasing(0.34f, 1.22f, 0.64f, 1f)
    /** Leaving, unhurried. */
    val recede  = CubicBezierEasing(0.40f, 0.02f, 0.20f, 1f)
    /** Ring expansion. */
    val ripple  = CubicBezierEasing(0.12f, 0.72f, 0.24f, 1f)
    /** Endless breathing loops. */
    val tide    = CubicBezierEasing(0.45f, 0.05f, 0.55f, 0.95f)

    // ── Durations ────────────────────────────────────────────────────────────
    const val instant  = 90
    const val quick    = 140   // press feedback
    const val base     = 240   // hover, chip, toggle
    const val slow     = 420   // screen entry element
    const val sheet    = 520   // bottom sheet, dialog
    const val theme    = 520   // light ↔ dark cross-fade
    const val palette  = 600   // track change palette cross-fade
    const val expand   = 640   // mini player → Now Playing
    const val durRipple  = 3200
    const val wave     = 9_000
    const val blob     = 14_000
    const val blobSlow = 21_000
    const val mesh     = 26_000
    const val drift    = 16_000

    // ── Staggers ─────────────────────────────────────────────────────────────
    const val staggerItem    = 60    // list items, onboarding text blocks
    const val staggerRipple  = 600   // between concentric rings
    const val staggerControl = 90    // transport controls fading up in sequence
}