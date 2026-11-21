#!/usr/bin/env node
/* Golden vectors from the web palette mapping; no network or npm install needed. */
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const createSheet = require('../assets/js/demo-palette.js');

const root = path.resolve(__dirname, '..');
const target = path.join(root, 'abk-ui/src/test/resources/abk/seed-palettes.tsv');
const context = {};
vm.runInNewContext(fs.readFileSync(path.join(root, 'assets/vendor/material-color-utilities/material-color-utilities.js'), 'utf8'), context);
const seeds = ['#496a78', '#c34a32', '#487743', '#7a46c7', '#ffc400', '#00ccff', '#000001', '#fffffe'];
const lines = ['seed\tdark\trole\targb'];
for (const seed of seeds) {
  const sheet = createSheet(context.ABKMaterial, seed);
  for (const block of sheet.matchAll(/data-theme="(light|dark)"\]\)\s*\{([^}]+)\}/g)) {
    for (const role of block[2].matchAll(/--([a-z-]+):\s*#([a-f\d]{6}(?:[a-f\d]{2})?);/g)) {
      const hex = role[2];
      const argb = (hex.length === 8 ? hex.slice(6) + hex.slice(0, 6) : 'ff' + hex).toUpperCase();
      lines.push(`${seed}\t${block[1] === 'dark'}\t${role[1]}\t${argb}`);
    }
  }
}
if (lines.length !== 1 + seeds.length * 2 * 32) throw new Error(`Unexpected role coverage: ${lines.length - 1}`);
const content = lines.join('\n') + '\n';
if (process.argv.includes('--check')) {
  if (fs.readFileSync(target, 'utf8') !== content) throw new Error('Palette fixtures differ; regenerate and review the mapping.');
  console.log('Verified 8 seeds × 2 themes × 32 web palette roles.');
} else {
  fs.mkdirSync(path.dirname(target), { recursive: true });
  fs.writeFileSync(target, content);
  console.log('Generated 8 seeds × 2 themes × 32 web palette roles.');
}
