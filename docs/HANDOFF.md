# Handoff note — laptop → desktop

**From:** laptop Claude session · **Date:** 2026-09-12 · **On top of:** `5005ecf`

First round on the laptop, and the first handoff note in this repo. Scope was narrow:
clone the project and get it building here. **No feature code was written or changed** —
every Kotlin file is exactly as `bcc3507` left it.

## What landed (`5005ecf`)

Two things were missing that Forge already had, and the build could not run without the
first of them.

- **`local.properties`** written with `sdk.dir=/home/yash/Android/Sdk`. Gitignored, so it
  is not in the commit and the desktop still needs its own.
- **`gradle.properties`** rewritten from four generic lines to the Forge-style tuning,
  with a smaller memory budget. Full reasoning is in the commit message and in the new
  `docs/CROSS-MACHINE-SETUP.md`; the short version is that Forge's `-Xmx3g` gets the
  Gradle daemon OOM-killed on this laptop when two Android Studio instances are open.

The JBR requirement needed nothing: `~/.gradle/gradle.properties` is user-global, so the
laptop's existing `org.gradle.java.home` already applied to a fresh clone. The daemon log
confirms the build ran on `android-studio-otter/jbr`, JetBrains s.r.o., not the distro
OpenJDK.

## Verified

`./gradlew :app:assembleDebug --rerun-tasks --no-build-cache`, on the committed
configuration with no command-line overrides:

```
BUILD SUCCESSFUL in 46s
42 actionable tasks: 42 executed
app/build/outputs/apk/debug/app-debug.apk   (24 MB)
```

Every heavy task genuinely executed rather than resolving from cache — `kspDebugKotlin`,
`compileDebugKotlin`, `hiltAggregateDepsDebug`, `hiltJavaCompileDebug`, `dexBuilderDebug`,
`mergeProjectDexDebug`, `packageDebug`. Zero compiler warnings. `aapt2 dump badging`
reports `com.development.fathom`, minSdk 26, targetSdk 36, compileSdk 36. No OOM kill in
the kernel log for that run.

Three findings worth carrying into the toolchain notes, all recorded in
`docs/CROSS-MACHINE-SETUP.md`:

- **Hilt 2.58 does not need Forge's `enableAggregatingTask = false`.** Both
  `hiltAggregateDepsDebug` and `hiltJavaCompileDebug` execute and pass on Kotlin 2.3.21.
  Forge sets that flag because 2.57.2 cannot read metadata 2.3.0. **Forge can probably
  drop it on a Hilt bump, and then also drop `ksp.incremental=false`**, which exists there
  only to dodge the stale-PSI crash the workaround itself causes.
- **lifecycle 2.10.0 builds on AGP 8.13.2 / compileSdk 36.** Forge's setup note says
  2.10.0+ requires AGP 9.2.0. `lifecycle-runtime-ktx`, `-runtime-compose` and
  `-viewmodel-compose` all resolve at 2.10.0 here and compile clean. That constraint is on
  `lifecycle-viewmodel-navigation3`, which this project does not use.
- **`kotlin.incremental.useClasspathSnapshot` is dead.** Kotlin 2.3 rejects it as
  unsupported and warns every build. Not copied over from Forge; **Forge still sets it and
  is still emitting that warning.**

## Not verified this round

- **Nothing has been run on a device or emulator.** The APK was built and inspected, never
  installed. There is no reason to expect a useful screen if it were: `MainActivity`'s
  `setContent { FathomTheme { } }` has an empty body, so the app launches to a blank
  window by construction.
- **The downloadable-font path has never executed.** `MainActivity` preloads both families
  through `createFontFamilyResolver` on a background scope, but no text is rendered and no
  font request has left the device. See the provider bug below.
- **No test was run.** `ExampleUnitTest` and `ExampleInstrumentedTest` are untouched
  scaffolding; `testDebugUnitTest` was never invoked.
- **Release variant never built.** Only `assembleDebug`. `isMinifyEnabled = false` on
  release, so `proguard-rules.pro` is inert and unexercised.

## Found but not fixed

Read during the build review and deliberately left alone, since this round was scoped to
building, not to touching feature code. None of these break compilation.

- **`FathomFonts` points at the wrong font provider.** It declares
  `providerAuthority = "com.google.play.gms.fonts"` and
  `providerPackage = "com.google.play.gms"`. The real values, which Forge uses, are
  `com.google.android.gms.fonts` and `com.google.android.gms` — and this project's own
  `res/values/font_certs.xml` comment names the correct authority, as does its
  `com_google_android_gms_fonts_certs` array. As written, resolution will fail silently at
  runtime and every style falls back to the system face. **This is the one I would fix
  first**; it is a two-string change but invisible until something renders text.
- **`FathomShapes` is defined and never wired.** `FathomTheme` passes only `typography` to
  `MaterialTheme`, so the shape scale is unreachable. `FathomRadius` is used directly by
  `FathomButton`, so only the `Shapes` object is orphaned.
- **`FathomButtonSize.textStyle` cannot return a text style.** It is typed
  `@Composable () -> Unit`, so `{ FathomTypography.bodySmall }` has its value coerced away
  to `Unit` and the per-size style is unreachable. Wants to be
  `@Composable () -> TextStyle`, or a plain `TextStyle`.
- **`FathomButton(enabled = false)` by default**, which per its own spec means the gradient
  is replaced by flat `bgSunken`. Reads like a placeholder rather than an intent.
- **`LocalFathomColors` uses `compositionLocalOf`,** not `staticCompositionLocalOf`. For a
  theme object that changes only on a light/dark switch, the static variant avoids
  invalidating every reader.
- **`@Preview` sits on the components themselves** (`FathomButton`, `FathomNavHost`) rather
  than on separate preview functions. It compiles because every parameter has a default,
  but it puts the shipping component in the IDE preview list.

## Carried over from before this session

Nothing. This is the first handoff note in the repo, and `bcc3507` and earlier carry no
open items recorded anywhere I could find.

## Suggested next step

`FathomButton` is the live edge: an extensive doc spec with an empty `Box` body, no label,
no icon, no press or loading state. The font provider fix above is worth doing before any
of that, so the first thing rendered is rendered in the right typeface.
