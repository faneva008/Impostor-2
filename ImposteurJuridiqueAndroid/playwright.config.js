const { defineConfig } = require('@playwright/test');
module.exports = defineConfig({
  testDir: './tests',
  use: { headless: true, viewport: { width: 360, height: 720 } },
  reporter: 'list', workers: 2
});
