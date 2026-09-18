# Handoff note — desktop → laptop

**From:** desktop Claude session · **Date:** 2026-09-18 · **On top of:** `ef12720`

Same-day-as-usual follow-up to the `7d54b9a` note: `FathomTextField.kt` was found complete
in the working tree — not authored in this conversation, verified and committed, same
pattern as every round so far. This one's worth reading closely before picking up: it has
a correctness bug more severe than anything found in prior rounds.

## What landed (`ef12720`)

**`FathomTextField.kt`** — new file. The 56dp rounded field for auth screens and search:
label eyebrow, leading icon, animated focus/error border, optional strength bar (gradient
fill via `drawWithCache`, clipped to `strength * width`), hint/error text below.

Different from every prior component: this one **self-marks its own gaps** with three
inline `// TODO` comments (halo ring, shadow tiers, disabled treatment) instead of leaving
them silently unimplemented. Worth calling out as the better pattern — every finding below
is something the file itself doesn't disclose.

Verified with a full clean `:app:assembleDebug --rerun-tasks --no-build-cache` —
`BUILD SUCCESSFUL in 21s`, 42/42 tasks executed. No warnings beyond the pre-existing SDK
XML-version notice. None of the findings below are compile errors.

## Security finding — from an automated post-commit review, not found by reading

A background security review of the `ef12720` commit flagged **2 issues**; only the
first came through with a name, the second only as "+1 more" with no detail and no
retrievable task/transcript to pull it from. Not guessing at what the second one is —
flagging that it exists and is unverified, rather than inventing a description.

- **`sensitive-data-leak-to-ime`, confirmed by direct inspection.** `BasicTextField` here
  is called with no `KeyboardOptions` at all (`grep -n "KeyboardOptions\|KeyboardType"`
  on the file returns nothing), so it runs on `KeyboardOptions.Default` —
  `keyboardType = KeyboardType.Text`. That means even when `isPassword = true`, the IME
  is never told this is a sensitive field. Visual masking via `PasswordVisualTransformation`
  (already broken — see the always-on-regardless-of-`isPassword` finding below) only
  stops shoulder-surfing; without `KeyboardType.Password`, IMEs remain free to learn,
  autocorrect, and — on cloud-connected keyboards — sync the actual keystrokes. There's
  also no autofill `contentType`/semantics hint anywhere, so autofill services don't know
  to treat this as a password field either. Compounds directly with the masking and
  reveal-eye bugs below: right now there is no layer, visual or IME-level, that reliably
  treats password input as sensitive.
- **Second finding: unretrieved.** The task notification named only
  `sensitive-data-leak-to-ime` explicitly and appended "+1 more" with nothing else —
  no `task_id` surfaced, no spawned agent to query for the full report. Whoever picks
  this up next may have access to the same review tooling and get the complete list;
  this note only carries the one confirmed item.

Not fixed here, per standing instruction — documented only.

## Found, not fixed — new this round, undisclosed by the file's own TODOs

- **`decorationBox` never calls `innerTextField`.** `BasicTextField`'s trailing lambda
  *is* its `decorationBox` (last parameter, trailing-lambda syntax) and is required to
  invoke the `innerTextField` composable it receives to actually render the field's text.
  This lambda only conditionally shows the placeholder and never references
  `innerTextField` at all. Net effect: an empty field shows the placeholder, but the
  moment `value` becomes non-empty, **neither the placeholder nor the typed text
  renders** — the field goes visually blank while still accepting input. This is the
  most severe finding of any round so far: not a missing polish detail, the field cannot
  show what's typed into it.
- **`visualTransformation` ignores `isPassword` entirely.** It's
  `if (revealed) VisualTransformation.None else PasswordVisualTransformation()` —
  `revealed` starts `false` and nothing ever sets it (see next finding), so **every**
  field, password or not, masks its input by default. `isPassword` only gates whether the
  reveal-eye icon is drawn, never the masking itself.
- **The reveal-eye icon has no click handler.** Rendered when `isPassword` is true, but
  no `.clickable`/`onClick` anywhere sets `revealed` — purely decorative, compounding the
  masking bug above with no way to work around it from the UI.
- **hint/error text colour keys off the wrong null-check.** `if (hint != null) fgTertiary
  else warm` — the text itself correctly prefers `error` (`error ?: hint!!`), but the
  colour check should mirror that (`if (error != null) warm else fgTertiary`) and doesn't.
  When both `hint` and `error` are supplied together, the error string renders in grey
  `fgTertiary` instead of coral `warm`. Only correct when exactly one of the two is set.

## Carried over from before this session

- **Wrong-default pattern, five occurrences** — `FathomIconButton.active`,
  `FathomToggle.checked`, `FathomChip.selected`, `FathomSectionLabel.accent`, all
  defaulting to the override/special state rather than the plain one.
  (`FathomTextField`'s defaults — `value`/`label`/`placeholder`/`hint` = `"Fathom"`,
  `strength = 0.53f` — are `@Preview` placeholders, not this same shape of bug; not
  counted here.)
- **Disabled-state gap, now four components** — `FathomTextField` joins
  `FathomToggle`/`FathomChip`/`FathomIconButton`, but is the first to mark it with an
  explicit TODO rather than leave it silent.
- **`FathomChip`'s icon slot doesn't match its own spec** — doc says
  `@Composable () -> Unit`, actual signature is `icon: ImageVector? = null`.
- **Two unused imports in `FathomChip`** — `android.graphics.drawable.Icon` and
  `androidx.compose.ui.draw.dropShadow`.
- **`FathomPaginationDots`'s modifier-reuse bug** — each dot's `Box` builds from the
  composable's own `modifier` parameter inside the `repeat` loop instead of a fresh
  `Modifier`.
- **`FathomPaginationDots`'s colour cross-fade isn't implemented** — hard `if` swap
  instead of the spec'd 420ms alpha animation.
- **`FathomSectionLabel`'s no-rule-with-action branch is still unimplemented.**
- **`FathomFonts` still points at the wrong font provider** —
  `com.google.play.gms.fonts` / `com.google.play.gms`, should be
  `com.google.android.gms.fonts` / `com.google.android.gms`. Eight components deep into
  rendering text/colour through this theme now, still never exercised.
- **`FathomShapes` still orphaned.**
- **`LocalFathomColors` still uses `compositionLocalOf`**, not `staticCompositionLocalOf`.
- **`@Preview` still sits directly on the shipping composables**
  (`FathomButton`, `FathomSectionLabel`, `FathomPaginationDots`, `FathomChip`,
  `FathomToggle`, `FathomIconButton`, `FathomTextField`, `FathomNavHost`).
- No trailing newline at EOF on the last six new files.

## Not verified this round

No device/emulator run, no release variant — unchanged gaps, every round so far.

## Suggested next step

The `sensitive-data-leak-to-ime` finding outranks everything else on this list — it's the
only item so far in the security category rather than correctness or polish, and it's
about data leaving the device rather than a screen just looking wrong. Add
`keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password
else KeyboardType.Text)` (and get the missing second finding from a fresh review, since
this note couldn't retrieve it) before anything else touches this file. Right after that,
the `decorationBox` bug is still the one item that makes the component look broken rather
than merely unfinished — any screen built against this field right now would appear to
eat user input silently. The two cross-cutting patterns (five wrong defaults, four missing
disabled states) remain the highest-leverage passes after both of those.

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_01TbnXJC297hkg8tpvJFE7RR
