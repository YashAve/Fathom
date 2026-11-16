package com.development.fathom.core.designsystem.component.fluid

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal fun blobPoints(xs: FloatArray, ys: FloatArray, cx: Float, cy: Float, radius: Float) {
    val count = xs.size
    for (i in 0 until count) {
        val a = i * (2f * PI.toFloat() / count) - PI.toFloat() / 2f
        xs[i] = cx + cos(a) * radius
        ys[i] = cy + sin(a) * radius
    }
}