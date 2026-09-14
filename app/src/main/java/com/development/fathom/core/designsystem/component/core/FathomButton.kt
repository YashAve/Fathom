package com.development.fathom.core.designsystem.component.core

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.development.fathom.core.designsystem.theme.FathomElevation
import com.development.fathom.core.designsystem.theme.FathomMotion
import com.development.fathom.core.designsystem.theme.FathomRadius
import com.development.fathom.core.designsystem.theme.FathomTheme
import com.development.fathom.core.designsystem.theme.FathomTypography

enum class FathomButtonSize(
    val height: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp
) {
    Small(
        height = 40.dp,
        horizontalPadding = 16.dp,
        iconSize = 17.dp
    ),
    Medium(
        height = 52.dp,
        horizontalPadding = 24.dp,
        iconSize = 19.dp
    ),
    Large(
        height = 58.dp,
        horizontalPadding = 32.dp,
        iconSize = 21.dp
    )
}

enum class FathomButtonVariant { Primary, Secondary, Tertiary }

/**
 * DESIGN SPEC — FathomButton (components/core/Button.jsx → .fa-btn--primary).
 *
 * Fathom's pill button, primary variant. The one action a screen exists for —
 * "Start listening", "Log in", "Play". One per screen, never two.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * SHAPE
 * ═══════════════════════════════════════════════════════════════════════════
 * - RoundedCornerShape(FathomRadius.button)   // 999.dp — a true pill.
 *   Water has no corners; this is never a rounded rectangle.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * SIZE  (an enum carrying all four values together, so a call site can't
 *        pair a 40.dp height with 21.dp icons)
 * ═══════════════════════════════════════════════════════════════════════════
 *   Sm  → height 40.dp, horizontal padding 16.dp, text bodySmall  (13.sp), icon 17.dp
 *   Md  → height 52.dp, horizontal padding 24.dp, text bodyMedium (15.sp), icon 19.dp  ← default
 *   Lg  → height 58.dp, horizontal padding 32.dp, text bodyLarge  (17.sp), icon 21.dp
 *
 * - Gap between icon and label: 8.dp.
 * - block = true → Modifier.fillMaxWidth(). Every auth and onboarding CTA uses this.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * TYPE  — three overrides needed
 * ═══════════════════════════════════════════════════════════════════════════
 * The button is SEMIBOLD, but FathomTypography.bodyMedium is Regular W400.
 * It's also single-line, but bodyMedium carries 1.5.em leading. So:
 *
 *   MaterialTheme.typography.bodyMedium.copy(
 *       fontWeight    = FontWeight.W600,   // buttons are always semibold
 *       lineHeight    = 1.em,              // one line, no extra leading
 *       letterSpacing = (-0.005).em        // slight tightening, button-specific
 *   )
 *
 * - maxLines = 1, no wrapping. The CSS sets white-space: nowrap.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * BACKGROUND — a gradient, not a flat colour
 * ═══════════════════════════════════════════════════════════════════════════
 * Brush.linearGradient, 135° (top-left → bottom-right), three stops:
 *
 *   0%    FathomTheme.colors.currentLight   // #7BE0C4 light / #8DF0DF dark
 *   52%   FathomTheme.colors.current        // #2FBDB4 light / #4FD9CE dark
 *   100%  FathomTheme.colors.currentDeep    // #17847F light / #1F9E96 dark
 *
 * Content colour: FathomTheme.colors.currentOn   // white in light, near-black in dark
 *
 * The 135° start/end offsets must be computed from the button's real measured
 * size, so the brush is built inside drawWithCache (or drawBehind) where `size`
 * is available — not in the composable body where it isn't.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * GLOW
 * ═══════════════════════════════════════════════════════════════════════════
 * Primary sits above a soft teal glow — CSS: 0 10px 28px current @ 30%.
 *
 *   Modifier.shadow(
 *       elevation    = 10.dp,
 *       shape        = RoundedCornerShape(FathomRadius.button),
 *       ambientColor = colors.current.copy(alpha = FathomElevation.GlowAlpha.current),
 *       spotColor    = colors.current.copy(alpha = FathomElevation.GlowAlpha.current)
 *   )
 *
 * Note: in DARK the design system stacks two layers — an accent bloom plus a
 * deep shadow beneath. Modifier.shadow draws one layer only. Accept the single
 * layer; a second would need drawBehind and isn't worth it yet.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * STATES  (hover is discarded — it does not exist on Android)
 * ═══════════════════════════════════════════════════════════════════════════
 * NORMAL   as above.
 *
 * PRESSED  - scale 0.972 via Modifier.graphicsLayer { scaleX = s; scaleY = s }
 *            animated with tween(FathomMotion.quick, easing = FathomMotion.settle)
 *          - glow drops to the press shadow:
 *            elevation FathomElevation.press (1.dp),
 *            alpha FathomElevation.Light.press (0.08f) / Dark.press (0.50f)
 *          - NEVER add a scale transform at the call site. It lives in here.
 *
 * DISABLED - the gradient is REMOVED entirely, replaced by flat
 *            FathomTheme.colors.bgSunken
 *          - content FathomTheme.colors.fgDisabled
 *          - no glow, no shadow, no press scale
 *          - every variant looks identical when disabled
 *
 * LOADING  - the LEADING icon is replaced by an 18.dp spinner:
 *            2.dp stroke, ring in colors.paper @ 45%, top arc in contentColor,
 *            one rotation per 900ms on FathomMotion.tide, infinite.
 *          - onClick is ignored while loading (same as disabled).
 *          - the label stays visible; only the icon slot swaps.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * MOTION
 * ═══════════════════════════════════════════════════════════════════════════
 *   scale          → tween(FathomMotion.quick, easing = FathomMotion.settle)   140ms
 *   colour, shadow → tween(FathomMotion.base,  easing = FathomMotion.glide)    240ms
 *   spinner        → 900ms, FathomMotion.tide, infinite
 *
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * @param text      the label. Null gives an icon-only pill.
 * @param onClick   ignored when `enabled` is false or `loading` is true.
 * @param modifier  applied to the outermost element.
 * @param size      Sm | Md | Lg. Default Md.
 * @param block     true → fillMaxWidth. Use for auth and onboarding CTAs.
 * @param enabled   false → the disabled treatment above.
 * @param loading   true → spinner replaces the leading icon, clicks ignored.
 * @param icon      leading icon, drawn before the label.
 * @param iconRight trailing icon, drawn after the label.
 */

