package com.development.fathom.core.designsystem.component.core

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development.fathom.core.designsystem.theme.FathomElevation
import com.development.fathom.core.designsystem.theme.FathomMotion
import com.development.fathom.core.designsystem.theme.FathomRadius
import com.development.fathom.core.designsystem.theme.FathomTheme

/**
 * DESIGN SPEC — FathomPaginationDots (components/core/PaginationDots.jsx → .fa-dots).
 *
 * Onboarding progress. Three dots where the active one stretches into a wide pill.
 * Only used in the onboarding flow — nowhere else in the app.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * LAYOUT
 * ═══════════════════════════════════════════════════════════════════════════
 * - A Row, vertically centred.
 * - Gap between dots: 8.dp  (--fa-space-2).
 * - Row wraps its content — no fillMaxWidth. The dots sit centred by the parent.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * EACH DOT
 * ═══════════════════════════════════════════════════════════════════════════
 * INACTIVE
 * - width  8.dp
 * - height 8.dp
 * - shape  RoundedCornerShape(FathomRadius.button)   // pill — a circle at 8dp
 * - colour flat FathomTheme.colors.fgDisabled
 * - no shadow
 *
 * ACTIVE
 * - width  30.dp   ← the only dimension that changes
 * - height 8.dp    ← unchanged
 * - shape  same pill
 * - background: the SAME teal gradient you built for FathomButton —
 *   currentLight 0% → current 52% → currentDeep 100%, 135°.
 * - carries the current glow beneath it:
 *   Modifier.shadow(
 *       elevation    = 10.dp,
 *       shape        = RoundedCornerShape(FathomRadius.button),
 *       ambientColor = colors.current.copy(alpha = FathomElevation.GlowAlpha.current),
 *       spotColor    = same
 *   )
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * ANIMATION — the whole point of this component
 * ═══════════════════════════════════════════════════════════════════════════
 * The width animates from 8.dp to 30.dp over FathomMotion.slow (420ms) on
 * FathomMotion.settle — the easing with the gentle overshoot. The dot slightly
 * overshoots 30.dp and settles back.
 *
 *   val width by animateDpAsState(
 *       targetValue   = if (isActive) 30.dp else 8.dp,
 *       animationSpec = tween(FathomMotion.slow, easing = FathomMotion.settle)
 *   )
 *
 * Note this is animateDpAsState, not animateFloatAsState — you're animating a
 * dimension, not a scale. Unlike the button's graphicsLayer scale, this one DOES
 * change layout: the row genuinely gets wider as the pill grows.
 *
 * The colour also animates — fgDisabled to the gradient — over the same 420ms on
 * FathomMotion.morph. Since one side is a flat colour and the other is a Brush,
 * the simplest honest approach is to animate the ALPHA of the gradient layer over
 * the flat one rather than trying to interpolate a Color into a Brush.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * INTERACTION  (optional)
 * ═══════════════════════════════════════════════════════════════════════════
 * - When onSelect is null, the dots are decorative — no clickable at all.
 * - When onSelect is provided, each dot is clickable with indication = null
 *   and calls onSelect(i).
 * - No press scale on these. They're too small for it to read.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * @param count    number of dots. Default 3 (the onboarding flow).
 * @param index    zero-based active index.
 * @param modifier applied to the Row.
 * @param onSelect null → decorative. Non-null → each dot is tappable.
 */

@Preview
@Composable
fun FathomPaginationDots(
    modifier: Modifier = Modifier,
    count: Int = 3,
    index: Int = 0,
    onSelect: ((Int) -> Unit)? = null
) {

    Row(
        modifier = modifier
            .wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val currentLight = FathomTheme.colors.currentLight
        val current = FathomTheme.colors.current
        val currentDeep = FathomTheme.colors.currentDeep

        repeat(count) { i ->

            val isActive = i == index

            val width by animateDpAsState(
                targetValue = if (isActive) 30.dp else 8.dp,
                animationSpec = tween(FathomMotion.slow, easing = FathomMotion.settle)
            )

            Box(
                modifier = modifier
                    .width(width)
                    .height(8.dp)
                    .then(
                        if (isActive) {
                            Modifier.shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(FathomRadius.button),
                                ambientColor = current.copy(alpha = FathomElevation.GlowAlpha.current),
                                spotColor = current.copy(alpha = FathomElevation.GlowAlpha.current)
                            )
                        } else Modifier
                    )
                    .clip(RoundedCornerShape(FathomRadius.button))
                    .then(
                        if (isActive) {
                            Modifier.drawWithCache {
                                val brush = Brush.linearGradient(
                                    Pair(0f, currentLight),
                                    Pair(0.52f, current),
                                    Pair(1f, currentDeep),
                                    end = Offset(size.width, size.height)
                                )
                                onDrawBehind {
                                    drawRoundRect(
                                        brush,
                                        cornerRadius = CornerRadius(FathomRadius.button.toPx())
                                    )
                                }
                            }
                        } else {
                            Modifier.background(FathomTheme.colors.fgDisabled)
                        }
                    )
                    .then(
                        if (onSelect != null) {
                            Modifier.clickable(
                                enabled = true,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onSelect(i) }
                            )
                        } else Modifier
                    )
            )
        }
    }
}