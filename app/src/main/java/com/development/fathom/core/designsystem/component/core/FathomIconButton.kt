package com.development.fathom.core.designsystem.component.core

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.development.fathom.core.designsystem.theme.FathomElevation
import com.development.fathom.core.designsystem.theme.FathomMotion
import com.development.fathom.core.designsystem.theme.FathomRadius
import com.development.fathom.core.designsystem.theme.FathomTheme

enum class FathomIconButtonSize(val target: Dp, val iconSize: Dp) {
    Small(40.dp, 19.dp),
    Medium(48.dp, 22.dp),
    Large(56.dp, 26.dp)
}

enum class FathomIconButtonVariant { Plain, Tonal, Filled, Raised }

/**
 * DESIGN SPEC — FathomIconButton (components/core/IconButton.jsx → .fa-icon-btn).
 *
 * A round icon target for chrome and transport controls. The search and bell icons
 * in the Library top bar, the three-dot menu on every track row, the back arrow,
 * shuffle and repeat on Now Playing.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * LAYOUT
 * ═══════════════════════════════════════════════════════════════════════════
 * - A Box, square, contentAlignment = Alignment.Center.
 * - shape RoundedCornerShape(FathomRadius.button)   // pill — a circle when square
 *
 * SIZE — target size and glyph size travel together, same enum pattern as the button:
 *   Sm  → 40.dp target, 19.dp icon
 *   Md  → 48.dp target, 22.dp icon   ← default, Android's minimum touch target
 *   Lg  → 56.dp target, 26.dp icon
 *
 * - Icon stroke width 1.75 (the Lucide house stroke). Only relevant once you import
 *   the real Lucide vectors; Material icons ignore it.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * VARIANTS
 * ═══════════════════════════════════════════════════════════════════════════
 * Plain (default)
 *   background  none — fully transparent
 *   content     FathomTheme.colors.fgSecondary
 *   no shadow
 *
 * Tonal
 *   background  FathomTheme.colors.currentWash
 *   content     FathomTheme.colors.textAccent
 *   no shadow
 *
 * Filled
 *   background  the teal gradient — currentLight 0% → current 52% → currentDeep 100%
 *   content     FathomTheme.colors.currentOn
 *   glow        Modifier.shadow(10.dp, pill, current @ GlowAlpha.current)
 *
 * Raised
 *   background  FathomTheme.colors.bgRaised
 *   content     FathomTheme.colors.fgPrimary
 *   shadow      FathomElevation.card (2.dp)
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * ACTIVE — a toggled-on state, independent of variant
 * ═══════════════════════════════════════════════════════════════════════════
 * When active = true, ONLY the content colour changes:
 *   content → FathomTheme.colors.textAccent
 *
 * The background is untouched. Use for shuffle, repeat, and loved — controls that
 * stay lit once engaged.
 *
 * Note: in the CSS, `--on` sits after `--filled`, so active technically overrides
 * a filled button's currentOn with textAccent. That reads badly on a gradient and
 * is almost certainly not intended. Treat Filled + active as "leave the content
 * colour alone" and move on.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * DISABLED
 * ═══════════════════════════════════════════════════════════════════════════
 *   content     FathomTheme.colors.fgDisabled
 *   background  removed entirely — no gradient, no wash, no raised fill
 *   no shadow, no press scale
 *
 * Same collapse-everything pattern as FathomButton.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * PRESS — two things change, not one
 * ═══════════════════════════════════════════════════════════════════════════
 * 1. scale 0.9 via graphicsLayer
 *    tween(FathomMotion.quick, easing = FathomMotion.settle)
 *
 *    Note 0.9 — more pronounced than the button's 0.972 or the chip's 0.95.
 *    The smaller the target, the harder the shrink has to work to register.
 *
 * 2. background becomes FathomTheme.colors.bgPress
 *
 * The background change is new — your button and chip only scaled. For a Plain
 * icon button with no background at all, that momentary bgPress fill IS the
 * feedback; the scale alone wouldn't be enough on a transparent target.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * MOTION
 * ═══════════════════════════════════════════════════════════════════════════
 *   scale                   → tween(FathomMotion.quick, FathomMotion.settle)  140ms
 *   background, colour      → tween(FathomMotion.base,  FathomMotion.glide)   240ms
 *
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * @param icon     the glyph. Required — this component is nothing without it.
 * @param label    content description for accessibility. ALWAYS pass one; this is
 *                 the only component in the set with no visible text to fall back on.
 * @param onClick  the action.
 * @param modifier applied to the Box.
 * @param variant  Plain | Tonal | Filled | Raised. Default Plain.
 * @param size     Sm | Md | Lg. Default Md.
 * @param active   true → content tinted textAccent. For shuffle, repeat, loved.
 * @param enabled  false → the disabled treatment above, clicks ignored.
 */

@Preview
@Composable
fun FathomIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Search,
    label: String = "Fathom",
    variant: FathomIconButtonVariant = FathomIconButtonVariant.Plain,
    size: FathomIconButtonSize = FathomIconButtonSize.Medium,
    active: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit = { }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentLight = FathomTheme.colors.currentLight
    val current = FathomTheme.colors.current
    val currentDeep = FathomTheme.colors.currentDeep

    val content = when (variant) {
        FathomIconButtonVariant.Plain -> FathomTheme.colors.fgSecondary
        FathomIconButtonVariant.Tonal -> FathomTheme.colors.textAccent
        FathomIconButtonVariant.Filled -> FathomTheme.colors.currentOn
        FathomIconButtonVariant.Raised -> FathomTheme.colors.fgPrimary
    }

    val glow = when (variant) {
        FathomIconButtonVariant.Filled -> Modifier.shadow(
            10.dp,
            RoundedCornerShape(FathomRadius.button),
            ambientColor = FathomTheme.colors.current.copy(FathomElevation.GlowAlpha.current),
            spotColor = FathomTheme.colors.current.copy(FathomElevation.GlowAlpha.current)
        )

        FathomIconButtonVariant.Raised -> Modifier.shadow(
            FathomElevation.card,
            RoundedCornerShape(FathomRadius.button)
        )

        else -> Modifier
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(FathomMotion.quick, easing = FathomMotion.settle)
    )

    val contentColor by animateColorAsState(
        targetValue = if (active && variant != FathomIconButtonVariant.Filled) FathomTheme.colors.textAccent else content,
        tween(FathomMotion.base, easing = FathomMotion.glide)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed && variant != FathomIconButtonVariant.Filled) FathomTheme.colors.bgPress
        else if (variant == FathomIconButtonVariant.Tonal) FathomTheme.colors.currentWash
        else if (variant == FathomIconButtonVariant.Raised) FathomTheme.colors.bgRaised
        else Color.Transparent,
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide)
    )

    Box(
        modifier = Modifier
            .size(size.target)
            .then(glow)
            .clip(RoundedCornerShape(FathomRadius.button))
            .then(
                when (variant) {
                    FathomIconButtonVariant.Filled -> Modifier.drawWithCache {
                        val brush = Brush.linearGradient(
                            Pair(0f, currentLight),
                            Pair(0.52f, current),
                            Pair(1f, currentDeep),
                            end = Offset(this.size.width, this.size.height)
                        )
                        onDrawBehind {
                            drawRoundRect(
                                brush,
                                cornerRadius = CornerRadius(FathomRadius.button.toPx())
                            )
                        }
                    }

                    else -> Modifier.background(backgroundColor)
                })
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                enabled = enabled,
                onClick = { onClick() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            label,
            modifier = Modifier
                .size(size.iconSize),
            tint = contentColor
        )
    }
}