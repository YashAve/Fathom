# Handoff note — laptop → desktop

**From:** laptop Claude session · **Date:** 2026-09-14 · **On top of:** `419a7de`

Same instruction a third time: commit and push, **no** compile or build check. Three
consecutive rounds now with zero build verification — `FathomChip` lands on top of
`FathomPaginationDots` on top of `FathomSectionLabel`, none of it ever run through
`assembleDebug`. Repeating the flag from the last two notes rather than dropping it,
since the stack is only getting taller.

## What landed (`419a7de`)

**`FathomChip.kt`** — new file, same situation as the last two rounds: found complete
in the working tree when this session resumed, not authored here.

What it does: a `Box` (shadow, clip, gradient-or-flat background, press-scale
`graphicsLayer`, `clickable`) wrapping a `Row` (optional icon, label, optional count) —
the same two-layer idiom `FathomButton` and `FathomPaginationDots` both use. Label at
`bodySmall` with a `W500`/`1.em` override, `maxLines = 1`. Count only drawn when
non-null, at `labelMedium` and 72% content-colour alpha. Three unselected variants —
Current (`currentWash` / `textAccent`), Neutral (`bgSunken` / `fgSecondary`), Warm
(`warmWash` / `warm`) — all match the spec's colour pairs exactly. `selected = true`
overrides every variant with the shared teal gradient, `currentOn` content, and the
current-colour glow, matching the spec's "same structure as `FathomButton`'s disabled
state" note. Press scale is 0.95 (not the button's 0.972), applied only when `onClick`
is non-null, exactly as specified.

## Not verified this round

Everything, again — no `./gradlew` run this round either:

- Whether `FathomChip.kt` compiles. Same eye-only caveat as the last two notes.
- `FathomSectionLabel.kt` and `FathomPaginationDots.kt` from the last two rounds —
  **still** unverified. Nobody has run a build since `a7306b3`, four rounds back now.
- Everything already open from before: no device/emulator run, no
  `testDebugUnitTest`, no release variant.

## Found, not fixed

- **The icon parameter doesn't match its own spec.** The doc comment says: "A
  `@Composable () -> Unit` slot, same as `FathomButton`." The actual signature is
  `icon: ImageVector? = null`, rendered internally via a hardcoded `Icon(imageVector =
  icon, ..., tint = contentColor)`. That's a real API-shape difference, not a cosmetic
  one — a composable slot lets the caller draw anything at any size; a nullable
  `ImageVector` locks every chip icon to Compose's stock `Icon` at a fixed 16.dp and
  the computed `contentColor`, and it means `FathomChip` and `FathomButton` take
  different argument types for what the spec calls the same kind of slot.
- **Two unused imports, one of them actively shaping the code around it.**
  `android.graphics.drawable.Icon` (the framework class, not the Compose one) and
  `androidx.compose.ui.draw.dropShadow` are both imported and never used. The stray
  `android.graphics.drawable.Icon` import is likely *why* line 241 calls
  `androidx.compose.material3.Icon(...)` fully-qualified instead of importing it
  cleanly and calling `Icon(...)` bare — with both imported unqualified, `Icon` would
  be an ambiguous reference. Worth removing the dead import rather than leaving the
  fully-qualified call as the permanent workaround.
- **`selected` defaults to `true`.** Third occurrence of the same shape of issue,
  not a new one: `FathomButton`'s old `enabled = false` (fixed in `a7306b3`),
  `FathomSectionLabel`'s `accent = true` (flagged, still open), and now `FathomChip`'s
  `selected = true`. In every case the default renders the special/override state
  rather than the plain one. Three independent components with the same wrong-default
  shape reads like a pattern worth checking at the source — a shared template or habit
  — rather than three unrelated slips.
- No trailing newline at EOF (same as the last two files).

## Carried over from before this session

Unchanged, none touched this round:

- **`FathomPaginationDots`'s modifier-reuse bug** — each dot's `Box` builds from the
  composable's own `modifier` parameter inside the `repeat` loop instead of a fresh
  `Modifier`, so a caller-supplied modifier would land on every dot as well as the Row.
- **`FathomPaginationDots`'s colour cross-fade isn't implemented** — the spec calls for
  animating the gradient's alpha over the flat background across 420ms; the code swaps
  with a hard `if`, so the transition is instant.
- **`FathomSectionLabel`'s no-rule-with-action branch is still unimplemented** — no
  `Spacer(weight(1f))` push-right when `rule = false` and `action` is non-null.
- **`FathomFonts` still points at the wrong font provider** —
  `com.google.play.gms.fonts` / `com.google.play.gms`, should be
  `com.google.android.gms.fonts` / `com.google.android.gms`. Still the one I'd fix
  first — four components deep into rendering text/colour through this theme now,
  the font path has never once been exercised.
- **`FathomShapes` still orphaned.**
- **`LocalFathomColors` still uses `compositionLocalOf`**, not `staticCompositionLocalOf`.
- **`@Preview` still sits directly on the shipping composables**
  (`FathomButton`, `FathomSectionLabel`, `FathomPaginationDots`, `FathomChip`,
  `FathomNavHost`).

## Suggested next step

Run `./gradlew :app:assembleDebug` first — that is now the standing recommendation
three notes running, and the risk of an accumulated, hard-to-isolate failure only grows
the longer it's deferred. After that's green, the wrong-default pattern across three
components is worth a single pass rather than three separate fixes.

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_018ctkZ7BujomG2ctZonwC5M
