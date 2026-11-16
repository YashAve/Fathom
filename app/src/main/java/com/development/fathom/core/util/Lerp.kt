package com.development.fathom.core.util

/**
 * The point between start and stop: fraction 0f -> start, 1f -> stop, 0.5f -> exactly halfway.
 * Same formula as the Offset lerp you used before (A + (B - A) * t), just for one Float.
 * Written here because Compose's Float lerp lives in the ui-util library, which this module may not include.
 */
fun lerp(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction