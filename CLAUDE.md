# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project shape

ABK UI has two deliverables:

- The original static web catalog is `index.html` with shared roles in `assets/css/tokens.css`, reusable styles in `base.css`, `components.css`, `forms.css`, `overlays.css`, and `navigation.css`, and catalog-only layout in `assets/css/demo.css`.
- The native deliverable is `:abk-ui`, an Android Jetpack Compose library, plus `:catalog`, an interactive directory app. Native APIs are under `abk-ui/src/main/kotlin/.../theme`, `icons`, and `components`; app-only state and screen compositions are under `catalog/src/main/kotlin`. The catalog has four equal-weight destinations: the full component directory, a focused Component Preview screen (`ComponentPreview.kt`), and the local-only Build and Modules examples.

Use the web catalog as the visual inventory, not as a reason to put build or module business data into the library. The native coverage and validation boundary are tracked in [docs/compose-coverage.md](docs/compose-coverage.md).

## Native toolchain and commands

The native build uses JDK 17, the checked-in Gradle Wrapper 8.13, AGP 8.11.1, Kotlin/Compose compiler 2.2.21, and Compose BOM 2025.11.01. SDK API 36 is required for the configured compile/target SDK. Always select Java 17 at command level; do not change the machine's default Java.

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :abk-ui:assembleRelease :catalog:assembleDebug
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :abk-ui:testDebugUnitTest :abk-ui:lintDebug :catalog:lintDebug
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :abk-ui:testDebugUnitTest --tests 'io.github.xingguangcuican6666.abk.ui.AbkComponentsTest'
```

These commands need a user-reviewed Android SDK installation. Do not accept SDK licenses automatically, copy another machine's license files, or add `local.properties` to Git. There is no connected device requirement for host tests, but without an emulator/device native rendering, installation, TalkBack, IME, and pixel acceptance remain unverified.

Compilation targets Java 17 bytecode; `tasks.withType<Test>` in `abk-ui/build.gradle.kts` pins `javaLauncher` to Java 21 because the Robolectric SDK 36 fixture requires it. Keep `JAVA_HOME` on 17 for every command — the test JVM is selected separately.

For an SDK-free source check during development, use the temporary local workflow outside the repository that compiles the Kotlin sources against the resolved Compose artifacts. It is not a substitute for Gradle packaging and should not be documented as an AAR/APK build.

## Web preview and verification

For working-tree changes, open `index.html` directly. For a localhost preview of the **committed** snapshot, run this from the repository root in Bash. Open `http://127.0.0.1:8080` and press Ctrl+C to stop the server and clean up. Do not serve the repository root.

```bash
(
  set -o pipefail
  preview=$(mktemp -d) || exit 1
  trap 'rm -rf -- "$preview"' EXIT
  git archive HEAD | tar -xf - -C "$preview" || exit 1
  python3 -I -m http.server 8080 --bind 127.0.0.1 --directory "$preview"
)
```

For JS syntax use `node --check assets/js/abk-ui.js`, `node --check assets/js/demo-palette.js`, and `node --check assets/vendor/icons/icon-data.js`. For icon generation use `python3 scripts/generate-compose-icons.py`; the generator must be the only source of geometry in `AbkIcons.kt`. Run `git diff --check` before handoff.

## Design constraints

- Lucide is the finalized icon family. `assets/vendor/icons/icon-data.js` is the web registry and `scripts/generate-compose-icons.py` derives native vectors from it. Keep the 30 semantic roles, the `info-mark` dot, and RTL mirroring for directional icons.
- Material You roles use system dynamic color on Android 12+ and a deterministic fixed seed fallback. The seed path mirrors the web's Tonal Spot / 2021 / phone / contrast 0 mapping. Success and warning are separate semantic palettes.
- Bottom destinations are equal weight; selection does not create a prominent pill or elevated primary action. Build cards use ordinary surfaces; status colors stay local to progress and status controls. Toast/Snackbar with undo stays on the current theme surface.
- Use external, undecorated field labels. Preserve at least 48dp touch targets, visible focus, state descriptions, and meaningful icon content descriptions. Do not make color the only error, warning, selected, or loading signal.
- Library state is controlled by the host and does not persist preferences. Catalog sample state uses `rememberSaveable` and must remain in `:catalog`.

## Safety and repository boundaries

Do not commit SDK paths, `local.properties`, credentials, signing keys, debug keystores, caches, temporary screenshots, or build outputs. Do not publish Maven/APK artifacts, push branches, or replace the repository license without an explicit current request.
