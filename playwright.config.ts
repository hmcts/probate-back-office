import { CommonConfig, ProjectsConfig } from "@hmcts/playwright-common";
import { defineConfig, devices } from "@playwright/test";

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

    reporter: [
        ['html', { outputFolder: './functional-output/reports', open: 'never' }],
        ['json', { outputFile: './functional-output/results.json' }],
        ['list'], // Console output
    ],

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
        outputDir: './test-results/FullFunctionalTests',
    },
    {
      ...ProjectsConfig.edge,
        outputDir: './test-results/edge',
        grep: /@edge/,
    },
    {
      ...ProjectsConfig.firefox,
        outputDir: './test-results/firefox',
        grep: /@firefox/,
    },
    {
        ...ProjectsConfig.webkit,
        outputDir: './test-results/webkit',
        grep: /@webkit/,
    },
    {
        name: 'galaxyS4',
        outputDir: './test-results/galaxyS4',
        use: { ...devices['Galaxy S4'] },
        grep: /@galaxys4/,
    },
    {
        name: 'iPadPro11',
        outputDir: './test-results/ipadpro11',
        use: { ...devices['iPad Pro 11'] },
        grep: /@ipadpro11/,
    },
  ],
});
