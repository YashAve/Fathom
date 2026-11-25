package com.development.fathom.core.designsystem.component.fluid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Each point's distance from the centre, as a share of the radius (93% to 105%). Index i is point i. */
internal val BLOB_RADII = floatArrayOf(1.0f, 0.96f, 1.03f, 0.94f, 1.0f, 0.97f, 1.05f, 0.93f)
internal val BLOB_FREQS = floatArrayOf(1.0f, 0.82f, 1.17f, 0.91f, 1.06f, 0.76f, 1.24f, 0.97f)
internal val BLOB_PHASES = floatArrayOf(0f, 0.62f, 1.24f, 1.86f, 2.48f, 3.1f, 3.72f, 4.34f)

internal fun blobPoints(
    xs: FloatArray, ys: FloatArray,
    cx: Float, cy: Float, radius: Float,
    time: Float = 0f,
    travel: Float = 0f,
    jitter: Float = 0f,      // degrees each point's angle sways either way
    period: Float = 14f,
    radii: FloatArray = BLOB_RADII,
    freqs: FloatArray = BLOB_FREQS,
    phases: FloatArray = BLOB_PHASES
) {
    val count = xs.size
    val w = 2f * PI.toFloat() / period
    for (i in 0 until count) {
        val f = freqs[i % freqs.size]
        val ph = phases[i % phases.size]
        val wobble = sin(time * w * f * 0.5f + ph * 1.7f) * jitter * PI.toFloat() / 180f   // radians
        val a = i * (2f * PI.toFloat() / count) - PI.toFloat() / 2f + wobble
        val breathe = sin(time * w * f + ph) * travel
        val r = radius * radii[i % radii.size] + breathe
        xs[i] = cx + cos(a) * r
        ys[i] = cy + sin(a) * r
    }
}

/** Smooth closed outline through every point (Catmull-Rom as cubic curves). Rewinds and refills `path`. */
internal fun blobPath(path: Path, xs: FloatArray, ys: FloatArray, tension: Float = 1f / 6f) {
    val n = xs.size
    path.rewind()
    path.moveTo(xs[0], ys[0])
    for (i in 0 until n) {
        val p0 = (i - 1 + n) % n   // the point before
        val p1 = i                 // this part starts here
        val p2 = (i + 1) % n       // ...and ends here
        val p3 = (i + 2) % n       // the point after
        path.cubicTo(
            xs[p1] + (xs[p2] - xs[p0]) * tension, ys[p1] + (ys[p2] - ys[p0]) * tension,  // c1
            xs[p2] - (xs[p3] - xs[p1]) * tension, ys[p2] - (ys[p3] - ys[p1]) * tension,  // c2
            xs[p2], ys[p2]                                                             // end point
        )
    }
    path.close()
}

/** Start and end of a gradient line through (cx, cy). 0° points to 3 o'clock, 90° straight down (like blobPoints). */
internal fun gradientLine(cx: Float, cy: Float, halfLength: Float, angleDegrees: Float): Pair<Offset, Offset> {
    val rad = angleDegrees * PI.toFloat() / 180f
    val dx = cos(rad) * halfLength
    val dy = sin(rad) * halfLength
    return Offset(cx - dx, cy - dy) to Offset(cx + dx, cy + dy)
}