# Handoff note — desktop → laptop

**From:** desktop Claude session · **Date:** 2026-09-15 · **On top of:** `6c7f2b2`

Two things landed this round: the build verification the last three notes kept asking
for, and one new component found waiting in the working tree — not authored in this
conversation, just committed and read closely enough to describe accurately, same as
your last three rounds.

## What landed (`6c7f2b2`)

**`FathomToggle.kt`** — new file. The 54×32dp switch: track (border+`bgSunken` off, teal
gradient+glow on, flat `bgSunken` disabled) with a knob positioned by `BiasAlignment` so
travel distance recomputes for free when the knob stretches 26.dp → 30.dp on press —
same idiom as the doc comment's own explanation of why `BiasAlignment` beats a fixed
offset.

## Verified

```
./gradlew :app:assembleDebug --rerun-tasks --no-build-cache
BUILD SUCCESSFUL in 19s
42 actionable tasks: 42 executed
app/build/outputs/apk/debug/app-debug.apk   (24 MB)
```

Run before `FathomToggle.kt` landed, so it covers `FathomSectionLabel` (`3649989`),
`FathomPaginationDots` (`97e7dda`) and `FathomChip` (`419a7de`) compiling clean together —
answers the repeated flag from the three notes before this one. Zero Kotlin compiler
warnings, including on the ambiguous-`Icon`-import question raised last note: the
fully-qualified `androidx.compose.material3.Icon(...)` call in `FathomChip` produces no
warning either way, so that one stays a style finding, not a correctness one. Only warning
anywhere in the log is the pre-existing, unrelated SDK XML-version notice this project has
printed since round one.

Also ran `:app:testDebugUnitTest` — `BUILD SUCCESSFUL`. Still the untouched
`ExampleUnitTest` scaffolding, so this confirms the test task itself works, not new
coverage.

**Not re-run after `FathomToggle.kt` landed** — that file is new since the verification
above, not yet built. Worth a build check before or alongside whatever touches it next.

## Not verified this round

- No device/emulator run — same gap as every round so far.
- Release variant — still only ever `assembleDebug`.
- `FathomToggle.kt` itself, per the note above.

## Found, not fixed

**In `FathomToggle.kt`, new this round:**

- **Icon slot is an empty `Box`.** `iconOn`/`iconOff` are accepted parameters but nothing
  renders them — no `Icon(...)` call inside the knob's inner `Box`.
- **`.clickable` is never applied to the track.** `interactionSource` and `isPressed`
  are collected via `collectIsPressedAsState()`, but no modifier in the chain feeds press
  events into that source — so `onChange` never fires and the press-stretch animation
  (26.dp → 30.dp) has no way to trigger. This is a step further from working than the
  other WIP components: those render but may behave oddly; this one can't respond to a
  tap at all yet.
- **`checked` defaults to `true`** — a fourth occurrence of the wrong-default pattern,
  joining `FathomChip.selected`, `FathomSectionLabel.accent`, and the already-fixed
  `FathomButton.enabled` case below. Four for four now; still reads like a shared
  template or habit rather than four unrelated slips.

**Unchanged from `e7af684`:**

- **`FathomChip`'s icon slot doesn't match its own spec** — doc says
  `@Composable () -> Unit`, actual signature is `icon: ImageVector? = null`.
- **Two unused imports in `FathomChip`** — `android.graphics.drawable.Icon` and
  `androidx.compose.ui.draw.dropShadow` — the former likely why the `Icon(...)` call is
  fully-qualified instead of a clean unqualified import.
- No trailing newline at EOF on the last four new files.

## Carried over from before this session

Unchanged, none touched this round:

- **`FathomPaginationDots`'s modifier-reuse bug** — each dot's `Box` builds from the
  composable's own `modifier` parameter inside the `repeat` loop instead of a fresh
  `Modifier`.
- **`FathomPaginationDots`'s colour cross-fade isn't implemented** — hard `if` swap
  instead of the spec'd 420ms alpha animation.
- **`FathomSectionLabel`'s no-rule-with-action branch is still unimplemented.**
- **`FathomFonts` still points at the wrong font provider** —
  `com.google.play.gms.fonts` / `com.google.play.gms`, should be
  `com.google.android.gms.fonts` / `com.google.android.gms`. Six components deep into
  rendering text/colour through this theme now, still never exercised.
- **`FathomShapes` still orphaned.**
- **`LocalFathomColors` still uses `compositionLocalOf`**, not `staticCompositionLocalOf`.
- **`@Preview` still sits directly on the shipping composables**
  (`FathomButton`, `FathomSectionLabel`, `FathomPaginationDots`, `FathomChip`,
  `FathomToggle`, `FathomNavHost`).

## Suggested next step

`FathomToggle`'s missing `.clickable` is the one item on this list that isn't a polish
issue — everything else renders and can be exercised once put on a screen; this one
can't be interacted with at all yet, so it's worth closing before the component goes
further. The wrong-default pattern (now four occurrences) and the font provider strings
remain the cheapest, oldest open items otherwise.

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_01TbnXJC297hkg8tpvJFE7RR
