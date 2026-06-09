import { LintingConfig } from "@hmcts/playwright-common";
import tseslint from "typescript-eslint";

LintingConfig.ignored.ignores = LintingConfig.ignored.ignores.concat([
  "node_modules/*",
  "govuk/*",
  "public/*",
  "app/assets/javascripts/*.js",
  "coverage",
  "**/*.min.js",
  "target/*",
  "build/*",
  "functional-output/*",
  "**/test/end-to-end/*",
  "**/test/reporter/*",
]);
LintingConfig.tseslintRecommended.files = ["src/test/PlaywrightTest/**/*.ts"];
LintingConfig.tseslintPlugin.files = ["src/test/PlaywrightTest/**/*.ts"];
LintingConfig.playwright.files = ["src/test/PlaywrightTest/**/*.ts"];

export default tseslint.config(
  LintingConfig.ignored,
  LintingConfig.tseslintRecommended,
  LintingConfig.tseslintPlugin,
  LintingConfig.playwright
);
