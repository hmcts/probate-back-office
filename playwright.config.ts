import { CommonConfig, ProjectsConfig } from "@hmcts/playwright-common";
import { defineConfig } from "@playwright/test";

/**
 * @see https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
  timeout: 600000,
  testDir: "./src/test/PlaywrightTest",
  ...CommonConfig.recommended,
    expect: {
        timeout: 30000, // for all expect() assertions
    },

    use: {
        // Navigation timeout (affects goto, waitForLoadState, etc.)
        navigationTimeout: 60000,

        // Action timeout (affects click, fill, etc.)
        actionTimeout: 20000,
        // headless: false, // Run with visible browser
        screenshot: 'only-on-failure',
        video: 'retain-on-failure',
        trace: 'on-first-retry',
    },

  projects: [
    /*{
      ...ProjectsConfig.chrome,
    },*/
    {
      ...ProjectsConfig.chromium,
    },
    {
      ...ProjectsConfig.edge,
    },
    /*{
      ...ProjectsConfig.firefox,
    },
    {
      ...ProjectsConfig.webkit,
    },
    {
      ...ProjectsConfig.tabletChrome,
    },
    {
      ...ProjectsConfig.tabletWebkit,
    },*/
  ],
});
