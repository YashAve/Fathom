# Cross-machine setup notes (desktop ↔ personal laptop)

Fathom is built on two machines that run **different Android Studio versions** and have
different SDK/JBR locations. This note records what each machine needs so a `git pull`
from one never breaks the build on the other.

Nothing machine-specific (absolute paths, JDK homes) should be committed into
`gradle.properties` — keep those in each machine's own `~/.gradle/gradle.properties`.

Same arrangement as the Forge project; the differences from Forge are called out at the
bottom, because several of Forge's workarounds are **not** needed here.

## Toolchain the repo currently targets

Pinned in `gradle/libs.versions.toml` (committed, same on every machine):

| Tool | Version | Notes |
|------|---------|-------|
| Android Gradle Plugin | 8.13.2 | **Not** AGP 9 |
| Gradle wrapper | 8.14 | `gradle/wrapper/gradle-wrapper.properties` (Forge is on 8.13) |
| compileSdk / targetSdk | 36 | SDK Platform **36** must be installed |
| minSdk | 26 | |
| Java / Kotlin JVM target | 17 | |
| Kotlin | 2.3.21 | uses the `compilerOptions` DSL, not the removed `kotlinOptions` |
| KSP | 2.3.11 | Hilt + Room annotation processing |
| Hilt | 2.58 | needs **no** `enableAggregatingTask` workaround — see below |
| Compose BOM | 2026.06.01 | |
| lifecycle | 2.10.0 | builds fine on AGP 8.13.2 — see below |
| Navigation 3 | 1.1.7 | `navigation3-runtime` + `navigation3-ui` |
| Room | 2.8.4 | |
| Media3 | 1.11.0 | `exoplayer` + `session` + `common` |

## Per-machine setup checklist (do this once per machine)

1. **JDK / JBR** — Gradle must run on Android Studio's bundled JBR. The distro OpenJDK
   SIGSEGVs inside R8/D8 during dexing (a JIT fault, not memory pressure). Put this
   machine's own path in `~/.gradle/gradle.properties` (untracked, user-global, so it
   applies to every project on the machine):
   ```properties
   org.gradle.java.home=/path/to/this/machine/android-studio/jbr
   ```
   Find it with: `find ~ /opt -maxdepth 5 -type d -name jbr 2>/dev/null`
   (On the laptop it is `/home/yash/software/android-studio-otter/jbr`.)
2. **`local.properties`** — gitignored, and a fresh clone has none, so Gradle cannot
   find the SDK until you write it:
   ```properties
   sdk.dir=/path/to/this/machine/Android/Sdk
   ```
   (On the laptop: `/home/yash/Android/Sdk`.)
3. **SDK Platform 36** — install via SDK Manager (`platforms;android-36`). compileSdk is 36.
4. **Android Studio version** — any Studio that supports **AGP 8.13.2** is fine. Neither
   machine needs SDK Platform 37 or AGP 9 for the current setup.

## Gotcha: build memory on the laptop

The laptop has 15 GiB and frequently runs **two** Android Studio instances (~7.8 GiB
between them) with swap already full. Forge's committed `-Xmx3g` gets the Gradle daemon
**killed by the kernel OOM-killer** partway through dexing under that load:

```
oom-kill: ... task=java,pid=348851
Out of memory: Killed process 348851 (java) total-vm:9300516kB anon-rss:3168912kB
```

Gradle surfaces this only as `Gradle build daemon disappeared unexpectedly`, which reads
like a JVM crash and sends you looking at the wrong thing — in particular it is easy to
confuse with the R8/D8 SIGSEGV that makes the JBR mandatory above. Tell them apart with:

```
journalctl -k --since "10 min ago" | grep -i oom
```

So the committed budget here is smaller than Forge's: `-Xmx1600m`, a 900m Kotlin daemon
and 2 workers. Those exact numbers were verified against a full
`./gradlew :app:assembleDebug --rerun-tasks --no-build-cache` (42 of 42 tasks executed).
`gradle.properties` documents how to shrink a single run further without editing the
file, and notes that Forge's larger numbers are fine once one Studio is closed.

## Differences from Forge

Two of Forge's workarounds are deliberately absent, both verified on this project:

- **`hilt { enableAggregatingTask = false }` is not needed.** Forge sets it because Hilt
  2.57.2 reads Kotlin metadata through a bundled `kotlin-metadata-jvm` that tops out at
  metadata 2.2.0 and throws on Kotlin 2.3.x. On Hilt **2.58** both `hiltAggregateDepsDebug`
  and `hiltJavaCompileDebug` execute and pass on Kotlin 2.3.21. Forge can likely drop the
  setting on a Hilt bump.
- **`ksp.incremental=false` is not needed.** In Forge that flag exists *only* to avoid a
  stale-PSI crash caused by moving Hilt aggregation into KSP. With the aggregating task
  left alone, the conflict does not arise, so incremental KSP stays at its default.

One Forge note also does not reproduce here: Forge's `CROSS-MACHINE-SETUP.md` states that
lifecycle 2.10.0+ requires AGP 9.2.0. Fathom pins **lifecycle 2.10.0** and resolves
`lifecycle-runtime-ktx/-compose/-viewmodel-compose` at 2.10.0 on **AGP 8.13.2 /
compileSdk 36**, with a clean `assembleDebug` and no warnings. The constraint Forge hit
is on `lifecycle-viewmodel-navigation3`, which Fathom does not depend on.
