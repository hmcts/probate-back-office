const { expect } = require('@playwright/test');
const { testConfig } = require ('../../Configs/config');
const { BasePage } = require ('../utility/basePage');
const newCaseConfig = require('./newCaseConfig');
const createCaseConfig = require("../createCase/createCaseConfig.json");

exports.CreateCasePage = class CreateCasePage extends BasePage {
     constructor(page) {
        super(page);
        this.page = page;
        this.createCasePageLocator = page.getByRole('link', { name: newCaseConfig.waitForText});
        this.createCaseLocator = page.getByRole('link', { name: newCaseConfig.xuiCreateCaseLocator});
        this.jurisdictionLocator = page.getByLabel(newCaseConfig.jurisdictionLocatorName);
        this.caseTypeLocator = page.getByLabel(newCaseConfig.caseTypeLocatorName);
        this.eventLocator = page.getByLabel(newCaseConfig.eventLocatorName);
        this.startButtonLocator = page.getByRole('button', {name: newCaseConfig.startButton});
    }

    async selectNewCase() {
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.createCasePageLocator).toBeVisible();
        await this.rejectCookies();
        await expect(this.createCaseLocator).toBeEnabled();
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await this.createCaseLocator.click();
    }

    async selectCaseTypeOptions(caseType, event){
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.createCaseLocator).toBeVisible();
        await expect(this.jurisdictionLocator).toBeEnabled();
        await this.jurisdictionLocator.selectOption({value: newCaseConfig.jurisdictionValue});
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.caseTypeLocator).toBeEnabled();
        await this.caseTypeLocator.selectOption({value: caseType});
        await expect(this.page.getByRole('option', {name: caseType}).first()).toBeHidden();
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.eventLocator).toBeEnabled();
        await this.eventLocator.selectOption({label: event});
        await expect(this.page.getByRole('option', {name: event})).toBeHidden();
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
        await expect(this.startButtonLocator).toBeEnabled();
        await this.startButtonLocator.click();
        await this.page.waitForTimeout(testConfig.CreateCaseDelay);
    }
};
