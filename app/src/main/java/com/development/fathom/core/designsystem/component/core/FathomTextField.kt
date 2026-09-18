package com.development.fathom.core.designsystem.component.core

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.development.fathom.core.designsystem.theme.FathomMotion
import com.development.fathom.core.designsystem.theme.FathomRadius
import com.development.fathom.core.designsystem.theme.FathomTheme
import com.development.fathom.core.designsystem.theme.FathomTypography

/**
 * The fully-rounded 56dp field used by every auth screen and the search bar.
 *
 * @param value        current text. Stateless — the caller owns it.
 * @param onValueChange called with the next text.
 * @param modifier     applied to the Column.
 * @param label        optional mono eyebrow. Uppercased inside this composable.
 * @param placeholder  shown when value is empty.
 * @param icon         optional leading 20.dp glyph.
 * @param trailing     optional trailing slot. Ignored when isPassword is true.
 * @param hint         helper text below. Replaced by error when error is non-null.
 * @param error        non-null → coral ring, coral hint, and this string as the message.
 * @param enabled      false → disabled treatment, clicks and input ignored.
 * @param isPassword   true → masks input and adds the reveal eye.
 * @param strength     0f..1f → draws the strength bar. Null → no bar.
 */
@Preview
@Composable
fun FathomTextField(
    modifier: Modifier = Modifier,
    value: String = "Fathom",
    label: String? = "Fathom",
    placeholder: String = "Fathom",
    icon: ImageVector? = null,
    trailing: ImageVector? = null,
    hint: String? = "Fathom",
    error: String? = null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    strength: Float? = 0.53f,
    onValueChange: (String) -> Unit = { }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    var revealed by rememberSaveable { mutableStateOf(false) }

    val currentLight = FathomTheme.colors.currentLight
    val current = FathomTheme.colors.current
    val currentDeep = FathomTheme.colors.currentDeep

    val animatedStrength = strength?.coerceIn(0f, 1f)?.let {
        animateFloatAsState(
            targetValue = it,
            animationSpec = tween(FathomMotion.slow, easing = FathomMotion.glide)
        )
    }

    val animateBorder by animateColorAsState(
        targetValue = when {
            (!isFocused && value.isEmpty() && error != null && enabled) || (value.isNotEmpty() && !isFocused) ->
                FathomTheme.colors.borderDefault

            error != null -> FathomTheme.colors.warm
            isFocused -> FathomTheme.colors.current
            else -> Color.Transparent
        },
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide)
    )

    val animateAccentColor by animateColorAsState(
        targetValue = if (isFocused) FathomTheme.colors.textAccent else FathomTheme.colors.fgTertiary,
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide)
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        if (label != null) {
            Text(
                text = label.uppercase(),
                style = FathomTypography.labelSmall.copy(color = animateAccentColor),
                modifier = Modifier.padding(start = 20.dp)
            )
        }

        /* TODO — HALO
         * Wrap the Box below in an outer Box that ALWAYS reserves 4.dp padding.
         * Put the halo as a Modifier.border on that outer Box:
         *   focused     → FathomTheme.colors.focusRing
         *   error       → FathomTheme.colors.warm.copy(alpha = 0.16f)
         *   otherwise   → Color.Transparent
         * The 4.dp never changes, so the layout never jumps. Only the colour animates,
         * with tween(FathomMotion.base, FathomMotion.glide).
         */

        /* TODO — SHADOW
         * rest / filled → Modifier.shadow(FathomElevation.card, pill shape)
         * focused       → Modifier.shadow(FathomElevation.float, pill shape)
         * disabled      → no shadow
         * Must sit BEFORE clip in the modifier chain.
         */

        /* TODO — DISABLED
         * background → FathomTheme.colors.bgSunken (instead of bgRaised)
         * no border, no shadow
         * input and icon tint → FathomTheme.colors.fgDisabled
         * BasicTextField needs enabled = false so it can't take focus.
         */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(FathomRadius.field))
                .background(FathomTheme.colors.bgRaised)
                .then(
                    when {
                        (!isFocused && value.isEmpty() && error != null && enabled) || (value.isNotEmpty() && !isFocused) -> Modifier.border(
                            1.dp,
                            animateBorder,
                            RoundedCornerShape(FathomRadius.button)
                        )

                        error != null -> Modifier.border(
                            (1.5).dp, animateBorder,
                            RoundedCornerShape(FathomRadius.button)
                        )

                        isFocused -> Modifier.border(
                            (1.5).dp, animateBorder,
                            RoundedCornerShape(FathomRadius.button)
                        )

                        else -> Modifier
                    }
                )
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        null,
                        modifier = Modifier.size(20.dp),
                        tint = animateAccentColor
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = { onValueChange(it) },
                    textStyle = FathomTypography.bodyLarge.copy(
                        lineHeight = (1.4).em,
                        color = FathomTheme.colors.fgPrimary
                    ),
                    singleLine = true,
                    interactionSource = interactionSource,
                    visualTransformation = if (revealed) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.weight(1f)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = FathomTypography.bodyLarge.copy(
                                lineHeight = (1.4).em,
                                color = FathomTheme.colors.fgTertiary
                            )
                        )
                    }
                }
                if (isPassword) {
                    Icon(
                        imageVector = if (revealed) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        null,
                        modifier = Modifier.size(20.dp),
                        tint = animateAccentColor
                    )
                }
                if (trailing != null) {
                    Icon(
                        imageVector = trailing,
                        null,
                        modifier = Modifier.size(20.dp),
                        tint = animateAccentColor
                    )
                }
            }
        }

        if (strength != null && animatedStrength != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(FathomRadius.button))
                    .background(FathomTheme.colors.bgSunken)
                    .padding(horizontal = 20.dp)
                    .drawWithCache {
                        val brush = Brush.linearGradient(
                            Pair(0f, currentLight),
                            Pair(0.52f, current),
                            Pair(1f, currentDeep),
                            end = Offset(size.width, size.height)
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRoundRect(
                                brush,
                                size = Size(size.width * animatedStrength.value, size.height),
                                cornerRadius = CornerRadius(FathomRadius.button.toPx())
                            )
                        }
                    }
            )
        }

        if (hint != null || error != null) {
            Text(
                text = error ?: hint!!,
                style = FathomTypography.bodySmall.copy(if (hint != null) FathomTheme.colors.fgTertiary else FathomTheme.colors.warm),
                modifier = Modifier.padding(start = 20.dp)
            )
        }
    }
}