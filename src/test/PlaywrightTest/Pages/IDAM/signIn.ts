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
    await this.page.goto(`${testConfig.TestBackOfficeUrl}/`, {
      waitUntil: 'load',
      timeout: 60000
    });
    // await this.page.waitForTimeout(testConfig.ManualDelayLong);
    await this.verifyPageLoad(this.usernameLocator, 10_000);
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
    // await this.page.waitForSelector(this.submitButtonLocator, signInDelay);
    // await expect(this.submitButtonLocator).toBeEnabled();
    // await this.submitButtonLocator.click();
    await this.waitForNavigationToComplete(this.submitButtonLocator);
    // await this.page.waitForTimeout(signInDelay);
    // await this.page.waitForLoadState('domcontentloaded');
    await expect(this.usernameLocator).toBeHidden();
    await this.rejectCookies();
    await this.page.waitForTimeout(signInDelay);
  }

  async signOut() {
    await this.verifyPageLoad(this.page.locator('nav.hmcts-header__navigation ul li:last-child a'));
    await this.waitForNavigationToComplete('nav.hmcts-header__navigation ul li:last-child a', 10_000);
    await this.verifyPageLoad(this.usernameLocator, 10_000);
    await expect(this.usernameLocator).toBeVisible();
  }

  async authenticateUserNoc(useProfessionalUser, signInDelay = testConfig.SignInDelayDefault) {
    await this.page.goto(`${testConfig.TestBackOfficeUrl}/`);
    // await this.page.waitForTimeout(testConfig.ManualDelayMedium);
    await expect(
      this.page.getByRole("heading", {
        name: "Sign in",
        exact: true,
      })
    ).toBeVisible();
    await expect(this.usernameLocator).toBeVisible();
    await expect(this.passwordLocator).toBeVisible();
    await this.page.locator('#username').fill(useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvProfUserNoc);
    await this.page.locator('#password').fill(useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvProfPasswordNoc);
    await expect(this.submitButtonLocator).toBeEnabled();
    await this.submitButtonLocator.click();

    await expect(this.usernameLocator).toBeHidden();
    await this.rejectCookies();
    await this.page.waitForTimeout(signInDelay);
  }

  async authenticateUserShareCase (useProfessionalUser, signInDelay = testConfig.SignInDelayDefault) {
    await this.page.goto(`${testConfig.TestBackOfficeUrl}/`);
    // await this.page.waitForTimeout(testConfig.ManualDelayMedium);
    await expect(
      this.page.getByRole("heading", {
        name: "Sign in",
        exact: true,
      })
    ).toBeVisible();
    await expect(this.usernameLocator).toBeVisible();
    await expect(this.passwordLocator).toBeVisible();
    await this.page.locator('#username').fill(useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvProfUserSAC);
    await this.page.locator('#password').fill(useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvProfPasswordSAC);
    await expect(this.submitButtonLocator).toBeEnabled();
    await this.submitButtonLocator.click();

    await expect(this.usernameLocator).toBeHidden();
    await this.rejectCookies();
    await this.page.waitForTimeout(signInDelay);
    // I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/`);
    // await I.wait(testConfig.ManualDelayMedium);
    // await I.waitForText('Sign in', 600);
    // await I.waitForText('Email address');
    // await I.waitForText('Password');
    // await I.fillField('#username', useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvProfUserSAC);
    // await I.fillField('#password', useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvProfPasswordSAC);
    // await I.waitForNavigationToComplete('input[type="submit"]', signInDelay);
    // await I.dontSee({css: '#username'});
    // await I.rejectCookies();
    // await I.wait(signInDelay);
  }
}
