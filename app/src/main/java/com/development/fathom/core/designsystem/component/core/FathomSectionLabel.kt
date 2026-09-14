package com.development.fathom.core.designsystem.component.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development.fathom.core.designsystem.theme.FathomTheme
import com.development.fathom.core.designsystem.theme.FathomTypography

/**
 * DESIGN SPEC — FathomSectionLabel (components/core/SectionLabel.jsx → .fa-section-label).
 *
 * The quiet mono eyebrow that opens every section: "RECENTLY PLAYED", "ALL TRACKS",
 * "STEP ONE". Never shouts — it's the smallest, quietest text in the system.
 *
 * You've already seen this on the Library screen: the faint "recently played" and
 * "all tracks  9" lines above each list.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * LAYOUT — a Row, vertically centred, 12.dp gap between children
 * ═══════════════════════════════════════════════════════════════════════════
 *   [ LABEL ]   [ count ]   [ ──── hairline ──── ]   [ action ]
 *
 * - Gap: FathomSpacing space3 = 12.dp between every child.
 * - The label and count never shrink (flex: none).
 * - The hairline is the only element that stretches — Modifier.weight(1f).
 * - When there is no hairline but there IS an action, the action pushes to the
 *   far right instead (Spacer with weight(1f) before it).
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE LABEL
 * ═══════════════════════════════════════════════════════════════════════════
 * - Style: FathomTypography.labelSmall   // JetBrains Mono 11.sp, W500, 0.16.em
 * - TEXT IS UPPERCASED at the call site: text.uppercase()
 *   The CSS does this with text-transform; Compose has no equivalent in TextStyle,
 *   so you do it in Kotlin.
 * - Colour: FathomTheme.colors.fgTertiary
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE COUNT  (optional)
 * ═══════════════════════════════════════════════════════════════════════════
 * - Style: FathomTypography.labelMedium   // JetBrains Mono 12.sp, W500, 0.06.em
 * - NOT uppercased — it's a number.
 * - Colour: FathomTheme.colors.fgTertiary at 80% alpha
 *   .copy(alpha = 0.8f)
 * - Only drawn when count is non-null.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE RULE  (optional hairline)
 * ═══════════════════════════════════════════════════════════════════════════
 * - A 1.dp tall Box filling all remaining horizontal space.
 * - Colour: FathomTheme.colors.borderSubtle
 * - Modifier.weight(1f).height(1.dp).background(borderSubtle)
 * - Only drawn when rule = true.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * ACCENT  (optional)
 * ═══════════════════════════════════════════════════════════════════════════
 * - When accent = true, the LABEL colour becomes FathomTheme.colors.textAccent
 *   instead of fgTertiary. Used for onboarding eyebrows ("STEP ONE").
 * - The count colour does NOT change — it stays fgTertiary @ 80%.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * NO STATE, NO INTERACTION, NO ANIMATION
 * ═══════════════════════════════════════════════════════════════════════════
 * This component has no pressed state, no clickable, no graphicsLayer. It is
 * a Row of Text. That's all.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * @param text     the label. Uppercased inside this composable, not by the caller.
 * @param modifier applied to the Row.
 * @param count    optional trailing number, e.g. track count. Null = not drawn.
 * @param rule     true → hairline fills the remaining width.
 * @param accent   true → label tinted with textAccent instead of fgTertiary.
 * @param action   optional right-aligned slot, usually a Tertiary FathomButton.
 */

@Preview
@Composable
fun FathomSectionLabel(
    modifier: Modifier = Modifier,
    text: String = "Fathom",
    count: Int? = 0,
    rule: Boolean = true,
    accent: Boolean = true,
    action: (@Composable () -> Unit)? = null
) {
    val labelColor = if (accent) FathomTheme.colors.textAccent else FathomTheme.colors.fgTertiary

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text.uppercase(),
            style = FathomTypography.labelSmall.copy(color = labelColor)
        )
        if (count != null) {
            Text(
                text = count.toString(),
                style = FathomTypography.labelMedium.copy(
                    color = FathomTheme.colors.fgTertiary.copy(
                        alpha = 0.8f
                    )
                )
            )
        }
        if (rule) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(FathomTheme.colors.borderSubtle)
            )
        }
        if (action != null) {
            action()
        }
    }
}