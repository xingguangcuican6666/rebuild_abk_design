# Material Color Utilities

`material-color-utilities.js` is a bundled subset of Google's [Material Color Utilities](https://github.com/material-foundation/material-color-utilities), npm version [`0.4.0`](https://www.npmjs.com/package/@material/material-color-utilities/v/0.4.0), distributed under [Apache-2.0](LICENSE). `entry.mjs` lists the exports used by ABK. The classic script exposes `ABKMaterial` so the demo also works when opened via `file://`.

The bundle is checked in. Previewing or editing the demo does not require npm. To regenerate it, run from the repo root with Bash and npm:

```bash
(
  set -e
  work=$(mktemp -d)
  trap 'rm -rf -- "$work"' EXIT
  npm install --prefix "$work" --no-save --no-package-lock \
    esbuild@0.28.2 @material/material-color-utilities@0.4.0
  dest=assets/vendor/material-color-utilities
  NODE_PATH="$work/node_modules" "$work/node_modules/.bin/esbuild" "$dest/entry.mjs" \
    --bundle --format=iife --global-name=ABKMaterial --target=es2020 --minify \
    --legal-comments=eof \
    --banner:js='/* Generated from @material/material-color-utilities v0.4.0 via esbuild. See LICENSE and README.md. */' \
    --outfile="$dest/material-color-utilities.js"
  cp "$work/node_modules/@material/material-color-utilities/LICENSE" "$dest/LICENSE"
)
```
