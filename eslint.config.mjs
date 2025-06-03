import { LintingConfig } from "@hmcts/playwright-common";
import tseslint from "typescript-eslint";

// If path to tests is different, directly overwride the path:
// const tsConfig = LintingConfig.tseslintPlugin;
// const pwConfig = LintingConfig.playwright;
// tsConfig.files = ["tests/**/*.ts"];
// pwConfig.files = ["tests/**/*.ts"];
// tseslint.config(..., config, ...)

export default tseslint.config(
  {
    ...LintingConfig.tseslintRecommended,
    files: ["src/test/PlaywrightTest/**/*.ts"],
  },
  LintingConfig.ignored,
  {
    ...LintingConfig.tseslintPlugin,
    files: ["src/test/PlaywrightTest/**/*.ts"],
  },
  { ...LintingConfig.playwright, files: ["src/test/PlaywrightTest/**/*.ts"] }
);
