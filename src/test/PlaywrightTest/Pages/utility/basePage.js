const {expect} = require('@playwright/test');
const {testConfig} = require ('../../Configs/config');
const {accessibilityPage} = require('../../Accessibility/runner');

exports.BasePage = class BasePage {
    constructor(page) {
        this.page = page;
        this.rejectLocator = page.getByRole('button', {name: 'Reject analytics cookies'});
        this.continueButtonLocator = page.getByRole('button', {name: 'Continue'});
        this.submitButtonLocator = page.getByRole('button', {name: 'Submit'});
        this.goButtonLocator = this.page.getByRole('button', {name: 'Go'});
    }

    async logInfo(scenarioName, log, caseRef) {
        let ret = scenarioName;
        await this.page.waitForTimeout(testConfig.GetCaseRefFromUrlDelay);
        if (log) {
            ret = ret + ' : ' + log;
        }
        if (caseRef) {
            ret = ret + ' : ' + caseRef;
        }
        console.info(ret);
    }

    async rejectCookies () {
        if (testConfig.RejectCookies) {
            try {
                //const rejectLocator = {css: 'button.govuk-button[value="reject"]'};
                const numVisibleCookieBannerEls = await this.rejectLocator.count();
                if (numVisibleCookieBannerEls > 0) {
                    // just reject additional cookies
                    await expect(this.rejectLocator).toBeEnabled();
                    await this.rejectLocator.click();
                    await this.page.waitForTimeout(testConfig.RejectCookieDelay);
                }
            } catch (e) {
                console.error(`error trying to close cookie banner: ${e.message}`);
            }
        }
    }

    async getCaseRefFromUrl() {
        await this.page.waitForTimeout(testConfig.GetCaseRefFromUrlDelay);
        const url = await this.page.url();
        return url
            .replace('#Event%20History', '')
            .replace('#Case%20Progress', '')
            .split('/')
            .pop()
            .match(/.{4}/g)
            .join('-');
    }

    async waitForNavigationToComplete() {
        const navigationPromise = this.page.waitForNavigation();
        await expect(this.continueButtonLocator).toBeVisible();
        await expect(this.continueButtonLocator).toBeEnabled();
        this.continueButtonLocator.click();
        await navigationPromise;
    }

    async waitForSubmitNavigationToComplete() {
        const navigationPromise = this.page.waitForNavigation();
        await expect(this.submitButtonLocator).toBeVisible();
        await expect(this.submitButtonLocator).toBeEnabled();
        this.submitButtonLocator.click();
        await navigationPromise;
    }

    async waitForGoNavigationToComplete() {
        const navigationPromise = this.page.waitForNavigation();
        await expect(this.goButtonLocator).toBeVisible();
        await expect(this.goButtonLocator).toBeEnabled();
        this.goButtonLocator.click();
        await navigationPromise;
    }

    async waitForSignOutNavigationToComplete(signOutLocator) {
        const navigationPromise = this.page.waitForNavigation();
        await expect(this.page.locator(`${signOutLocator}`)).toBeVisible();
        await expect(this.page.locator(`${signOutLocator}`)).toBeEnabled();
        this.page.locator(`${signOutLocator}`).click();
        await navigationPromise;
    }

    async seeCaseDetails(caseRef, tabConfigFile, dataConfigFile, nextStep, endState, delay = testConfig.CaseDetailsDelayDefault) {
        if (tabConfigFile.tabName) {
            await expect(this.page.locator(`//div[contains(text(),"${tabConfigFile.tabName}")]`)).toBeEnabled();
        }

        await expect(this.page.getByRole('heading', {name: caseRef})).toBeVisible();
        await this.page.getByRole('tab', {name: tabConfigFile.tabName}).focus();
        await this.page.getByRole('tab', {name: tabConfigFile.tabName}).click();
        await this.page.waitForTimeout(delay);

        // *****Need to comment this until accessibility script is completed*****/
        // await this.page.runAccessibilityTest();

        if (tabConfigFile.waitForText) {
            this.tabLocator = this.page.getByText(tabConfigFile.waitForText);
            await expect(this.tabLocator).toBeVisible();
        }

        /* eslint-disable no-await-in-loop */
        for (let i = 0; i < tabConfigFile.fields.length; i++) {
            if (tabConfigFile.fields[i] && tabConfigFile.fields[i] !== '') {
                const textCount = this.page.getByText(tabConfigFile.fields[i]);
                if (textCount > 1) {
                    if (tabConfigFile.fields[i] === 'Caveat not matched') {
                        await expect(this.page.getByText(tabConfigFile.fields[i]).nth(2)).toBeVisible();
                    }
                    await expect(this.page.getByText(tabConfigFile.fields[i], {exact: true})).toBeVisible();
                } else if (tabConfigFile.tabName === 'Event History') {
                    await expect(this.page.getByRole('table', {name: 'Details'})).toContainText(tabConfigFile.fields[i]);
                } else {
                    await expect(this.page.getByRole('table', {name: 'case viewer table'})).toContainText(tabConfigFile.fields[i]);
                }
            }
        }

        const dataConfigKeys = tabConfigFile.dataKeys;
        // If 'Event History' tab, then check Next Step (Event), End State, Summary and Comment
        if (tabConfigFile.tabName === 'Event History') {
            if (nextStep === endState) {
                await expect(this.page.getByText(nextStep).nth(2)).toBeVisible();
                await expect(this.page.getByText(endState).nth(3)).toBeVisible();
            } else {
                await expect(this.page.getByRole('cell', {name: nextStep, exact: true}).locator('span')).toBeVisible();
                await expect(this.page.getByRole('cell', {name: endState, exact: true}).locator('span')).toBeVisible();
            }
            let eventSummaryPrefix = nextStep;
            eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';
            if (dataConfigKeys) {
                await expect(this.page.getByText(eventSummaryPrefix + dataConfigFile.summary)).toBeVisible();
                await expect(this.page.getByText(eventSummaryPrefix + dataConfigFile.comment)).toBeVisible();
            }
        } else if (dataConfigKeys) {
            for (let i = 0; i < tabConfigFile.dataKeys.length; i++) {
                const textCount = this.page.getByText(dataConfigFile[tabConfigFile.dataKeys[i]]);
                if (textCount > 1) {
                    await expect(this.page.getByText(dataConfigFile[tabConfigFile.dataKeys[i]], {exact: true})).toBeVisible();
                } else {
                    await expect(this.page.getByRole('table', {name: 'case viewer table'})).toContainText(dataConfigFile[tabConfigFile.dataKeys[i]]);
                }
            }
        }
    }

    async waitForUploadToBeCompleted() {
        const locs = await this.page.getByText('Cancel upload').all();
        for (let i = 0; i < locs.length; i++) {
            await expect(locs[i]).toBeDisabled();
        }
    }

    async runAccessibilityTest() {
        if (!testConfig.TestForAccessibility) {
            return;
        }
        const url = await this.page.url();
        const {page} = await this.page;

        accessibilityPage.runAccessibility(url, page);
    }
};
