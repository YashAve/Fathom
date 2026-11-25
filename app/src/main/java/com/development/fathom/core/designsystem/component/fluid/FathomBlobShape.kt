package com.development.fathom.core.designsystem.component.fluid

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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.development.fathom.core.designsystem.theme.FathomTheme
import kotlinx.coroutines.isActive

@Composable
fun FathomBlobShape(
    modifier: Modifier = Modifier,
    size: Dp = 240.dp
) {
    var time by remember { mutableFloatStateOf(0f) }
    val current = FathomTheme.colors.current
    val bgRaised = FathomTheme.colors.bgRaised
    val currentLight = FathomTheme.colors.currentLight
    val currentDeep = FathomTheme.colors.currentDeep
    var via: Color? = null
    val gradientAngle = 135f

    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { now ->
                time = (now - start) / 1_000_000_000f
            }
        }
    }

    Spacer(
        modifier = modifier
            .size(size)
            /*Outer block: call val (start, end) =
             gradientLine(cx, cy, radius, gradientAngle).
              Build Brush.linearGradient with stops 0f to from,
               1f to to, or 0f to from, 0.52f to via, 1f to to when via isn't null,
                from start to end. It's built once and reused every frame,
                 like the scrubber's gradient.*/
            .drawWithCache {
                val path = Path()
                val xs = FloatArray(8)
                val ys = FloatArray(8)
                val cx = this.size.width / 2f
                val cy = this.size.height / 2f
                val radius = this.size.minDimension * 0.42f
                val (start, end) = gradientLine(cx, cy, radius, gradientAngle)
                val brush = if (via == null) {
                    Brush.linearGradient(
                        0f to currentLight,
                        1f to currentDeep,
                        start = start,
                        end = end
                    )
                } else {
                    Brush.linearGradient(
                        0f to currentLight,
                        0.52f to via,
                        1f to currentDeep,
                        start = start,
                        end = end
                    )
                }
                onDrawBehind {
                    val travel = 22.dp.toPx()
                    blobPoints(
                        xs,
                        ys,
                        cx,
                        cy,
                        radius,
                        time = time,
                        travel = travel,
                        jitter = 3f
                    )
                    blobPath(path, xs, ys)
                    drawPath(path, brush = brush)
                }
            }
    )
}

@Preview
@Composable
private fun FathomBlobShapePreview(
    modifier: Modifier = Modifier
) {
    FathomTheme {
        FathomBlobShape(modifier = modifier)
    }
}