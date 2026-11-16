package com.development.fathom.core.designsystem.component.fluid

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.development.fathom.core.designsystem.theme.FathomTheme

/*Your part, the Compose side. Create FathomBlobShape(modifier, size: Dp = 240.dp):

The element: a Spacer with Modifier.size(size) and drawWithCache.
Outer block: create xs and ys as FloatArray(8) once. Work out radius = size.minDimension * 0.42f, the design’s default.
onDrawBehind: call blobPoints(xs, ys, size.width / 2f, size.height / 2f, radius),
then draw 8 small dots with drawCircle(current, radius = 4.dp.toPx(), center = Offset(xs[i], ys[i])).*/
@Preview
@Composable
fun FathomBlobShape(
    modifier: Modifier = Modifier,
    size: Dp = 240.dp
) {
    val current = FathomTheme.colors.current
    Spacer(
        modifier = modifier
            .size(size)
            .drawWithCache {
                val xs = FloatArray(8)
                val xy = FloatArray(8)
                val radius = this.size.minDimension * 0.42f
                onDrawBehind {
                    blobPoints(xs, xy, this.size.width / 2f, this.size.height / 2f, radius)
                    for ((i, element) in xs.withIndex()) {
                        drawCircle(
                            current,
                            radius = 4.dp.toPx(),
                            center = Offset(element, xy[i])
                        )
                    }
                }
            }
    )
}