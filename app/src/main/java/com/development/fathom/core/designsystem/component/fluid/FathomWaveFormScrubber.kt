package com.development.fathom.core.designsystem.component.fluid

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development.fathom.core.designsystem.theme.FathomMotion
import com.development.fathom.core.designsystem.theme.FathomTheme
import com.development.fathom.core.designsystem.theme.FathomTypography
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
private fun ScrubberTimes(
    modifier: Modifier = Modifier,
    progress: () -> Float = { 0.35f },
    durationMs: Long = 240_000L
) {
    val labels by remember(progress, durationMs) {
        derivedStateOf {
            timeLabels(progress(), durationMs)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (label in labels.toList()) {
            Text(
                text = label,
                style = FathomTypography.labelMedium.copy(color = FathomTheme.colors.fgTertiary)
            )
        }
    }
}

@Composable
fun FathomWaveFormScrubber(
    modifier: Modifier = Modifier,
    seed: Int = 3,
    durationMs: Long = 214_000L,
    showTimes: Boolean = true,
    progress: () -> Float = { 0.35f },
    onSeek: ((Float) -> Unit)? = null
) {

    val current = FathomTheme.colors.current
    val currentLight = FathomTheme.colors.currentLight
    val currentDeep = FathomTheme.colors.currentDeep
    val bgRaised = FathomTheme.colors.bgRaised
    val outline = FathomTheme.colors.fgTertiary.copy(alpha = 0.55f)
    val wave = remember(seed) { makeWaveForm(seed = seed) }
    var dragging by remember { mutableStateOf(false) }
    val latestOnSeek by rememberUpdatedState(onSeek)
    val transition = rememberInfiniteTransition(label = "wave")

    // MATH: how far the bumps have slid, in radians. Grows 0 -> FULL_LAP (6.28) every 9 s, then restarts.
    // FULL_LAP is one whole lap of sin, so the restart looks exactly like the start: no jump.
    // Only read inside onDrawBehind, so every frame re-runs the draw phase and nothing else.
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = FULL_LAP,
        animationSpec = infiniteRepeatable(
            animation = tween(
                FathomMotion.wave,
                easing = LinearEasing
            ), repeatMode = RepeatMode.Restart
        ),
        label = "transition"
    )

    val thumbRadius by animateDpAsState(
        targetValue = if (dragging) 10.dp else 7.dp,
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.settle)
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (dragging) 0.22f else 0.14f,
        animationSpec = tween(FathomMotion.base, easing = FathomMotion.glide)
    )

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .drawWithCache {
                    // OUTER BLOCK: runs again only when the size changes.
                    // Everything is created here once, and reused by every frame in onDrawBehind.
                    val cy = size.height / 2f
                    val maxHeight = size.height / 2f - 9.dp.toPx()
                    // MATH: the thinnest the ribbon can be (half-thickness), so the two ends never pinch to a point.
                    val minHeight = 1.5.dp.toPx()
                    val undulation = 4.dp.toPx()
                    val last = wave.lastIndex // 87

                    // x and h never depend on phase, so they are worked out once here instead of every frame.
                    // MATH: i / last goes 0 -> 1 from the first sample to the last, then * width.
                    // i.toFloat() first: Int / Int throws away the fraction (40 / 87 = 0).
                    val xs = FloatArray(wave.size) { i -> i.toFloat() / last * size.width }
                    // MATH: wave[i] = 0 -> minHeight. wave[i] = 1 -> maxHeight.
                    val hs =
                        FloatArray(wave.size) { i -> minHeight + wave[i] * (maxHeight - minHeight) }

                    // These depend on phase, so they are refilled every frame.
                    // FloatArray stores plain numbers inside the array, so writing topY[i] = ... creates no objects.
                    val topY = FloatArray(wave.size)
                    val bottomY = FloatArray(wave.size)

                    val path = Path()
                    val outlineStroke = Stroke(width = 1.4.dp.toPx())
                    val thumbStroke = Stroke(width = 2.5.dp.toPx())

                    val gradient = Brush.linearGradient(
                        Pair(0f, currentLight),
                        Pair(0.6f, current),
                        Pair(1f, currentDeep),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height * 0.4f)
                    )

                    val glow = Brush.radialGradient(
                        Pair(0f, current),
                        Pair(1f, Color.Transparent),
                        center = Offset.Zero,
                        radius = 24.dp.toPx()
                    )

                    onDrawBehind {
                        // EVERY FRAME: only refill what already exists. Nothing new is created in here.

                        // MATH: read progress once, clamp once. px and idx below both use this same p.
                        val p = progress().coerceIn(0f, 1f)
                        val px = p * size.width

                        // MATH: only the centre line moves with phase.
                        // sin(...) gives -1..1, * undulation gives -4dp..+4dp around cy.
                        // "+ phase" inside the brackets slides the bumps left as phase grows.
                        // A plain for loop: wave.indices.forEach { } would build a range object every frame.
                        for (i in wave.indices) {
                            val center = cy + sin(i * 0.29f + phase) * undulation
                            topY[i] = center - hs[i]
                            bottomY[i] = center + hs[i]
                        }

                        path.rewind()
                        with(path) {
                            // Top edge, left to right.
                            // MATH: sample i is the control point; halfway to the next sample is where the curve lands.
                            moveTo(xs[0], topY[0])
                            for (i in 1 until last) {
                                quadraticTo(
                                    xs[i], topY[i],
                                    lerp(xs[i], xs[i + 1], 0.5f), lerp(topY[i], topY[i + 1], 0.5f)
                                )
                            }
                            lineTo(xs[last], topY[last])

                            // Bottom edge, right to left: i = 86 down to 1, landing halfway to the previous sample.
                            lineTo(xs[last], bottomY[last])
                            for (i in last - 1 downTo 1) {
                                quadraticTo(
                                    xs[i],
                                    bottomY[i],
                                    lerp(xs[i], xs[i - 1], 0.5f),
                                    lerp(bottomY[i], bottomY[i - 1], 0.5f)
                                )
                            }
                            lineTo(xs[0], bottomY[0])
                            close()
                        }

                        clipRect(left = px, right = size.width) {
                            drawPath(path = path, color = outline, style = outlineStroke)
                        }
                        clipRect(left = 0f, right = px) {
                            drawPath(path = path, brush = gradient)
                        }
                        if (onSeek != null) {
                            // MATH: the sample under the thumb. x = i / last * width, turned around:
                            // i = p * last. Rounded to the nearest whole sample (0..87).
                            val idx = (p * last).roundToInt()
                            // MATH: exactly the same formula as center above (phase included),
                            // so the thumb sits on the ribbon's centre line while it moves.
                            val ty = cy + sin(idx * 0.29f + phase) * undulation
                            translate(left = px, top = ty) {
                                drawCircle(
                                    brush = glow,
                                    radius = 24.dp.toPx(),
                                    center = Offset.Zero,
                                    alpha = glowAlpha
                                )
                                drawCircle(
                                    color = bgRaised,
                                    radius = thumbRadius.toPx(),
                                    center = Offset.Zero
                                )
                                drawCircle(
                                    color = current,
                                    radius = thumbRadius.toPx(),
                                    center = Offset.Zero,
                                    style = thumbStroke
                                )
                            }
                        }
                    }
                }
                .then(
                    if (latestOnSeek != null) {
                        Modifier.pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = { offset ->
                                    dragging = true
                                    // MATH: pixels -> 0..1
                                    latestOnSeek?.invoke(xToProgress(offset.x, size.width))
                                },
                                onDragEnd = { dragging = false },
                                onDragCancel = { dragging = false }
                            ) { change, _ ->
                                // MATH: pixels -> 0..1
                                latestOnSeek?.invoke(xToProgress(change.position.x, size.width))
                            }
                        }
                    } else Modifier)
                    then if (onSeek != null) {
                Modifier.pointerInput(Unit) {
                    // MATH: pixels -> 0..1
                    detectTapGestures { offset ->
                        latestOnSeek?.invoke(
                            xToProgress(
                                offset.x,
                                size.width
                            )
                        )
                    }
                }
            } else Modifier
        )
        if (showTimes) ScrubberTimes(progress = progress, durationMs = durationMs)
    }
}

