# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Preview and verification

ABK UI is a static HTML/CSS/JavaScript prototype. There is no dependency install, build, configured linter, or automated test suite (including single-test commands).

- For working-tree changes, open `index.html` directly in a browser. If Node is available, run `node --check` on `assets/js/abk-ui.js` or `assets/js/demo-palette.js` to check syntax; there is no project test runner.
- For a localhost preview of the **committed** snapshot, run this from the repository root in Bash. Open `http://127.0.0.1:8080` and press Ctrl+C to stop the server and clean up. Do not serve the repository root.

```bash
(
  set -o pipefail
  preview=$(mktemp -d) || exit 1
  trap 'rm -rf -- "$preview"' EXIT
  git archive HEAD | tar -xf - -C "$preview" || exit 1
  python3 -I -m http.server 8080 --bind 127.0.0.1 --directory "$preview"
)
```

Start visual checks at `index.html#screens` (构建 and 模块), then check individual component states. Check light and dark themes at desktop and narrow widths. LXGW WenKai is loaded from unpkg by `assets/css/fonts.css`, so font availability can affect layout. See [HANDOFF.md](HANDOFF.md) for known prototype gaps and browser acceptance checks.

## Architecture

- `index.html` is both the component catalog and the host for the two composed App examples. Those examples use static sample data, not a build service. `assets/css/demo.css` styles the catalog and mock screens; reusable component styles live in the other CSS files.
- `assets/css/tokens.css` defines shared semantic roles and their `data-theme` values, plus typography, spacing, radius, and motion. `base.css` supplies layout and accessibility primitives; `components.css`, `forms.css`, `overlays.css`, and `navigation.css` build on those roles. Make broad visual changes in tokens first, and keep `demo.css` last in the stylesheet order.
- `assets/js/abk-ui.js` binds documented `data-abk-*` attributes and exposes the same behavior through `window.ABK`. The inline script in `index.html` applies the initial theme before rendering; `ABK.theme` handles changes and persists them at `abk-ui.theme` in `localStorage`.
- `assets/js/demo-palette.js` maps the locally bundled Material Color Utilities Tonal Spot roles to ABK tokens for both themes. `tokens.css` contains the default seed's fallback values; changing the demo seed adds a generated stylesheet without changing the theme preference. Keep the default tokens and mapping in sync; vendoring instructions are in `assets/vendor/material-color-utilities/README.md`.
- `assets/js/abk-icons.js` renders semantic icon roles from the pinned local SVG subset in `assets/vendor/icons/icon-data.js`; `demo-icons.js` synchronizes the catalog and screen-example pickers. Both are optional to `abk-ui.js`, which retains text fallbacks. The four families are comparison choices, not a final selection; preserve the load order documented in `index.html` and keep `sources.json`, licenses, and generated data aligned.
- Kotlin/Compose work has not started. Once the web design is final, use the catalog as the migration inventory, not just the two composed examples. See [README.md](README.md#移植到-kotlin) for platform mappings and [HANDOFF.md](HANDOFF.md) for the migration order.
