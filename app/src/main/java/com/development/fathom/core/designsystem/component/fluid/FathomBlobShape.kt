package com.development.fathom.core.designsystem.component.fluid

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.development.fathom.core.designsystem.theme.FathomTheme
import kotlinx.coroutines.isActive
import kotlin.math.sqrt

@Composable
fun FathomBlobShape(
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    baseRadius: Dp? = null,
    travel: Dp = 22.dp,
    period: Float = 14f,
    jitter: Float = 3f,
    seed: Float = 0f,
    from: Color = FathomTheme.colors.currentLight,
    to: Color = FathomTheme.colors.currentDeep,
    via: Color? = null,
    gradientAngle: Float = 135f,
    opacity: Float = 1f,
    glow: Boolean = false,
    glowColor: Color? = null,
    glowBlur: Dp = 22.dp,
    glowOpacity: Float = 0.55f,
    glowOffset: Dp = 14.dp,
    paused: Boolean = false
) {
    var time by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(paused) {
        val scale = coroutineContext[MotionDurationScale]?.scaleFactor ?: 1f
        if (paused || scale == 0f) return@LaunchedEffect
        val resumeAt = time
        val start = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { now ->
                time = resumeAt + (now - start) / 1_000_000_000f
            }
        }
    }

    Box(modifier = modifier.size(size)) {
        if (glow && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            BlobLayer(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { translationY = glowOffset.toPx() }
                    .blur(glowBlur * sqrt(3f), BlurredEdgeTreatment.Unbounded),
                time = { time + seed },
                baseRadius = baseRadius,
                travel = travel,
                period = period,
                jitter = jitter,
                from = from,
                to = to,
                via = via,
                gradientAngle = gradientAngle,
                solidColor = glowColor,
                alpha = glowOpacity
            )
        }
        BlobLayer(
            modifier = Modifier.matchParentSize(),
            time = { time + seed },
            baseRadius = baseRadius,
            travel = travel,
            period = period,
            jitter = jitter,
            from = from,
            to = to,
            via = via,
            gradientAngle = gradientAngle,
            solidColor = null,
            alpha = opacity
        )
    }
}

@Composable
private fun BlobLayer(
    modifier: Modifier,
    time: () -> Float,
    baseRadius: Dp?,
    travel: Dp,
    period: Float,
    jitter: Float,
    from: Color,
    to: Color,
    via: Color?,
    gradientAngle: Float,
    solidColor: Color?,
    alpha: Float
) {
    Spacer(
        modifier = modifier.drawWithCache {
            val path = Path()
            val xs = FloatArray(8)
            val ys = FloatArray(8)
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = baseRadius?.toPx() ?: (size.minDimension * 0.42f)
            val travelPx = travel.toPx()
            val (start, end) = gradientLine(cx, cy, radius, gradientAngle)
            val brush = when {
                solidColor != null -> SolidColor(solidColor)
                via == null -> Brush.linearGradient(
                    0f to from,
                    1f to to,
                    start = start,
                    end = end
                )
                else -> Brush.linearGradient(
                    0f to from,
                    0.52f to via,
                    1f to to,
                    start = start,
                    end = end
                )
            }
            onDrawBehind {
                blobPoints(
                    xs,
                    ys,
                    cx,
                    cy,
                    radius,
                    time = time(),
                    travel = travelPx,
                    jitter = jitter,
                    period = period
                )
                blobPath(path, xs, ys)
                drawPath(path, brush = brush, alpha = alpha)
            }
        }
    )
}

@Preview
@Composable
private fun FathomBlobShapePreview() {
    FathomTheme {
        FathomBlobShape()
    }
}

@Preview
@Composable
private fun FathomBlobShapeGlowPreview() {
    FathomTheme {
        FathomBlobShape(glow = true)
    }
}