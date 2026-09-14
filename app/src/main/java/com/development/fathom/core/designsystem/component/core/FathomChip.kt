package com.development.fathom.core.designsystem.component.core

import android.graphics.drawable.Icon
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.development.fathom.core.designsystem.theme.FathomElevation
import com.development.fathom.core.designsystem.theme.FathomMotion
import com.development.fathom.core.designsystem.theme.FathomRadius
import com.development.fathom.core.designsystem.theme.FathomTheme
import com.development.fathom.core.designsystem.theme.FathomTypography

/**
 * DESIGN SPEC — FathomChip (components/core/Chip.jsx → .fa-chip).
 *
 * A 38dp filter pill. Search scopes, sort order, genres.
 *
 * You've seen these on the Library screen: "Recent 12", "Loved", "Albums",
 * "Artists" — the row under the greeting. "Recent 12" is the selected one.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * LAYOUT
 * ═══════════════════════════════════════════════════════════════════════════
 * - A Row, vertically centred, wrapping its content (never fillMaxWidth).
 * - height 38.dp
 * - horizontal padding 16.dp   (--fa-space-4)
 * - gap between children 8.dp  (--fa-space-2)
 * - shape RoundedCornerShape(FathomRadius.chip)   // 999.dp pill
 *
 *   [ icon ]  [ label ]  [ count ]
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE LABEL
 * ═══════════════════════════════════════════════════════════════════════════
 * - Style: FathomTypography.bodySmall   // Space Grotesk 13.sp
 * - Weight override: FontWeight.W500    // bodySmall is W400, chips are medium
 * - lineHeight override: 1.em           // single line
 * - maxLines = 1
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE COUNT  (optional)
 * ═══════════════════════════════════════════════════════════════════════════
 * - Style: FathomTypography.labelMedium   // JetBrains Mono 12.sp, W500, 0.06.em
 * - Colour: same as the label's content colour, at 72% alpha
 *   contentColor.copy(alpha = 0.72f)
 * - Only drawn when count is non-null.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE ICON  (optional)
 * ═══════════════════════════════════════════════════════════════════════════
 * - 16.dp, tinted with contentColor.
 * - A @Composable () -> Unit slot, same as FathomButton.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * VARIANTS — unselected only. Selection overrides all three.
 * ═══════════════════════════════════════════════════════════════════════════
 * Current (default)
 *   background  FathomTheme.colors.currentWash
 *   content     FathomTheme.colors.textAccent
 *
 * Neutral
 *   background  FathomTheme.colors.bgSunken
 *   content     FathomTheme.colors.fgSecondary
 *
 * Warm
 *   background  FathomTheme.colors.warmWash
 *   content     FathomTheme.colors.warm
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * SELECTED — the same for every variant
 * ═══════════════════════════════════════════════════════════════════════════
 * When selected = true, the variant is ignored entirely:
 *   background  the teal gradient (currentLight 0% → current 52% → currentDeep 100%)
 *   content     FathomTheme.colors.currentOn
 *   glow        Modifier.shadow(10.dp, pill shape, current @ GlowAlpha.current)
 *
 * Same structure as FathomButton's disabled state — one condition collapses all
 * the variant branches.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * PRESS  (hover discarded)
 * ═══════════════════════════════════════════════════════════════════════════
 * - scale 0.95 via graphicsLayer, tween(FathomMotion.quick, FathomMotion.settle)
 *   Note: 0.95, not the button's 0.972. Chips are small, so the shrink is more
 *   pronounced to stay readable.
 * - Only when onClick is non-null.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * STATIC VS INTERACTIVE
 * ═══════════════════════════════════════════════════════════════════════════
 * - onClick null  → a static tag. No clickable, no interactionSource, no press scale.
 * - onClick set   → clickable with indication = null, plus the press scale.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * MOTION
 * ═══════════════════════════════════════════════════════════════════════════
 *   scale                  → tween(FathomMotion.quick, FathomMotion.settle)  140ms
 *   background, colour     → tween(FathomMotion.base,  FathomMotion.glide)   240ms
 *
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * @param text     the label.
 * @param modifier applied to the Row.
 * @param selected true → gradient fill, currentOn text, glow. Overrides variant.
 * @param variant  Current | Neutral | Warm. Ignored when selected.
 * @param count    optional trailing mono number. Null = not drawn.
 * @param icon     optional leading 16.dp icon slot.
 * @param onClick  null → static tag. Non-null → tappable with press scale.
 */
enum class FathomChipVariant { Current, Neutral, Warm }

@Preview
@Composable
fun FathomChip(
    modifier: Modifier = Modifier,
    text: String = "Fathom",
    selected: Boolean = true,
    variant: FathomChipVariant = FathomChipVariant.Current,
    count: Int? = null,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentLight = FathomTheme.colors.currentLight
    val current = FathomTheme.colors.current
    val currentDeep = FathomTheme.colors.currentDeep

    val colors = when (variant) {
        FathomChipVariant.Current -> Pair(
            FathomTheme.colors.currentWash,
            FathomTheme.colors.textAccent
        )

        FathomChipVariant.Neutral -> Pair(
            FathomTheme.colors.bgSunken,
            FathomTheme.colors.fgSecondary
        )

        FathomChipVariant.Warm -> Pair(FathomTheme.colors.warmWash, FathomTheme.colors.warm)
    }

    val contentColor = if (selected) FathomTheme.colors.currentOn else colors.second

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(FathomMotion.quick, easing = FathomMotion.settle)
    )

    Box(
        modifier = modifier
            .then(
                if (selected) {
                    Modifier.shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(FathomRadius.chip),
                        ambientColor = FathomTheme.colors.current.copy(alpha = FathomElevation.GlowAlpha.current),
                        spotColor = FathomTheme.colors.current.copy(alpha = FathomElevation.GlowAlpha.current)
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(FathomRadius.chip))
            .then(
                if (selected) {
                    Modifier.drawWithCache {
                        val brush = Brush.linearGradient(
                            Pair(0f, currentLight),
                            Pair(0.52f, current),
                            Pair(1f, currentDeep),
                            end = Offset(size.width, size.height)
                        )
                        onDrawBehind {
                            drawRoundRect(
                                brush = brush,
                                cornerRadius = CornerRadius(FathomRadius.chip.toPx())
                            )
                        }
                    }
                } else Modifier.background(colors.first)
            )
            .then(
                if (onClick != null) {
                    Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                } else Modifier
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        enabled = true,
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .height(38.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor
                )
            }
            Text(
                text = text,
                style = FathomTypography.bodySmall.copy(
                    fontWeight = FontWeight.W500,
                    lineHeight = 1.em,
                    color = contentColor
                ),
                maxLines = 1
            )
            if (count != null) {
                Text(
                    text = count.toString(),
                    style = FathomTypography.labelMedium.copy(color = contentColor.copy(alpha = 0.72f))
                )
            }
        }
    }
}