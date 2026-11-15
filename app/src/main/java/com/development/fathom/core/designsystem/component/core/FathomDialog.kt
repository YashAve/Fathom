package com.development.fathom.core.designsystem.component.core

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.development.fathom.core.designsystem.component.fluid.lerp
import com.development.fathom.core.designsystem.theme.FathomElevation
import com.development.fathom.core.designsystem.theme.FathomMotion
import com.development.fathom.core.designsystem.theme.FathomTheme
import com.development.fathom.core.designsystem.theme.FathomTypography

@Composable
fun FathomDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    body: String? = null,
    onDismiss: () -> Unit = { },
    actions: @Composable RowScope.() -> Unit = { },
    content: @Composable ColumnScope.() -> Unit = { }
) {

    val themeLight = FathomTheme.colors.isLight
    val current = FathomTheme.colors.current
    val backgroundElevated = FathomTheme.colors.bgElev
    val shadowTint = FathomTheme.colors.shadowTint
    val appear = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        appear.animateTo(
            targetValue = 1f,
            animationSpec = tween(FathomMotion.sheet, easing = FathomMotion.settle)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        val window = (LocalView.current.parent as? DialogWindowProvider)?.window

        SideEffect {
            window?.setDimAmount(if (themeLight) 0.28f else 0.6f)
        }

        Column(
            modifier = modifier
                .graphicsLayer {
                    val t = appear.value
                    alpha = t.coerceIn(0f, 1f)
                    translationY = (1f - t) * 18.dp.toPx()
                    scaleX = lerp(0.96f, 1f, t)
                    scaleY = scaleX
                }
                .padding(24.dp)
                .widthIn(max = 332.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = FathomElevation.sheet,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = shadowTint.copy(if (themeLight) FathomElevation.Light.lg else FathomElevation.Dark.lg),
                    spotColor = shadowTint.copy(if (themeLight) FathomElevation.Light.lg else FathomElevation.Dark.lg)
                )
                .background(backgroundElevated)
                .drawWithCache {
                    val gradient = Brush.radialGradient(
                        Pair(0f, current.copy(0.22f)),
                        Pair(1f, current.copy(0f)),
                        center = Offset(50.dp.toPx(), 30.dp.toPx()),
                        radius = 100.dp.toPx()
                    )
                    onDrawBehind {
                        drawCircle(
                            gradient,
                            center = Offset(50.dp.toPx(), 30.dp.toPx()),
                            radius = 100.dp.toPx()
                        )
                    }
                }
                .padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = FathomTypography.titleLarge.copy(color = FathomTheme.colors.fgPrimary)
                )
            }
            if (body != null) {
                Text(
                    text = body,
                    style = FathomTypography.bodyMedium.copy(color = FathomTheme.colors.fgSecondary)
                )
            }
            content()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                actions()
            }
        }
    }
}