const {expect} = require('@playwright/test');
const {testConfig} = require ('../../Configs/config');
const {BasePage} = require('../utility/basePage');

exports.SignInPage = class SignInPage extends BasePage {
    constructor(page) {
        super(page);
        this.page = page;
        // this.signinPageLocator = page.getByLabel('Sign in');
        this.usernameLocator = this.page.getByText('Email address');
        this.passwordLocator = this.page.getByText('Password', {exact: true});
        this.submitButtonLocator = page.getByRole("button", {name: 'Sign in'});
    }
    async authenticateWithIdamIfAvailable (useProfessionalUser, signInDelay = testConfig.SignInDelayDefault) {
        await this.page.goto(`${testConfig.TestBackOfficeUrl}/`);
        await this.page.waitForTimeout(testConfig.ManualDelayMedium);
        await expect(this.page.getByRole('heading', {name: 'Sign in', exact: true}, {timeout: 6000})).toBeVisible();
        await expect(this.usernameLocator).toBeVisible();
        await expect(this.passwordLocator).toBeVisible();
        await this.page.locator('#username').fill(useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvCwUser);
        await this.page.locator('#password').fill(useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvCwPassword);
        //await this.page.waitForSelector(this.submitButtonLocator, signInDelay);
        await expect(this.submitButtonLocator).toBeEnabled();
        await this.submitButtonLocator.click();

        await expect(this.usernameLocator).not.toBeVisible();
        await this.rejectCookies();
        await this.page.waitForTimeout(signInDelay);
    }

    async signOut(delay = testConfig.SignOutDelayDefault) {
        await this.waitForSignOutNavigationToComplete('nav.hmcts-header__navigation ul li:last-child a', delay);
        await expect(this.usernameLocator).toBeVisible();
    }
};