@Preview
@Composable
private fun ScrubberPreview() {
    var p by remember { mutableFloatStateOf(0.35f) }
    LaunchedEffect(Unit) {
        while (true) {
            if (p < 1f) {
                delay(500.toDuration(DurationUnit.MILLISECONDS))
                p += 0.01f
            } else p = 0f
        }
    }
    FathomTheme { FathomWaveFormScrubber(progress = { p }, onSeek = { p = it }) }
}

// ---------------------------------------------------------------
// MATH helpers. No Compose in here: numbers in, numbers out.
// ---------------------------------------------------------------

/**
 * One full lap of sin, in radians: 2 * pi = 6.28.
 * sin(a + FULL_LAP) == sin(a), so when phase jumps from FULL_LAP back to 0f the picture doesn't change.
 */
private val FULL_LAP = 2f * PI.toFloat()

/**
 * The point between start and stop: fraction 0f -> start, 1f -> stop, 0.5f -> exactly halfway.
 * Same formula as the Offset lerp you used before (A + (B - A) * t), just for one Float.
 * Written here because Compose's Float lerp lives in the ui-util library, which this module may not include.
 */
private fun lerp(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction

/**
 * Finger x (pixels) -> progress (0..1).
 * x = 0 -> 0f (left edge). x = width -> 1f (right edge).
 * A finger dragged past either edge is clamped, so progress never leaves 0..1.
 */
internal fun xToProgress(x: Float, width: Int): Float = (x / width).coerceIn(0f, 1f)

/**
 * The two timestamps under the scrubber, e.g. Pair("1:14", "-2:20").
 * Not used yet. It's for the timestamps step.
 *
 * Works in whole seconds, so elapsed + remaining always equals the track length:
 * 214 s track at progress 0.35 -> elapsed 74, remaining 140, and 74 + 140 = 214.
 * durationMs is milliseconds, the unit ExoPlayer gives you. A negative value (unknown length) is treated as 0.
 */
internal fun timeLabels(progress: Float, durationMs: Long): Pair<String, String> {
    val totalSeconds = (durationMs.coerceAtLeast(0L) / 1000L).toInt()
    // toInt() throws away the fraction: 0.35 * 214 = 74.9 -> 74
    val elapsedSeconds = (progress.coerceIn(0f, 1f) * totalSeconds).toInt()
    val remainingSeconds = totalSeconds - elapsedSeconds
    return Pair(formatTime(elapsedSeconds), "-" + formatTime(remainingSeconds))
}

/** Whole seconds -> "m:ss". 74 -> "1:14", 140 -> "2:20", 5 -> "0:05". */
internal fun formatTime(seconds: Int): String {
    val minutes = seconds / 60   // Int / Int throws away the fraction: 74 / 60 = 1
    val secs = seconds % 60      // what's left over after the full minutes: 74 % 60 = 14
    return "$minutes:" + secs.toString().padStart(2, '0')   // 5 -> "05"
}