@Preview
@Composable
fun FathomButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    text: String? = "Fathom",
    size: FathomButtonSize = FathomButtonSize.Medium,
    block: Boolean = true,
    enabled: Boolean = true,
    loading: Boolean = false,
    variant: FathomButtonVariant = FathomButtonVariant.Primary,
    icon: @Composable () -> Unit = { },
    iconRight: @Composable () -> Unit = { }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (!enabled || loading) 1f else if (isPressed) 0.972f else 1f,
        animationSpec = tween(
            durationMillis = FathomMotion.quick,
            easing = FathomMotion.settle
        )
    )

    val currentLight = FathomTheme.colors.currentLight
    val current = FathomTheme.colors.current
    val currentDeep = FathomTheme.colors.currentDeep
    val contentColor = if (enabled) when (variant) {
        FathomButtonVariant.Primary -> FathomTheme.colors.currentOn
        FathomButtonVariant.Secondary -> FathomTheme.colors.fgPrimary
        FathomButtonVariant.Tertiary -> FathomTheme.colors.textAccent
    } else FathomTheme.colors.fgDisabled

    Box(
        modifier
            .then(if (block) Modifier.fillMaxWidth() else Modifier.wrapContentWidth())
            .wrapContentHeight()
            .then(
                if (variant == FathomButtonVariant.Primary && enabled)
                    Modifier.shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(FathomRadius.button),
                        ambientColor = FathomTheme.colors.current.copy(alpha = FathomElevation.GlowAlpha.current),
                        spotColor = FathomTheme.colors.current.copy(alpha = FathomElevation.GlowAlpha.current)
                    ) else Modifier
            )
            .clip(RoundedCornerShape(FathomRadius.button))
            .then(
                if (!enabled) {
                    Modifier.background(FathomTheme.colors.bgSunken)
                } else {
                    when (variant) {
                        FathomButtonVariant.Primary ->
                            Modifier.drawWithCache {
                                val gradient = Brush.linearGradient(
                                    Pair(0f, currentLight),
                                    Pair(0.52f, current),
                                    Pair(1f, currentDeep),
                                    start = Offset.Zero,
                                    end = Offset(this.size.width, this.size.height)
                                )
                                onDrawBehind {
                                    drawRoundRect(
                                        brush = gradient,
                                        cornerRadius = CornerRadius(FathomRadius.button.toPx())
                                    )
                                }
                            }

                        FathomButtonVariant.Secondary -> Modifier.background(FathomTheme.colors.bgRaised)
                        FathomButtonVariant.Tertiary -> Modifier
                    }
                }
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                enabled = enabled && !loading,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .height(size.height)
                .padding(horizontal = size.horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = contentColor,
                    trackColor = FathomTheme.colors.paper.copy(alpha = 0.45f)
                )
            } else {
                icon()
            }
            if (text != null) {
                Text(
                    text = text,
                    style = FathomTypography.bodyMedium.copy(
                        fontWeight = FontWeight.W600,
                        lineHeight = 1.em,
                        letterSpacing = (-0.005).em
                    ),
                    color = contentColor,
                    maxLines = 1
                )
            }
            iconRight()
        }
    }
}