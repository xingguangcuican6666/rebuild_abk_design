# ABK UI Compose coverage

The Android library lives in `abk-ui`; `catalog` is a host app and keeps sample state out of the library. The table records source coverage and current verification state rather than treating file creation as visual acceptance. Statuses say *host-tested* only where a named test in `abk-ui/src/test` exercises the API; everything else compiles in the SDK-free check and is rendered only by the catalog screens (`CatalogSections.kt`, `ComponentPreview.kt`, `ExampleScreens.kt`), which no test or device run has yet exercised.

| Web inventory | Compose API | Variants and state | Catalog entry | Status |
| --- | --- | --- | --- | --- |
| tokens, base typography | `AbkTheme`, `AbkText`, `AbkHeading`, `AbkEyebrow`, `AbkDivider` | system/seed color, light/dark, font scale, reduced motion | 主题与可访问性 / 排版与布局 | host-tested: seed/dynamic color roles, 512-vector palette, spacing/shape tokens |
| panel, cluster, code, keycap, highlight | `AbkPanel`, `AbkCluster`, `AbkCodeBlock`, `AbkInlineCode`, `AbkKeycap`, `AbkHighlight` | raised/sunken, optional grain, copy feedback | 排版、数据展示 | compiles in the SDK-free Compose check; no host test |
| buttons | `AbkButton`, `AbkIconButton` | primary/secondary/soft/outline/ghost/danger/sketch; three sizes; disabled/loading | 按钮 | host-tested: loading/disabled ignore touch while enabled fires once |
| fields | `AbkField`, `AbkTextField`, `AbkSelect` | hint/error/optional, prefix/suffix, multiline, read-only, disabled, option disabled | 表单与选择 | host-tested: external label, error state, read-only, disabled option, controlled selection |
| choice controls | `AbkCheckbox`, `AbkRadioGroup`, `AbkSwitch`, `AbkSettingSwitch`, `AbkSlider`, `AbkSegmented` | tri-state, card radio, two switch sizes, value/steps, 2–4 segments | 表单与选择 | host-tested: checkbox/radio/segmented call back once; disabled option does not select |
| navigation | `AbkScaffold`, `AbkTopBar`, `AbkHero`, `AbkTabs`, `AbkBottomNavigation`, `AbkNavigationDrawer`, `AbkBreadcrumb` | insets, subtitle/actions, fixed/scrollable tabs, equal-weight 2–5 bottom destinations, side navigation (modal drawer or permanent rail) | 导航; build/modules screens | host-tested: equal widths, selected semantics, RTL + 2× font scale |
| cards and lists | `AbkCard`, `AbkList`, `AbkListItem`, `AbkAccordion`, `AbkAvatar` | default/flat/sunken/clickable, media/header/footer, open/closed, online status | 卡片、列表与标签 | host-tested (accordion open/closed); other cards compile-only |
| badges and chips | `AbkBadge`, `AbkChip`, `AbkRemovableChip` | neutral/accent/success/warning/danger/info, dot/plain, selected/disabled/removable | 卡片、列表与标签 | compiles in the SDK-free Compose check; no host test |
| data display | `AbkStat`, `AbkKeyValue`, `AbkTable`, `AbkCodeBlock` | delta tones, horizontal table with vertically scrolling body and fixed header | 数据展示 | compiles in the SDK-free Compose check; no host test |
| steps/history | `AbkSteps`, `AbkTimeline` | horizontal/vertical; complete/current/pending; status tones | 步骤与时间线 | compiles in the SDK-free Compose check; no host test |
| alerts/progress/loading | `AbkAlert`, `AbkLinearProgress`, `AbkRingProgress`, `AbkSpinner`, `AbkLoadingDots`, `AbkSkeleton`, `AbkEmptyState` | four alert tones, determinate/indeterminate/striped, reduced-motion static decoration, empty actions | 反馈与加载 | compiles in the SDK-free Compose check; no host test |
| overlays | `AbkDialog`, `AbkBottomSheet`, `AbkMenu`, `AbkTooltip` | normal/wide, dismiss/back/outside, action/label/divider/disabled/danger menu, tooltip | 浮层与 Toast | host-tested: menu dismiss-before-action and disabled entry, dialog close-then-feedback |
| toast | `AbkSnackbarHostState`, `rememberAbkSnackbarHostState`, `AbkSnackbarHost` | queued `SnackbarHostState`, four tones, action/dismiss/undo result | 浮层与 Toast; build/modules | host-tested: queued SnackbarHostState, tones, action/dismiss results |
| icon registry | generated `AbkIcons`, `AbkIcon` | 30 Lucide semantic roles; directional vectors auto-mirror; decorative vs named content descriptions | Lucide 图标 | host-tested: 30 roles, 24dp vectors, non-empty geometry, RTL mirroring, info-mark path |

## Verification limits

- `python3 scripts/generate-compose-icons.py` is the reproducible icon generation check.
- `node --check` and `git diff --check` cover the web-side syntax/whitespace changes.
- A local SDK-free Kotlin compiler check was run against the resolved Compose 1.9.5 / Material3 1.4.0 and Kotlin 2.2.21 artifacts. It checks source signatures and Compose compiler lowering, but not Android resource packaging.
- 20 host tests pass in that same SDK-free fixture on the Java 21 test JVM: `AbkComponentsTest` (15), `AbkPaletteTest` (2, one of them 512 golden palette vectors), `AbkSnackbarTest` (3). They cover touch targets and semantics, controlled inputs, overlay dismissal, snackbar queueing, token scales, the icon registry, and the old-Android seed fallback.
- The Gradle Wrapper is Gradle 8.13 with its distribution checksum verified. Full Gradle tasks — `assembleRelease`, `assembleDebug`, both Lint tasks — remain blocked until a user-reviewed Android SDK license is accepted and `platforms;android-36` plus build tools are installed. The SDK-free fixture produces no AAR or APK.
- No emulator/device is connected. Real rendering, APK installation, TalkBack, IME, and pixel-level light/dark/narrow-window acceptance remain open.
