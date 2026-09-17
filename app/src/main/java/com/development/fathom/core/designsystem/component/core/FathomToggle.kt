package com.development.fathom.core.designsystem.component.core

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development.fathom.core.designsystem.theme.FathomElevation
import com.development.fathom.core.designsystem.theme.FathomMotion
import com.development.fathom.core.designsystem.theme.FathomRadius
import com.development.fathom.core.designsystem.theme.FathomTheme

/**
 * DESIGN SPEC — FathomToggle (components/core/Toggle.jsx → .fa-toggle).
 *
 * The 54×32dp switch. Its headline use is the light/dark theme control in Settings —
 * you've seen it on the Settings screenshot, next to "Dark theme" and "Gapless playback".
 *
 * Always paired with a settings row label. Never a floating switch on its own.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE TRACK
 * ═══════════════════════════════════════════════════════════════════════════
 * - width 54.dp, height 32.dp
 * - padding 3.dp on all sides
 * - shape RoundedCornerShape(FathomRadius.button)   // pill
 * - contentAlignment driven by BiasAlignment (see below)
 *
 * OFF
 *   background  FathomTheme.colors.bgSunken
 *   border      1.dp inset hairline in FathomTheme.colors.borderSubtle
 *               The CSS uses inset box-shadow; in Compose use Modifier.border().
 *   no glow
 *
 * ON
 *   background  the teal gradient — currentLight 0% → current 52% → currentDeep 100%
 *   glow        Modifier.shadow(10.dp, pill, current @ GlowAlpha.current)
 *   no border
 *
 * DISABLED
 *   background  FathomTheme.colors.bgSunken   (same as off)
 *   no border, no glow
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE KNOB
 * ═══════════════════════════════════════════════════════════════════════════
 * - size 26.dp × 26.dp, CircleShape
 * - background FathomTheme.colors.bgRaised
 * - shadow FathomElevation.card (2.dp) — the small one, not the glow
 * - centres its icon
 *
 * Knob content colour:
 *   OFF       FathomTheme.colors.fgTertiary
 *   ON        FathomTheme.colors.currentDeep
 *   DISABLED  knob background becomes FathomTheme.colors.bgPress, no shadow
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE TRAVEL — BiasAlignment
 * ═══════════════════════════════════════════════════════════════════════════
 * The knob sits inside the track Box, positioned by an animated bias:
 *
 *   val bias by animateFloatAsState(
 *       targetValue   = if (checked) 1f else -1f,
 *       animationSpec = tween(FathomMotion.slow, easing = FathomMotion.settle)
 *   )
 *
 *   Box(contentAlignment = BiasAlignment(horizontalBias = bias, verticalBias = 0f))
 *
 * -1f pins the knob left, 1f pins it right, and Compose computes the distance from
 * the remaining space. You never hardcode 22.dp — it falls out of the geometry.
 * This matters because the knob changes width on press (below), which changes the
 * travel distance. BiasAlignment handles that automatically; a fixed offset wouldn't.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE PRESS — the knob STRETCHES, it does not scale
 * ═══════════════════════════════════════════════════════════════════════════
 * While pressed, the knob's WIDTH goes 26.dp → 30.dp. Height stays 26.dp.
 *
 *   val knobWidth by animateDpAsState(
 *       targetValue   = if (isPressed) 30.dp else 26.dp,
 *       animationSpec = tween(FathomMotion.slow, easing = FathomMotion.settle)
 *   )
 *
 * This is NOT graphicsLayer scale. It's a real width change — the knob stops being
 * a circle and becomes a short pill, like a droplet under tension. Use CircleShape
 * anyway; at 30×26 it reads as a rounded capsule, which is the intent.
 *
 * Note the consequence: with a 30.dp knob, travel drops to 54 − 6 − 30 = 18.dp.
 * BiasAlignment recalculates this for free. That's the reason to use it.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE ICON  (optional)
 * ═══════════════════════════════════════════════════════════════════════════
 * - 14.dp, stroke width 2 — smaller than the chip's 16.dp.
 * - iconOn shows when checked, iconOff when not. Either may be null.
 * - Tinted with the knob content colour above.
 * - On the theme switch: moon when on, sun when off.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * INTERACTION
 * ═══════════════════════════════════════════════════════════════════════════
 * - Modifier.clickable on the TRACK, not the knob. The whole 54×32 area is the
 *   tap target.
 * - indication = null, interactionSource shared with the press animation.
 * - enabled = !disabled
 * - onClick calls onChange(!checked) — this component is stateless. It never holds
 *   its own checked value.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * MOTION — everything here is FathomMotion.slow (420ms), not quick
 * ═══════════════════════════════════════════════════════════════════════════
 *   knob travel       → tween(FathomMotion.slow, FathomMotion.settle)
 *   knob stretch      → tween(FathomMotion.slow, FathomMotion.settle)
 *   track background  → tween(FathomMotion.slow, FathomMotion.morph)
 *   knob colour       → tween(FathomMotion.slow, FathomMotion.morph)
 *
 * The toggle is deliberately slower than the button. A press is instant feedback;
 * a switch is a state change, and state changes get room to breathe.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * @param checked  current state. This component is stateless — the caller owns it.
 * @param onChange called with the NEXT value, i.e. !checked.
 * @param modifier applied to the track.
 * @param enabled  false → the disabled treatment above, clicks ignored.
 * @param iconOn   optional 14.dp glyph shown inside the knob when checked.
 * @param iconOff  optional 14.dp glyph shown inside the knob when unchecked.
 */

