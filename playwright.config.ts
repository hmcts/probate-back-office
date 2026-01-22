import { CommonConfig, ProjectsConfig } from "@hmcts/playwright-common";
import { defineConfig } from "@playwright/test";

/**
 * @see https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
  timeout: 600000,
  //expect: { timeout: 600000 },
  testDir: "./src/test/PlaywrightTest",
  ...CommonConfig.recommended,
  /*use: {
    headless: false, // Run with visible browser
  },*/

  projects: [
    /*{
      ...ProjectsConfig.chrome,
    },*/
    {
      ...ProjectsConfig.chromium,
    },
    /*{
      ...ProjectsConfig.edge,
    },
    {
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
