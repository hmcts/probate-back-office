import { test as setup, expect } from "@playwright/test";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import { testConfig } from "../Configs/config.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const caseworkerAuthFile = path.resolve(
  __dirname,
  "../../../../playwright/.auth/caseworker.json"
);

const solicitorAuthFile = path.resolve(
  __dirname,
  "../../../../playwright/.auth/solicitor.json"
);

async function loginAndSaveState(
  page,
  username,
  password,
  startUrl,
  authFile
) {
  fs.mkdirSync(path.dirname(authFile), { recursive: true });

  await page.goto(startUrl, { waitUntil: "domcontentloaded" });

  await expect(page).toHaveURL(/login|sign-in|idam|manage-case|probate/i, {
    timeout: 30000,
  });

  const usernameInput = page
    .getByRole("textbox", { name: /email|username/i })
    .or(page.locator('input[name="username"]'))
    .or(page.locator('input[name="email"]'))
    .first();

  const passwordInput = page
    .getByLabel(/password/i)
    .or(page.locator('input[name="password"]'))
    .first();

  await expect(usernameInput).toBeVisible({ timeout: 30000 });
  await expect(passwordInput).toBeVisible({ timeout: 30000 });

  await usernameInput.fill(username);
  await passwordInput.fill(password);

  const signInButton = page
    .getByRole("button", { name: /sign in|log in|continue/i })
    .or(page.locator('input[type="submit"]'))
    .or(page.locator('button[type="submit"]'))
    .first();

  await expect(signInButton).toBeVisible({ timeout: 30000 });
  await signInButton.click();

  await expect(page).toHaveURL(/manage-case|probate|cases/i, {
    timeout: 60000,
  });

  await expect(page).not.toHaveURL(/login|sign-in/i);

  await page.context().storageState({ path: authFile });
}

setup("authenticate caseworker", async ({ page }) => {
  if (!testConfig.TestEnvCwUser || !testConfig.TestEnvCwPassword) {
    throw new Error(
      "Caseworker credentials are missing. Check CW_USER_EMAIL and CW_USER_PASSWORD."
    );
  }

  await loginAndSaveState(
    page,
    testConfig.TestEnvCwUser,
    testConfig.TestEnvCwPassword,
    testConfig.TestBackOfficeUrl,
    caseworkerAuthFile
  );
});

setup("authenticate solicitor", async ({ page }) => {
  if (!testConfig.TestEnvSolUser || !testConfig.TestEnvSolPassword) {
    throw new Error(
      "Solicitor credentials are missing. Check SOL_USER_EMAIL and SOL_USER_PASSWORD."
    );
  }

  await loginAndSaveState(
    page,
    testConfig.TestEnvSolUser,
    testConfig.TestEnvSolPassword,
    testConfig.TestBackOfficeUrl,
    solicitorAuthFile
  );
});
