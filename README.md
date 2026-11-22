# ABK UI

ABK UI is a reusable Android Jetpack Compose component library based on the finalized static design prototype. It uses Yozai (悠哉字体) for UI text and LXGW WenKai Mono for code, Material You semantic roles, and the Lucide SVG subset. The repository retains the original static catalog as well as a native library and a separate native catalog app.

## Modules

- `:abk-ui` — Android library (`io.github.xingguangcuican6666.abk.ui`). Public APIs use the `Abk*` prefix and are grouped under `theme`, `icons`, and `components`.
- `:catalog` — Android application (`io.github.xingguangcuican6666.abk.catalog`). It provides a full interactive component directory, a focused **Component Preview** screen, and local-only Build and Modules compositions; none of these connect to a build service, download modules, install software, or alter a device.

The migration matrix is [docs/compose-coverage.md](docs/compose-coverage.md). The web catalog remains `index.html`; its styles and behavior are not a dependency of the native library.

## Native setup

The project pins JDK 17, Gradle Wrapper 8.13, AGP 8.11.1, Kotlin/Compose compiler 2.2.21, and Compose BOM 2025.11.01 (Compose 1.9.5 / Material3 1.4.0). `minSdk` is 23 and `compileSdk`/`targetSdk` are 36.

The wrapper distribution has a SHA-256 checksum in `gradle/wrapper/gradle-wrapper.properties`. Use a user-reviewed Android SDK location through `local.properties` or `ANDROID_HOME`; `local.properties`, SDKs, signing keys, and build outputs are ignored. Do not accept SDK licenses automatically on someone else's behalf.

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :abk-ui:assembleRelease :catalog:assembleDebug
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :abk-ui:testDebugUnitTest :abk-ui:lintDebug :catalog:lintDebug
```

The full commands require an installed Android SDK with the requested platform and build tools. If those are unavailable, the source can still be checked with the local Compose/Kotlin compiler workflow used during development, but that is not an AAR/APK build.

Compilation targets Java 17 bytecode, but `Test` tasks launch on Java 21 because the Robolectric SDK 36 fixture requires it. `abk-ui/build.gradle.kts` sets that `javaLauncher`, so `JAVA_HOME` stays on Java 17 for every command, including:

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :abk-ui:testDebugUnitTest
```

## Minimal usage

```kotlin
AbkTheme(darkTheme = isSystemInDarkTheme()) {   // Android 12+: system dynamic color
    AbkScaffold(topBar = { AbkTopBar("ABK UI") }) { padding ->
        Column(Modifier.padding(padding)) {
            AbkHeading("组件预览")
            AbkTextField(value = name, onValueChange = { name = it }, label = "名称")
            AbkButton(onClick = ::save, style = AbkButtonStyle.Primary) { Text("保存") }
        }
    }
}
```

Pass `colorSource = AbkColorSource.Seed` (optionally with `seed = ...`) to use the deterministic Tonal Spot palette instead of the platform's dynamic color. Host state owns the values; the library never persists preferences.

## Theme and resources

`AbkTheme` supports system dynamic color on Android 12+ and deterministic Tonal Spot seed colors for comparison with the web palette. It does not persist preferences. Success and warning use fixed semantic seeds; status is not communicated by color alone. The bundled fonts are the same v1.250 family used by the web baseline and are intentionally full TTF files rather than a small sample subset: Regular, Bold, and Mono Regular total about 47.7 MB (45.5 MiB), which becomes part of every APK that packages the library. Source and license records are in `abk-ui/src/main/assets/abk/`.

Lucide vectors are generated from `assets/vendor/icons/icon-data.js`:

```bash
python3 scripts/generate-compose-icons.py
node --check assets/vendor/icons/icon-data.js
```

The generator is the single source for the native vector geometry; do not hand-edit `AbkIcons.kt`. `info-mark` intentionally includes the information dot and directional vectors are auto-mirrored for RTL.

## Web preview

For working-tree web changes, open `index.html` directly. For a committed snapshot, export Git into a temporary directory before serving; do not serve the repository root. See [CLAUDE.md](CLAUDE.md) for the exact command. The web side is now Lucide-only and keeps text fallbacks when its optional icon renderer is absent.

## Current verification boundary

What has actually run:

- **Host tests** — 20 JUnit/Robolectric tests pass in the SDK-free host fixture (`AbkComponentsTest` 15, `AbkPaletteTest` 2 including 512 golden palette vectors, `AbkSnackbarTest` 3), executed with the Java 21 test JVM against resolved Compose artifacts. They cover component semantics and touch targets, the equal-weight navigation, controlled field/select/switch behavior, overlay dismissal, snackbar queueing, spacing/shape tokens, the icon registry and its RTL mirroring, and the old-Android seed-color fallback role by role.
- **Sources** — `:abk-ui`, `:catalog`, and the test sources compile with the Compose compiler in that same fixture.
- **Icons** — `scripts/generate-compose-icons.py` regenerates all 30 vectors reproducibly; `node --check` passes for the web scripts.
- **Web** — an Edge/Playwright regression passes: theme switching, 57 icon hosts, accordion, dialog/sheet/menu open-close with focus return, toast undo in both themes on the raised surface, equal-weight bottom navigation at 1440/390/320, no horizontal overflow, and the icon-script fallback path.

What has **not** run: Gradle Android packaging (`assembleRelease`, `assembleDebug`), Lint, and everything that depends on them. They are blocked until the SDK licenses for `build-tools;35.0.0` and `platforms;android-36` are reviewed and accepted by the user's own installation. The SDK-free fixture does not package Android resources or produce an AAR/APK. No emulator or device is attached, so native rendering, installation, IME, TalkBack, and pixel acceptance remain outstanding. Do not describe those as passed.
