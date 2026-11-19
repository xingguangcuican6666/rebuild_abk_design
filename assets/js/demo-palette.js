/* ABK UI · Material You 动态配色 */
(() => {
  "use strict";

  function createSheet(material, hex) {
    const { argbFromHex, hexFromArgb, Hct, SchemeTonalSpot, TonalPalette } = material;
    const source = Hct.fromInt(argbFromHex(hex));
    // Material 没有成功、警示角色：沿用绿 / 琥珀的语义，按同一组色调配对。
    const success = TonalPalette.fromInt(argbFromHex("#3f7a4e"));
    const warning = TonalPalette.fromInt(argbFromHex("#a86b12"));

    return [false, true].map(dark => {
      const scheme = new SchemeTonalSpot(source, dark, 0, "2021", "phone");
      const roles = {
        bg: scheme.surface,
        "bg-raised": dark ? scheme.surfaceContainer : scheme.surfaceContainerLowest,
        "bg-sunken": dark ? scheme.surfaceContainerLowest : scheme.surfaceContainer,
        "bg-input": dark ? scheme.surfaceContainerHigh : scheme.surfaceContainerLow,
        ink: scheme.onSurface,
        "ink-soft": scheme.onSurfaceVariant,
        // outline 用于边框，不用于小字；为次级文字留出对比度余量。
        "ink-muted": scheme.neutralVariantPalette.tone(dark ? 78 : 36),
        "ink-faint": scheme.neutralVariantPalette.tone(dark ? 75 : 40),
        line: scheme.outlineVariant,
        "line-strong": scheme.outline,
        accent: scheme.primary,
        "accent-ink": scheme.onPrimary,
        "accent-soft": scheme.primaryContainer,
        "accent-soft-ink": scheme.onPrimaryContainer,
        "accent-line": scheme.outlineVariant,
        danger: scheme.error,
        "danger-ink": scheme.onError,
        "danger-soft": scheme.errorContainer,
        "danger-soft-ink": scheme.onErrorContainer,
        info: scheme.secondary,
        "info-ink": scheme.onSecondary,
        "info-soft": scheme.secondaryContainer,
        "info-soft-ink": scheme.onSecondaryContainer,
      };
      for (const [name, tones] of Object.entries({ success, warning })) {
        roles[name] = tones.tone(dark ? 80 : 40);
        roles[`${name}-ink`] = tones.tone(dark ? 20 : 100);
        roles[`${name}-soft`] = tones.tone(dark ? 30 : 90);
        roles[`${name}-soft-ink`] = tones.tone(dark ? 90 : 10);
      }

      const declarations = Object.entries(roles).map(([role, argb]) => `  --${role}: ${hexFromArgb(argb)};`);
      declarations.push(`  --scrim: ${hexFromArgb(scheme.scrim)}${dark ? "94" : "6b"};`);
      return `:root:where([data-theme="${dark ? "dark" : "light"}"]) {\n${declarations.join("\n")}\n}`;
    }).join("\n\n");
  }

  // 维护 tokens.css 的默认值时，复用这里的角色映射。
  if (typeof module === "object" && module.exports) {
    module.exports = createSheet;
    return;
  }

  const seed = document.getElementById("demo-palette-seed");
  if (!seed) return;
  const status = document.getElementById("demo-palette-status");
  if (!window.ABKMaterial) {
    status.textContent = "种子颜色暂不可更改，当前使用默认配色。";
    return;
  }

  const generated = document.createElement("style");
  document.head.append(generated);
  let appliedSeed = seed.defaultValue;

  function applySeed() {
    if (seed.value === appliedSeed) return;
    try {
      // 默认种子用 tokens.css；只有换种子时才需要生成颜色覆盖。
      generated.textContent = seed.value === seed.defaultValue ? "" : createSheet(window.ABKMaterial, seed.value);
      appliedSeed = seed.value;
      status.textContent = "";
    } catch (error) {
      seed.value = appliedSeed;
      status.textContent = "无法应用这个种子颜色，已保留当前配色。";
      console.warn("ABK UI: Could not generate the Material palette.", error);
    }
  }

  seed.addEventListener("input", applySeed);
  seed.addEventListener("change", applySeed);
  seed.disabled = false;
  applySeed();
})();
