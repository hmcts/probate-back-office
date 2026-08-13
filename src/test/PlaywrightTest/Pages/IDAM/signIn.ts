import { expect } from "@playwright/test";
import { testConfig } from "../../Configs/config.ts";
import { BasePage } from "../utility/basePage.ts";

export class SignInPage extends BasePage {
  // this.signinPageLocator = page.getByLabel('Sign in');
  readonly usernameLocator = this.page.getByText('Enter your email address');
  readonly passwordLocator = this.page.getByText('Enter your password');
  readonly usernameTextboxLocator = this.page.getByRole("textbox", {
    name: "Enter your email address",
  });
  readonly passwordTextboxLocator = this.page.getByRole("textbox", {
    name: "Enter your password",
  });
  readonly submitButtonLocator = this.page.getByRole("button", {
    name: "Sign in",
  });
  readonly continueButtonLocator = this.page.getByRole("button", {
    name: "Continue",
  })

  constructor(page) {
    super(page);
  }
  async authenticateWithIdamIfAvailable(
    useProfessionalUser,
    signInDelay = testConfig.SignInDelayDefault,
  ) {
    await this.page.goto(`${testConfig.TestBackOfficeUrl}/`, {
      waitUntil: "load",
      timeout: 60000,
    });
    // await this.page.waitForTimeout(testConfig.ManualDelayLong);
    await this.verifyPageLoad(this.usernameLocator, 10_000);
    await expect(this.usernameLocator).toBeVisible();
    if (useProfessionalUser === "superUser") {
      await this.usernameTextboxLocator.fill(testConfig.TestEnvSuperCwUser);
    } else {
      await this.usernameTextboxLocator.fill(
        useProfessionalUser
          ? testConfig.TestEnvProfUser
          : testConfig.TestEnvCwUser,
      );
      await this.continueButtonLocator.click();
      await expect(this.passwordLocator).toBeVisible();

      await this.passwordTextboxLocator.fill(
        useProfessionalUser
          ? testConfig.TestEnvProfPassword
          : testConfig.TestEnvCwPassword,
      );
    }
    // await this.page.waitForSelector(this.submitButtonLocator, signInDelay);
    // await expect(this.submitButtonLocator).toBeEnabled();
    // await this.submitButtonLocator.click();
    await this.waitForNavigationToComplete(this.continueButtonLocator);
    // await this.page.waitForTimeout(signInDelay);
    // await this.page.waitForLoadState('domcontentloaded');
    await expect(this.passwordLocator).toBeHidden();
    await this.rejectCookies();
    await this.page.waitForTimeout(signInDelay);
  }

  async signOut() {
    await this.verifyPageLoad(
      this.page.locator("nav.hmcts-header__navigation ul li:last-child a"),
    );
    await this.waitForNavigationToComplete(
      "nav.hmcts-header__navigation ul li:last-child a",
      10_000,
    );
    await this.verifyPageLoad(this.usernameLocator, 10_000);
    await expect(this.usernameLocator).toBeVisible();
  }

  async authenticateUserNoc(
    useProfessionalUser,
    signInDelay = testConfig.SignInDelayDefault,
  ) {
    await this.page.goto(`${testConfig.TestBackOfficeUrl}/`);
    // await this.page.waitForTimeout(testConfig.ManualDelayMedium);
    await expect(
      this.page.getByRole("heading", {
        name: "Sign in",
        exact: true,
      }),
    ).toBeVisible();
    await expect(this.usernameLocator).toBeVisible();
    await expect(this.passwordLocator).toBeVisible();
    await this.page
      .locator("#username")
      .fill(
        useProfessionalUser
          ? testConfig.TestEnvProfUser
          : testConfig.TestEnvProfUserNoc,
      );
    await this.page
      .locator("#password")
      .fill(
        useProfessionalUser
          ? testConfig.TestEnvProfPassword
          : testConfig.TestEnvProfPasswordNoc,
      );
    await expect(this.submitButtonLocator).toBeEnabled();
    await this.submitButtonLocator.click();

    await expect(this.usernameLocator).toBeHidden();
    await this.rejectCookies();
    await this.page.waitForTimeout(signInDelay);
  }

  async authenticateUserShareCase(
    useProfessionalUser,
    signInDelay = testConfig.SignInDelayDefault,
  ) {
    await this.page.goto(`${testConfig.TestBackOfficeUrl}/`);
    // await this.page.waitForTimeout(testConfig.ManualDelayMedium);
    await expect(
      this.page.getByRole("heading", {
        name: "Sign in",
        exact: true,
      }),
    ).toBeVisible();
    await expect(this.usernameLocator).toBeVisible();
    await expect(this.passwordLocator).toBeVisible();
    await this.page
      .locator("#username")
      .fill(
        useProfessionalUser
          ? testConfig.TestEnvProfUser
          : testConfig.TestEnvProfUserSAC,
      );
    await this.page
      .locator("#password")
      .fill(
        useProfessionalUser
          ? testConfig.TestEnvProfPassword
          : testConfig.TestEnvProfPasswordSAC,
      );
    await expect(this.submitButtonLocator).toBeEnabled();
    await this.submitButtonLocator.click();

    await expect(this.usernameLocator).toBeHidden();
    await this.rejectCookies();
    await this.page.waitForTimeout(signInDelay);
  }

  async authenticateUserWorkAllocation(
    jobRole: string,
    signInDelay = testConfig.SignInDelayDefault,
  ) {
    type JobRole =
      | "Senior Legal Caseworker"
      | "Legal Caseworker"
      | "CTSC Team Leader"
      | "CTSC Administrator";

    const USER_CREDENTIALS: Record<
      JobRole,
      { email?: string; password?: string }
    > = {
      "Senior Legal Caseworker": {
        email: process.env.WA_CTSC_SENIOR_USER_EMAIL,
        password: process.env.WA_CTSC_SENIOR_USER_PASSWORD,
      },
      "Legal Caseworker": {
        email: process.env.WA_CTSC_USER_EMAIL,
        password: process.env.WA_CTSC_USER_PASSWORD,
      },
      "CTSC Team Leader": {
        email: process.env.WA_CTSC_TEAM_LEADER_EMAIL,
        password: process.env.WA_CTSC_TEAM_LEADER_PASSWORD,
      },
      "CTSC Administrator": {
        email: process.env.WA_CTSC_ADMIN_USER_EMAIL,
        password: process.env.WA_CTSC_ADMIN_USER_PASSWORD,
      },
    };

    const credentials = USER_CREDENTIALS[jobRole as JobRole];

    if (!credentials.email || !credentials.password) {
      throw new Error(
        `Missing environment credentials for role: ${jobRole}. jobRole must be one of: ${Object.keys(USER_CREDENTIALS).join(", ")}`,
      );
    }
    await this.page.goto(`${testConfig.TestBackOfficeUrl}/`);
    // await this.page.waitForTimeout(testConfig.ManualDelayMedium);
    await expect(
      this.page.getByRole("heading", {
        name: "Sign in",
        exact: true,
      }),
    ).toBeVisible();
    await expect(this.usernameLocator).toBeVisible();
    await expect(this.passwordLocator).toBeVisible();
    await this.page
      .locator("#username")
      .fill(credentials.email);
    await this.page
      .locator("#password")
      .fill(credentials.password);
    await expect(this.submitButtonLocator).toBeEnabled();
    await this.submitButtonLocator.click();

    await expect(this.usernameLocator).toBeHidden();
    await this.rejectCookies();
    await this.page.waitForTimeout(signInDelay);
  }
}
