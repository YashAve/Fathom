package com.development.fathom.core.designsystem.component.core

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.development.fathom.core.designsystem.theme.FathomElevation
import com.development.fathom.core.designsystem.theme.FathomMotion
import com.development.fathom.core.designsystem.theme.FathomRadius
import com.development.fathom.core.designsystem.theme.FathomTheme
import com.development.fathom.core.designsystem.theme.FathomTypography

/**
 * The fully-rounded 56dp field used by every auth screen and the search bar.
 *
 * @param value         current text. Stateless — the caller owns it.
 * @param onValueChange called with the next text.
 * @param modifier      applied to the Column.
 * @param label         optional mono eyebrow. Uppercased inside this composable.
 * @param placeholder   shown when value is empty.
 * @param icon          optional leading 20.dp glyph.
 * @param trailing      optional trailing slot. Ignored when isPassword is true.
 * @param hint          helper text below. Replaced by error when error is non-null.
 * @param error         non-null → coral ring, coral hint, and this string as the message.
 * @param enabled       false → disabled treatment, input ignored.
 * @param isPassword    true → masks input and adds the reveal eye.
 * @param strength      0f..1f → draws the strength bar. Null → no bar.
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

    val shape = RoundedCornerShape(FathomRadius.field)

    // ── Strength ─────────────────────────────────────────────────────────────
    // Called unconditionally. A composable inside a null-check would lose its
    // animation slot whenever `strength` flipped between null and non-null.
    val animatedStrength by animateFloatAsState(
        targetValue = (strength ?: 0f).coerceIn(0f, 1f),
        animationSpec = tween(FathomMotion.slow, easing = FathomMotion.glide),
        label = "strength"
    )

    // ── Border ───────────────────────────────────────────────────────────────
    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> Color.Transparent
            error != null -> FathomTheme.colors.warm
            isFocused -> FathomTheme.colors.current
            else -> FathomTheme.colors.borderDefault
        },
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide),
        label = "borderColor"
    )

    val borderWidth by animateDpAsState(
        targetValue = when {
            !enabled -> 0.dp
            isFocused || error != null -> 1.5.dp
            else -> 1.dp
        },
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide),
        label = "borderWidth"
    )

    // ── Halo ─────────────────────────────────────────────────────────────────
    val haloColor by animateColorAsState(
        targetValue = when {
            !enabled -> Color.Transparent
            error != null -> FathomTheme.colors.warm.copy(alpha = 0.16f)
            isFocused -> FathomTheme.colors.focusRing
            else -> Color.Transparent
        },
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide),
        label = "halo"
    )

    // ── Shadow ───────────────────────────────────────────────────────────────
    val shadowElevation by animateDpAsState(
        targetValue = when {
            !enabled -> 0.dp
            isFocused -> FathomElevation.float
            else -> FathomElevation.card
        },
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide),
        label = "shadow"
    )

    // ── Surfaces and content ─────────────────────────────────────────────────
    val backgroundColor by animateColorAsState(
        targetValue = if (enabled) FathomTheme.colors.bgRaised else FathomTheme.colors.bgSunken,
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide),
        label = "background"
    )

    val iconTint by animateColorAsState(
        targetValue = when {
            !enabled -> FathomTheme.colors.fgDisabled
            error != null -> FathomTheme.colors.warm
            isFocused -> FathomTheme.colors.textAccent
            else -> FathomTheme.colors.fgTertiary
        },
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide),
        label = "iconTint"
    )

    val labelColor by animateColorAsState(
        targetValue = when {
            !enabled -> FathomTheme.colors.fgDisabled
            isFocused -> FathomTheme.colors.textAccent
            else -> FathomTheme.colors.fgTertiary
        },
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide),
        label = "labelColor"
    )

    val textColor = if (enabled) FathomTheme.colors.fgPrimary else FathomTheme.colors.fgDisabled

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        if (label != null) {
            Text(
                text = label.uppercase(),
                style = FathomTypography.labelSmall.copy(color = labelColor),
                modifier = Modifier.padding(start = 20.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                // The halo sits OUTSIDE the pill. inset(-4.dp) expands the drawing
                // area past the bounds; the opaque background below covers the middle,
                // leaving only the 4.dp rim visible. Must come BEFORE clip.
                .drawBehind {
                    inset(-4.dp.toPx()) {
                        drawRoundRect(
                            color = haloColor,
                            cornerRadius = CornerRadius(size.height / 2f)
                        )
                    }
                }
                .shadow(elevation = shadowElevation, shape = shape)
                .clip(shape)
                .background(backgroundColor)
                .border(borderWidth, borderColor, shape)
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = iconTint
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    textStyle = FathomTypography.bodyLarge.copy(
                        lineHeight = 1.4.em,
                        color = textColor
                    ),
                    singleLine = true,
                    interactionSource = interactionSource,
                    cursorBrush = SolidColor(FathomTheme.colors.current),
                    visualTransformation =
                        if (isPassword && !revealed) PasswordVisualTransformation()
                        else VisualTransformation.None,
                    modifier = Modifier.weight(1f)
                ) { innerTextField ->
                    // This trailing lambda is the decorationBox. innerTextField() IS
                    // the editable text — omit the call and the field renders nothing.
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = FathomTypography.bodyLarge.copy(
                                    lineHeight = 1.4.em,
                                    color = FathomTheme.colors.fgTertiary
                                )
                            )
                        }
                        innerTextField()
                    }
                }

                when {
                    isPassword -> Icon(
                        imageVector = if (revealed) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (revealed) "Hide password" else "Show password",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(enabled = enabled) { revealed = !revealed },
                        tint = iconTint
                    )

                    trailing != null -> Icon(
                        imageVector = trailing,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = iconTint
                    )
                }
            }
        }

        if (strength != null) {
            Box(
                modifier = Modifier
                    // Padding first. After background it would inset the gradient
                    // while leaving the grey track at full width.
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(FathomRadius.button))
                    .background(FathomTheme.colors.bgSunken)
                    .drawWithCache {
                        val brush = Brush.linearGradient(
                            0f to currentLight,
                            0.52f to current,
                            1f to currentDeep,
                            start = Offset.Zero,
                            end = Offset(size.width, size.height)
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRoundRect(
                                brush = brush,
                                size = Size(size.width * animatedStrength, size.height),
                                cornerRadius = CornerRadius(size.height / 2f)
                            )
                        }
                    }
            )
        }

        if (hint != null || error != null) {
            Text(
                text = error ?: hint!!,
                style = FathomTypography.bodySmall.copy(
                    color = if (error != null) FathomTheme.colors.warm
                    else FathomTheme.colors.fgTertiary
                ),
                modifier = Modifier.padding(start = 20.dp)
            )
        }
    }
}