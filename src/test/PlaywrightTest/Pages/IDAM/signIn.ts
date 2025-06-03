import { expect } from "@playwright/test";
import { testConfig } from "../../Configs/config.ts";
import { BasePage } from "../utility/basePage.ts";

export class SignInPage extends BasePage {
  // this.signinPageLocator = page.getByLabel('Sign in');
  readonly usernameLocator = this.page.getByText("Email address");
  readonly passwordLocator = this.page.getByText("Password", { exact: true });
  readonly submitButtonLocator = this.page.getByRole("button", {
    name: "Sign in",
  });

  constructor(page) {
    super(page);
  }
  async authenticateWithIdamIfAvailable(
    useProfessionalUser,
    signInDelay = testConfig.SignInDelayDefault
  ) {
    await this.page.goto(`${testConfig.TestBackOfficeUrl}/`);
    await this.page.waitForTimeout(testConfig.ManualDelayMedium);
    await expect(
      this.page.getByRole("heading", {
        name: "Sign in",
        exact: true,
      })
    ).toBeVisible();
    await expect(this.usernameLocator).toBeVisible();
    await expect(this.passwordLocator).toBeVisible();
    if (useProfessionalUser === "superUser") {
      await this.page.locator("#username").fill(testConfig.TestEnvSuperCwUser);
      await this.page
        .locator("#password")
        .fill(testConfig.TestEnvSuperCwPassword);
    } else {
      await this.page
        .locator("#username")
        .fill(
          useProfessionalUser
            ? testConfig.TestEnvProfUser
            : testConfig.TestEnvCwUser
        );
      await this.page
        .locator("#password")
        .fill(
          useProfessionalUser
            ? testConfig.TestEnvProfPassword
            : testConfig.TestEnvCwPassword
        );
    }
    //await this.page.waitForSelector(this.submitButtonLocator, signInDelay);
    await expect(this.submitButtonLocator).toBeEnabled();
    await this.submitButtonLocator.click();

    await expect(this.usernameLocator).not.toBeVisible();
    await this.rejectCookies();
    await this.page.waitForTimeout(signInDelay);
  }

  async signOut(delay = testConfig.SignOutDelayDefault) {
    await this.waitForSignOutNavigationToComplete(
      "nav.hmcts-header__navigation ul li:last-child a"
    );
    await expect(this.usernameLocator).toBeVisible();
  }
}
