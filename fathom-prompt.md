You are setting up a brand-new Android project called **Fathom**, a local
music player. This task is SCAFFOLDING ONLY.

## Hard constraints — read before doing anything

- Do NOT implement any feature, screen, composable, ViewModel, repository,
  entity, DAO, or theme.
- Do NOT write a design system, colours, typography, or motion tokens.
- Do NOT create a playback service or any Media3 wiring.
- The only Kotlin files you may create or modify: `FathomApplication.kt`,
  `MainActivity.kt`, and the generated test stubs.
- Every other package must be created EMPTY with a `.gitkeep` file inside
  (Git does not track empty directories).
- When the structure exists and the project builds, STOP and report. Do not
  continue to "helpfully" fill anything in.

## Project configuration

- Single module: `:app`. No `:server`, no `:shared` — this app has no backend.
- `namespace` / `applicationId`: `com.yourname.fathom`   ← replace with your
  real namespace before running
- `minSdk 26`, `targetSdk 36`, `compileSdk 36`
- Kotlin JVM target 17
- Jetpack Compose enabled via the Kotlin Compose Compiler plugin (Kotlin 2.x
  style — do NOT set `composeOptions.kotlinCompilerExtensionVersion`)
- All dependency versions declared in `gradle/libs.versions.toml`. Resolve the
  latest stable release of each library and pin the exact version — do not
  guess or use `+`. If you cannot verify a version, say so rather than
  inventing one.

## Gradle plugins

- `com.android.application`
- `org.jetbrains.kotlin.android`
- `org.jetbrains.kotlin.plugin.compose`
- `org.jetbrains.kotlin.plugin.serialization`
- `com.google.devtools.ksp`
- `com.google.dagger.hilt.android`

## Dependencies

**Core / lifecycle**
- `androidx.core:core-ktx`
- `androidx.activity:activity-compose`
- `androidx.lifecycle:lifecycle-runtime-ktx`
- `androidx.lifecycle:lifecycle-runtime-compose`
- `androidx.lifecycle:lifecycle-viewmodel-compose`

**Compose** (via BOM)
- `androidx.compose:compose-bom`
- `androidx.compose.ui:ui`
- `androidx.compose.ui:ui-graphics`
- `androidx.compose.ui:ui-tooling-preview`
- `androidx.compose.ui:ui-tooling` (debug)
- `androidx.compose.material3:material3`
- `androidx.compose.material:material-icons-extended`

**Navigation 3**
- `androidx.navigation3:navigation3-runtime`
- `androidx.navigation3:navigation3-ui`
- Do NOT add `androidx.lifecycle:lifecycle-viewmodel-navigation3`. It requires
  AGP 9.1+ / compileSdk 37, which is deliberately out of scope.

**DI**
- `com.google.dagger:hilt-android`
- `com.google.dagger:hilt-compiler` (ksp)
- `androidx.hilt:hilt-navigation-compose`

**Persistence**
- `androidx.room:room-runtime`
- `androidx.room:room-ktx`
- `androidx.room:room-compiler` (ksp)
- `androidx.datastore:datastore-preferences`

**Media**
- `androidx.media3:media3-exoplayer`
- `androidx.media3:media3-session`
- `androidx.media3:media3-common`
- Do NOT add `media3-ui` — all playback UI is custom Compose.

**Other**
- `io.coil-kt.coil3:coil-compose`
- `org.jetbrains.kotlinx:kotlinx-serialization-json`

**Test**
- `junit`, `androidx.test.ext:junit`, `androidx.test.espresso:espresso-core`,
  `androidx.compose.ui:ui-test-junit4`, `androidx.compose.ui:ui-test-manifest`

Do NOT add an accompanist permissions library — permissions will be hand-rolled
with `rememberLauncherForActivityResult`.

## AndroidManifest

Declare these permissions and nothing else:

- `READ_MEDIA_AUDIO`
- `READ_EXTERNAL_STORAGE` with `android:maxSdkVersion="32"`
- `POST_NOTIFICATIONS`
- `FOREGROUND_SERVICE`
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK`
- `WAKE_LOCK`

Explicitly do NOT declare `RECORD_AUDIO`. Audio visualisation will be driven
through a Media3 `AudioProcessor` tap, not `android.media.audiofx.Visualizer`.

Do not declare any `<service>` yet — the class does not exist.

Wire `FathomApplication` as `android:name` on `<application>`.

## Kotlin files to create

1. `FathomApplication.kt` — `@HiltAndroidApp class FathomApplication : Application()`
2. `MainActivity.kt` — `@AndroidEntryPoint`, `enableEdgeToEdge()`, `setContent { }`
   with an empty `Surface`. Nothing more.

## Package structure

Create every package below, each containing a `.gitkeep`:

```
com.yourname.fathom
├── core
│   ├── designsystem
│   │   ├── theme
│   │   └── component
│   │       ├── core          // buttons, fields, chips, toggle, labels
│   │       ├── fluid         // blob, visualiser, mesh, ripple, waveform, play
│   │       ├── media         // album art, track row, playlist card, mini player
│   │       ├── navigation    // app bar, bottom nav, dock
│   │       ├── onboarding    // the three heroes, wordmark
│   │       └── feedback      // empty, loading, dialog, sheet
│   └── util
├── data
│   ├── local
│   │   ├── database
│   │   │   ├── entity
│   │   │   └── dao
│   │   ├── mediastore
│   │   └── datastore
│   ├── model
│   └── repository
├── playback
│   └── visualizer
├── feature
│   ├── onboarding
│   ├── permission
│   ├── library
│   ├── nowplaying
│   ├── queue
│   ├── playlist
│   ├── search
│   └── settings
├── navigation
└── di
```

Also create `app/src/main/res/font/` with a `.gitkeep` — Space Grotesk and
JetBrains Mono `.ttf` files will be dropped in manually later.

Each `feature/*` package will eventually hold a contract, a ViewModel, and
screen composables. Create the packages only.

## Done criteria

1. `./gradlew :app:assembleDebug` succeeds.
2. Every package above exists and is tracked by Git.
3. No Kotlin file exists beyond the three named above.

Report the resolved version of each dependency you pinned, then stop.". there is already a git file, create a work branch and do the commit and push on it
