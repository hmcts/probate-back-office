import { CommonConfig, ProjectsConfig } from "@hmcts/playwright-common";
import { defineConfig, devices } from "@playwright/test";
import dotenv from "dotenv";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const envFileName = process.env.TEST_ENV ? `.env.${process.env.TEST_ENV}` : ".env";
const envFilePath = path.resolve(__dirname, "src/test/PlaywrightTest", envFileName);

dotenv.config({ path: envFilePath, override: true });

console.log(`Loading env file: ${envFilePath}`);
console.log(`IDAM_API_URL present: ${process.env.IDAM_API_URL ? "yes" : "no"}`);
console.log(`CW_USER_EMAIL present: ${process.env.CW_USER_EMAIL ? "yes" : "no"}`);

const browserName = process.env.BROWSER_NAME || "default";

export default defineConfig({
    timeout: 600000,
    testDir: "./src/test/PlaywrightTest",
    ...CommonConfig.recommended,
    expect: {
        timeout: 60000, // for all expect() assertions
    },
    reporter: [
        ["html", { outputFolder: `./functional-output/reports/${browserName}`, open: "never" }],
        ["json", { outputFile: "./functional-output/results.json" }],
        ["list"],
    ],
    use: {
        navigationTimeout: 60000,
        actionTimeout: 20000,
        screenshot: "only-on-failure",
        video: "retain-on-failure",
        trace: "on-first-retry",
    },
    projects: [
        {
            ...ProjectsConfig.chromium,
            outputDir: "./test-results/FullFunctionalTests",
        },
        {
            ...ProjectsConfig.edge,
            outputDir: "./test-results/edge",
            grep: /@edge/,
        },
        {
            ...ProjectsConfig.firefox,
            outputDir: "./test-results/firefox",
            grep: /@firefox/,
        },
        {
            ...ProjectsConfig.webkit,
            outputDir: "./test-results/webkit",
            grep: /@webkit/,
        },
        {
            name: "galaxyS4",
            outputDir: "./test-results/galaxyS4",
            use: { ...devices["Galaxy S4"] },
            grep: /@galaxys4/,
        },
        {
            name: "iPadPro11",
            outputDir: "./test-results/ipadpro11",
            use: { ...devices["iPad Pro 11"] },
            grep: /@ipadpro11/,
        },
    ],
});