@Preview
@Composable
fun FathomToggle(
    modifier: Modifier = Modifier,
    checked: Boolean = true,
    onChange: (Boolean) -> Unit = { },
    enabled: Boolean = true,
    iconOn: ImageVector? = Icons.Default.WbSunny,
    iconOff: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentLight = FathomTheme.colors.currentLight
    val current = FathomTheme.colors.current
    val currentDeep = FathomTheme.colors.currentDeep
    val knobContentColor = when {
        !checked && enabled -> FathomTheme.colors.fgTertiary
        checked && enabled -> FathomTheme.colors.currentDeep
        else -> FathomTheme.colors.fgDisabled
    }

    val bias by animateFloatAsState(
        targetValue = if (checked) 1f else -1f,
        animationSpec = tween(FathomMotion.slow, easing = FathomMotion.settle)
    )

    val knobWidth by animateDpAsState(
        targetValue = if (isPressed) 30.dp else 26.dp,
        animationSpec = tween(FathomMotion.slow, easing = FathomMotion.settle)
    )

    Box(
        modifier = Modifier
            .width(54.dp)
            .height(32.dp)
            .then(
                when {
                    !checked && enabled -> Modifier
                    checked && enabled -> Modifier.shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(FathomRadius.button),
                        ambientColor = FathomTheme.colors.current.copy(alpha = FathomElevation.GlowAlpha.current),
                        spotColor = FathomTheme.colors.current.copy(alpha = FathomElevation.GlowAlpha.current)
                    )

                    else -> Modifier
                }
            )
            .then(
                when {
                    !checked && enabled -> Modifier.border(
                        width = 1.dp,
                        color = FathomTheme.colors.borderSubtle,
                        shape = RoundedCornerShape(FathomRadius.button)
                    )

                    else -> Modifier
                }
            )
            .clip(RoundedCornerShape(FathomRadius.button))
            .then(
                when {
                    checked && enabled -> Modifier.drawWithCache {
                        val brush = Brush.linearGradient(
                            Pair(0f, currentLight),
                            Pair(0.52f, current),
                            Pair(1f, currentDeep),
                            end = Offset(size.width, size.height)
                        )

                        onDrawBehind {
                            drawRoundRect(
                                brush = brush,
                                cornerRadius = CornerRadius(FathomRadius.button.toPx())
                            )
                        }
                    }

                    else -> Modifier.background(FathomTheme.colors.bgSunken)
                })
            .then(
                if (enabled) {
                    Modifier.clickable(
                        indication = null,
                        interactionSource = interactionSource,
                        enabled = true,
                        onClick = { onChange(!checked) }
                    )
                } else Modifier
            )
            .padding(3.dp)
    ) {
        Box(
            Modifier
                .width(knobWidth)
                .height(26.dp)
                .shadow(
                    elevation = FathomElevation.card,
                    shape = RoundedCornerShape(FathomRadius.button)
                )
                .clip(RoundedCornerShape(FathomRadius.button))
                .background(FathomTheme.colors.bgRaised)
                .align(BiasAlignment(horizontalBias = bias, verticalBias = 0f)),
            contentAlignment = Alignment.Center
        ) {
            val currentIcon = if (checked) iconOn else iconOff
            if (currentIcon != null) {
                Icon(currentIcon, null, Modifier.size(14.dp), tint = knobContentColor)
            }
        }
    }
}