package com.development.fathom.core.designsystem.component.fluid

import kotlin.math.min
import kotlin.math.sin

internal fun makeWaveForm(seed: Int, count: Int = 88): FloatArray {
    val output = FloatArray(count)
    val s = seed * 1.7f
    for (i in 0 until count) {
        val x = i / (count - 1f)
        val v = 0.34f +
                    0.20f * sin(x * 13.7f + s) +
                    0.13f * sin(x * 31.2f + s * 2.3f) +
                    0.08f * sin(x * 61.5f + s * 4.1f) +
                    0.15f * sin(x * Math.PI.toFloat() * 1.1f + s * 0.6f)
        val fadeIn = min(1f, x * 6f)
        val fadeOut = min(1f, (1f - x) * 5f)
        output[i] = v.coerceIn(0.10f, 1f) * min(fadeIn, fadeOut)
    }
    return output
}